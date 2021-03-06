package com.danenergy.communications;

import com.danenergy.SerialComm;
import com.danenergy.common.*;
import com.danenergy.common.EventBusMessages.*;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.common.protocol.Command;
import com.danenergy.common.protocol.FrameFormat;
import com.danenergy.common.protocol.Version;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class ServerManager implements IPlugin {

    //logging
    //final static Logger logger = Logger.getLogger(ServerManager.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    //constancts
    //final static int COMMAND_TIMEOUT_SECONDS = 60;
    //final static int POLLING_TIMEOUT_MILLISEC = 5*1000;
    
    EventBus eventBus;
    ICommPort commPort;
    Timer timer;
    Data sharedData;
    Configuration config;
    //Timer commandTimeoutTimer;
    //TimeoutIndicator timeoutIndicator;
    //Semaphore pollingFinishedSignal;
    @Inject
    public ServerManager(EventBus eventBus,ICommPort commPort,Data sharedData,Configuration config) {
        this.eventBus = eventBus;
        this.commPort = commPort;
        this.sharedData = sharedData;
        this.config = config;
        //this.pollingFinishedSignal = new Semaphore(1);
    }

    //@Inject
    public ServerManager(EventBus eventBus,Data sharedData,Configuration config) {
        this.eventBus = eventBus;
        this.sharedData = sharedData;
        this.config = config;
        this.commPort = new SerialComm(this.config);
        //this.pollingFinishedSignal = new Semaphore(1);
    }

    @Override
    public void Start() {
        eventBus.register(this);
        logger.info("ServerManager Started");

        timer = new Timer(false);
        //timeoutIndicator = new TimeoutIndicator(this.sharedData.getBatteries().size());
        //this.sharedData.getBatteries().forEach((k,b) -> timeoutIndicator.addEntry(k));
    }

    public void publishIncomingData(IncommingBmsData inData) {
        eventBus.post(inData);
    }

    public void publishBmsNonresponsiceAsync(BmsNonResponsive bmsResp)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            eventBus.post(bmsResp);
        });
    }

    @Override
    public void Stop() {

        eventBus.unregister(this);
        commPort.close();
        timer.cancel();
//        commandTimeoutTimer.cancel();
//        commandTimeoutTimer.purge();
        try{
            Thread.sleep(3000);
        }
        catch (Exception e)
        {
            logger.warn("Stop Exception in sleep",e);
        }

        logger.info("ServerManager Stopped");
    }

    @Subscribe
    public void handleStopServerManager(StopServerManagerMessage msg)
    {
        this.Stop();
    }

    @Override
    public void Dispose() {
        timer.cancel();
        logger.info("TimerTask cancelled");
//        commandTimeoutTimer.cancel();
//        commandTimeoutTimer.purge();
        logger.info("commandTimeoutTimer cancelled");
        //timeoutIndicator.dispose();

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("ServerManager Dispose");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Subscribe
    public void handleStartServerManager(StartServerManagerMessage msg)
    {
        String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
        logger.info(s+"handleStartServerManager started");
        //completeTask();
        int threadWaitForEachBattery = config.getWaitTimePeriodBetweenCommandSendMilliSec();

        if(!config.isActivateSamplingTimer())
        {
            return;
        }

//        if(!commPort.isOpen())
//        {
//            commPort.initializePort(null);
//            commPort.open();
//        }
//
//        completeTask();



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
                logger.info(s+"polling timer started");
                logger.info("Timer task started at:"+new Date());
                if(!commPort.isOpen())
                {
                    commPort.initializePort(null);
                    //commPort.initializePort("",9600,8,1,0);
                    commPort.open();
                }
                completeTask();
                logger.info("Timer task finished at:"+new Date());
            }
            private void completeTask() {
                try {

                    //assuming it takes 2 secs to complete the task
                    //Thread.sleep(2000);
                    //pollingFinishedSignal.acquire();
                    sharedData.getDefinedAddresses().stream().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String s) {

                            String s1 = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
                            logger.info(s1+" polling battery " + s);

                            FrameFormat ff = new FrameFormat();
                            ff.Address = Short.parseShort(s);
                            ff.Cmd = (short)Command.RealTimeData.getValue();
                            ff.Version = (short)Version.Version82.getValue();
                            ff.Length = ff.CalculateLength();

                            String cmd = GenericParser.Build(ff,FrameFormat.class);

                            String cmdSub = cmd.substring(1,cmd.length()-3);

                            String crc = FrameFormat.CalculateCRC(cmdSub);

                            String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;

                            if(!commPort.isOpen())
                            {
                                logger.warn("CommPort is closed");
                                return;
                            }

                            logger.info("Sending " + finalCmd);

                            String result = commPort.sendReceive(finalCmd);

                            logger.info("Received " + result);

                            if(StringUtils.isEmpty(result))
                            {
                                //timeoutIndicator.setNotResponded(s);
                                handleBmsNotResponsing(s, ff);
                                return;
                            }

                            //timeoutIndicator.setResponded(s);

                            IncommingBmsData inData = new IncommingBmsData();
                            inData.data = result;
                            publishIncomingData(inData);
                            try {
                                Thread.sleep((long) threadWaitForEachBattery);
                            }
                            catch (InterruptedException e)
                            {
                                logger.debug("Sampling thread interrupted",e);
                            }
                        }
                    });
