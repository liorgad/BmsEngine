package com.danenergy.communications;

import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.Command;
import com.danenergy.protocol.FrameFormat;
import com.danenergy.protocol.Version;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ServerManager implements IPlugin {

    EventBus eventBus;
    ICommPort commPort;


    Timer timer;

    @Inject
    public ServerManager(EventBus eventBus,ICommPort commPort) {
        this();
        this.eventBus = eventBus;
        this.commPort = commPort;

    }
    public ServerManager() {
        timer = new Timer();

    }

    @Override
    public void Start() {

        System.out.println("ServerManager Started");

        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    FrameFormat f = new FrameFormat();
                    f.Address = 1;
                    f.Cmd = (short) Command.RealTimeData.getValue();
                    f.Version = (short) Version.Version82.getValue();

                    String cmdString = GenericParser.Build(f, FrameFormat.class);

                    String subStr = cmdString.substring(1, cmdString.length() - 3);
                    String CRC = FrameFormat.CalculateCRC(subStr);
                    cmdString = cmdString.substring(0, 1) + subStr + CRC + cmdString.substring(cmdString.length() - 1, cmdString.length());

                    String result = commPort.sendReceive(cmdString);
                }
            }, 5000);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }



    }

    @Override
    public void Stop() {
        timer.cancel();
        System.out.println("ServerManager Stopped");
    }

    @Override
    public void Dispose() {
        System.out.println("ServerManager Dispose");
    }
}
