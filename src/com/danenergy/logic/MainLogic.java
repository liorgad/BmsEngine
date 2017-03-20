package com.danenergy.logic;

import com.danenergy.common.IPlugin;
import com.danenergy.configuration.Configuration;
import com.danenergy.configuration.Data;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.*;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Created by Lior Gad on 2/28/2017.
 */
public class MainLogic {


    Configuration configuration;
    Data data;
    SimpleEventBus eventBus;
    Set<IPlugin> plugins;

    @Inject
    public MainLogic(SimpleEventBus eventBus,Configuration  conf,Data data,Set<IPlugin> plugins)
    {
        this.eventBus = eventBus;
        this.configuration = conf;
        this.data = data;
        this.plugins = plugins;
    }

    public void start()
    {
        System.out.println("MainLogic Started");
        for(IPlugin plgn : plugins)
        {
            plgn.Start();
        }
    }

    public void handleParsing(String message)
    {
        try
        {
            String syncedMessage = SyncMarkers.SyncMessage(message, FrameFormat.SOI,FrameFormat.EOI);

            if(StringUtils.isEmpty(syncedMessage))
            {
                return;
            }

            FrameFormat frameFormat = GenericParser.Parse(message, FrameFormat.class);

            if(null == frameFormat)
            {
                return;
            }

            String strippedMessage = syncedMessage.substring(syncedMessage.indexOf(FrameFormat.SOI)+1,syncedMessage.indexOf(FrameFormat.EOI));

            String strippedMessageNoCRC = strippedMessage.substring(0,strippedMessage.length()-2);

            String calculatedCRC = FrameFormat.CalculateCRC(strippedMessageNoCRC);

            byte calcCRC = Byte.parseByte(calculatedCRC,16);

            if(calcCRC != frameFormat.CRC)
            {
                return;
            }


            switch (Version.fromInt(frameFormat.Version))
            {
                case Version82:
                    ParseCommand(frameFormat);
                    break;
                default:
                    break;

            }


        }
        catch(Exception e)
        {
            String m = e.getMessage();
        }
    }

    public void ParseCommand(FrameFormat frameFormat)
    {
        try
        {
            switch (CommandResponse.fromInt(frameFormat.Cmd))
            {
                case RealTimeData:
                    RealtimeData rt = GenericParser.Parse(frameFormat.Data,RealtimeData.class);

                    if(null == rt)
                    {
                        return;
                    }

                    HandleRealtimeData(frameFormat,rt);
                    break;
            }
        }
        catch (Exception e)
        {

        }
    }

    public void HandleRealtimeData(FrameFormat frameFormat,RealtimeData realtimeData)
    {
        System.out.println(realtimeData.toString());
    }
}