//                    if(timeoutIndicator.allResponded())
//                    {
//                        pollingFinishedSignal.release();
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0, config.getSamplingTimerIntervalMilisec().intValue());
        logger.info("TimerTask started");




        //cancel after sometime
//        try {
//            Thread.sleep(120000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        timer.cancel();
//        logger.info("TimerTask cancelled");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void completeTask() {
        try {

            //assuming it takes 2 secs to complete the task
            //Thread.sleep(2000);
            //pollingFinishedSignal.acquire();
            sharedData.getDefinedAddresses().stream().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {

                    String s1 = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
                    logger.info(s1+" polling battery " + s);

                    FrameFormat ff = new FrameFormat();
                    ff.Address = Short.parseShort(s);
                    ff.Cmd = (short)Command.RealTimeData.getValue();
                    ff.Version = (short)Version.Version82.getValue();
                    ff.Length = ff.CalculateLength();

                    String cmd = GenericParser.Build(ff,FrameFormat.class);

                    String cmdSub = cmd.substring(1,cmd.length()-3);

                    String crc = FrameFormat.CalculateCRC(cmdSub);

                    String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;

                    if(!commPort.isOpen())
                    {
                        logger.warn("CommPort is closed");
                        return;
                    }

                    logger.info("Sending " + finalCmd);

                    String result = commPort.sendReceive(finalCmd);

                    logger.info("Received " + result);

                    if(StringUtils.isEmpty(result))
                    {
                        //timeoutIndicator.setNotResponded(s);
                        handleBmsNotResponsing(s, ff);
                        return;
                    }

                    //timeoutIndicator.setResponded(s);

                    IncommingBmsData inData = new IncommingBmsData();
                    inData.data = result;
                    publishIncomingData(inData);
                    try {
                        Thread.sleep((long) config.getWaitTimePeriodBetweenCommandSendMilliSec());
                    }
                    catch (InterruptedException e)
                    {
                        logger.debug("Sampling thread interrupted",e);
                    }
                }
            });
//                    if(timeoutIndicator.allResponded())
//                    {
//                        pollingFinishedSignal.release();
//                    }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBmsNotResponsing(String address, FrameFormat ff) {
        logger.warn("Bms " + address + " not responding");
        BmsNonResponsive msg = new BmsNonResponsive();
        msg.BmsAddress = ff.Address;
        publishBmsNonresponsiceAsync(msg);
        return;
    }
}
