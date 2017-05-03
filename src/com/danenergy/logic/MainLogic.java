package com.danenergy.logic;

import com.danenergy.common.EventBusMessages.ClusterUpdatedMessage;
import com.danenergy.common.EventBusMessages.IncommingBmsData;
import com.danenergy.common.EventBusMessages.StartServerManagerMessage;
import com.danenergy.common.EventQueue;
import com.danenergy.common.IPlugin;
import com.danenergy.configuration.Configuration;
import com.danenergy.configuration.Data;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * Created by Lior Gad on 2/28/2017.
 */
public class MainLogic {

    //logging
    final static Logger logger = Logger.getLogger(MainLogic.class);

    Configuration configuration;
    Data sharedData;
    EventBus eventBus;
    EventQueue<String> fromBmsDataEvQ;
    Set<IPlugin> plugins;

    @Inject
    public MainLogic(EventBus eventBus,Configuration  conf,Data sharedData,Set<IPlugin> plugins)
    {
        this.eventBus = eventBus;
        this.configuration = conf;
        this.sharedData = sharedData;
        this.plugins = plugins;

        fromBmsDataEvQ = new EventQueue<>( (s) ->
        {
            logger.info("MainLogic: fromBmsDataEvQ");            
            this.handleParsing(s);
        });


        eventBus.register(this);
    }

    public void start()
    {
        logger.info("MainLogic Started");

        for(IPlugin plgn : plugins)
        {
            plgn.Start();
        }

        eventBus.post(new StartServerManagerMessage());
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
        //logger.info("MainLogic: HandleRealtimeData - parsed FrameFormat: " + frameFormat.toString());
        //logger.info("MainLogic: HandleRealtimeData - parsed RealtimeData: " + realtimeData.toString());

        try {
            sharedData.getCluster().setRtData(frameFormat.Address, realtimeData);
            sharedData.getCluster().Update();

            logger.info("MainLogic: updated cluster");


            ClusterUpdatedMessage msg = new ClusterUpdatedMessage(sharedData.getCluster().getAsJson());

            logger.info("MainLogic: posting cluster :\n" + msg.message);
            eventBus.post(msg);
        }
        catch(Exception e)
        {
            logger.error("Error in handling realtime data",e);
        }
    }

    @Subscribe
    public void HandleIncommingBmsData(IncommingBmsData data)
    {
        logger.info("MainLogic: handling " + data.data);
        fromBmsDataEvQ.add(data.data);
    }
}
