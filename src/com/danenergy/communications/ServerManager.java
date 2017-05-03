package com.danenergy.communications;

import com.danenergy.common.EventBusMessages.IncommingBmsData;
import com.danenergy.common.EventBusMessages.StartServerManagerMessage;
import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.danenergy.configuration.Data;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.Command;
import com.danenergy.protocol.FrameFormat;
import com.danenergy.protocol.Version;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ServerManager implements IPlugin {

    //logging
    final static Logger logger = Logger.getLogger(ServerManager.class);
    
    EventBus eventBus;
    ICommPort commPort;
    Timer timer;
    Data sharedData;

    @Inject
    public ServerManager(EventBus eventBus,ICommPort commPort,Data sharedData) {
        this.eventBus = eventBus;
        this.commPort = commPort;
        this.sharedData = sharedData;
    }

    @Override
    public void Start() {
        eventBus.register(this);
        logger.info("ServerManager Started");

        timer = new Timer(true);
    }

    public void publishIncomingData(IncommingBmsData inData) {
        eventBus.post(inData);
    }

    @Override
    public void Stop() {
        logger.info("ServerManager Stopped");
    }

    @Override
    public void Dispose() {
        timer.cancel();
        logger.info("TimerTask cancelled");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("ServerManager Dispose");
    }

    @Subscribe
    public void handleStartServerManager(StartServerManagerMessage msg)
    {
        completeTask();

//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                logger.info("Timer task started at:"+new Date());
//                completeTask();
//                logger.info("Timer task finished at:"+new Date());
//            }
//            private void completeTask() {
//                try {
//
//                    //assuming it takes 2 secs to complete the task
//                    //Thread.sleep(2000);
//                    sharedData.getDefinedAddresses().stream().forEach(new Consumer<String>() {
//                        @Override
//                        public void accept(String s) {
//
//                            FrameFormat ff = new FrameFormat();
//                            ff.Address = Short.parseShort(s);
//                            ff.Cmd = (short)Command.RealTimeData.getValue();
//                            ff.Version = (short)Version.Version82.getValue();
//                            ff.Length = ff.CalculateLength();
//
//                            String cmd = GenericParser.Build(ff,FrameFormat.class);
//
//                            String cmdSub = cmd.substring(1,cmd.length()-3);
//
//                            String crc = FrameFormat.CalculateCRC(cmdSub);
//
//                            String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;
//
//                            if(!commPort.isOpen())
//                            {
//                                return;
//                            }
//
//                            logger.info("Sending " + finalCmd);
//
//                            String result = commPort.sendReceive(finalCmd);
//
//                            logger.info("Received " + result);
//
//                            if(StringUtils.isEmpty(result))
//                            {
//                                return;
//                            }
//
//                            IncommingBmsData inData = new IncommingBmsData();
//                            inData.data = result;
//                            publishIncomingData(inData);
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }, 0, 10 * 1000);
//        logger.info("TimerTask started");


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

            FrameFormat ff = new FrameFormat();
            ff.Address = Short.parseShort("3");
            ff.Cmd = (short) Command.RealTimeData.getValue();
            ff.Version = (short) Version.Version82.getValue();
            ff.Length = ff.CalculateLength();

            String cmd = GenericParser.Build(ff, FrameFormat.class);

            String cmdSub = cmd.substring(1, cmd.length() - 3);

            String crc = FrameFormat.CalculateCRC(cmdSub);

            String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;

            if (!commPort.isOpen()) {
                return;
            }

            logger.info("Sending " + finalCmd);

            String result = commPort.sendReceive(finalCmd);

            logger.info("Received " + result);

            if (StringUtils.isEmpty(result)) {
                return;
            }

            IncommingBmsData inData = new IncommingBmsData();
            inData.data = result;
            publishIncomingData(inData);
        }
        catch (Exception e)
        {

        }
    }
}
