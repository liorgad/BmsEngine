package com.danenergy.communications;

import com.danenergy.EventBusMessages.IncommingBmsData;
import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.danenergy.configuration.Data;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.Command;
import com.danenergy.protocol.FrameFormat;
import com.danenergy.protocol.Version;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ServerManager implements IPlugin {

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
        System.out.println("ServerManager Started");

        timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer task started at:"+new Date());
                completeTask();
                System.out.println("Timer task finished at:"+new Date());
            }
            private void completeTask() {
                try {

                    //assuming it takes 2 secs to complete the task
                    //Thread.sleep(2000);
                    sharedData.getDefinedAddresses().stream().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String s) {

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
                                return;
                            }

                            System.out.println("Sending " + finalCmd);

                            String result = commPort.sendReceive(finalCmd);

                            System.out.println("Received " + result);

                            if(StringUtils.isEmpty(result))
                            {
                                return;
                            }

                            IncommingBmsData inData = new IncommingBmsData();
                            inData.data = result;
                            publishIncomingData(inData);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0, 10 * 1000);
        System.out.println("TimerTask started");
        //cancel after sometime
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        System.out.println("TimerTask cancelled");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void publishIncomingData(IncommingBmsData inData) {
        eventBus.post(inData);
    }

    @Override
    public void Stop() {
        System.out.println("ServerManager Stopped");
    }

    @Override
    public void Dispose() {
        System.out.println("ServerManager Dispose");
    }
}
