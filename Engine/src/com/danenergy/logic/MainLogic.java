package com.danenergy.logic;

import com.danenergy.common.EventBusMessages.*;
import com.danenergy.common.EventQueue;
import com.danenergy.common.IPlugin;
import com.danenergy.common.Configuration;
import com.danenergy.common.Data;
import com.danenergy.common.dataObjects.Battery;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.common.protocol.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.danenergy.common.protocol.CommandResponse.*;
import static com.danenergy.common.protocol.Version.Version82;


public class MainLogic {

    //logging
    //final static Logger logger = Logger.getLogger(MainLogic.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();
    final int SECOND_IN_MILLI = 1000;

    public Configuration configuration;
    Data sharedData;
    EventBus eventBus;
    EventQueue<String> fromBmsDataEvQ;
    Set<IPlugin> plugins;
    Timer clusterUpdateTimer;
    ExecutorService executorService;

    @Inject
    public MainLogic(EventBus eventBus,Configuration  conf,Data sharedData,Set<IPlugin> plugins)
    {
        this.eventBus = eventBus;
        this.configuration = conf;
        this.sharedData = sharedData;
        this.plugins = plugins;
    }

    public void start()
    {
        try {


            executorService = Executors.newFixedThreadPool(10);

            fromBmsDataEvQ = new EventQueue<>( (s) ->
            {
                logger.info("MainLogic: fromBmsDataEvQ");
                this.handleParsing(s);
            });


            eventBus.register(this);

            clusterUpdateTimer = new Timer("ClusterUpdateTimer",true);
            logger.info("MainLogic Started");

            for (IPlugin plgn : plugins) {
                plgn.Start();
            }

            eventBus.post(new StartServerManagerMessage());

            int clusterUpdateTime = configuration.getClusterUpdateTimeInSeconds();
            int clusterDelay = configuration.getClusterUpdateDelayTimeInSeconds();

            if(!configuration.isActivateSamplingTimer())
            {
                return;
            }
            clusterUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    logger.info("ClusterUpdateTimer task started at:" + new Date());
                    updateCluster();
                    logger.info("ClusterUpdateTimer task finished at:" + new Date());
                }
            }, SECOND_IN_MILLI * clusterDelay, clusterUpdateTime * SECOND_IN_MILLI);
            //}, SECOND_IN_MILLI * 10 ,5000);

        }
        catch (Exception e)
        {
            logger.error("Error main",e);
            stop();
        }

    }

    public void stop()
    {
        logger.info("MainLogic: stop");

        try {
            if (null != this.eventBus) {
                this.eventBus.unregister(this);
            }

            if (null != this.fromBmsDataEvQ) {
                this.fromBmsDataEvQ.stop();
                this.fromBmsDataEvQ.dispose();
            }

            if (null != this.plugins) {
                plugins.stream().forEach(new Consumer<IPlugin>() {
                    @Override
                    public void accept(IPlugin iPlugin) {
                        iPlugin.Stop();
                        iPlugin.Dispose();
                    }
                });
            }

            logger.info("Stopping cluster update timer");
            if (null != this.clusterUpdateTimer) {
                this.clusterUpdateTimer.cancel();
                this.clusterUpdateTimer.purge();
            }

            logger.info("Shutting down executorservice");
            executorService.shutdown();

            logger.info("MainLogic stopped");
        }
        catch(Exception e)
        {
            logger.error("Error stopping mainlogic: ",e);
        }
    }

    public void handleParsing(String message)
    {
        try
        {
            logger.info("handleParsing");
            String syncedMessage = SyncMarkers.SyncMessage(message, FrameFormat.SOI,FrameFormat.EOI);

            if(StringUtils.isEmpty(syncedMessage))
            {
                logger.warn("Synced message is empty");
                return;
            }

            FrameFormat frameFormat = GenericParser.Parse(message, FrameFormat.class);

            if(null == frameFormat)
            {
                logger.warn("FrameFormat parsing failed");
                return;
            }

            String strippedMessage = syncedMessage.substring(syncedMessage.indexOf(FrameFormat.SOI)+1,syncedMessage.indexOf(FrameFormat.EOI));

            String strippedMessageNoCRC = strippedMessage.substring(0,strippedMessage.length()-2);

            String calculatedCRC = FrameFormat.CalculateCRC(strippedMessageNoCRC);

            short calcCRC = Short.parseShort(calculatedCRC, 16);

            if(calcCRC != frameFormat.CRC)
            {
                logger.warn("Wrong CRC");
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
            logger.error("Error, handleParsing",e);
        }
    }

    public void ParseCommand(FrameFormat frameFormat)
    {
        try
        {
            switch (fromInt(frameFormat.Cmd))
            {
                case RealTimeData:
                    RealtimeData rt = GenericParser.Parse(frameFormat.Data,RealtimeData.class);

                    if(null == rt)
                    {
                        logger.warn("RealTime data parsing empty");
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
        logger.debug("MainLogic: HandleRealtimeData - parsed FrameFormat:\n" + frameFormat.getAsJson());
        logger.debug("MainLogic: HandleRealtimeData - parsed RealtimeData:\n" + realtimeData.getAsJson());

        try {
            sharedData.getCluster().setRtData(frameFormat.Address, realtimeData);

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

    @Subscribe
    public void handleClientRequest(ClientRequest request)
    {
        logger.info("MainLogic: received client request for name="+request.getName());


        getRequestFromSharedData(request);

//        ExecutorService task = Executors.newSingleThreadExecutor();
//
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

    @Subscribe
    public void handleBmsNonResponsive(BmsNonResponsive msg)
    {
        String addrAsStr = Integer.toString(msg.BmsAddress);
        Battery batt = sharedData.getBatteries().get(addrAsStr);
        batt.setStatusNum(3);
        batt.setStatus(addrAsStr);
        batt.setRtData(null);
        updateCluster();
        //this.clusterUpdateTimer.cancel();

//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.submit(() -> {
//            handleBmsSendTimeout(null);
//        });
    }

    @Subscribe
    public void handleBmsSendTimeout(BmsSendTimeout msg)
    {
        stop();
//        for (IPlugin plgn : plugins) {
//            if(plgn instanceof ServerManager)
//            {
//                plgn.Stop();
//                plgn.Start();
//                eventBus.post(new StartServerManagerMessage());
//
//                int clusterUpdateTime = configuration.getClusterUpdateTimeInSeconds();
//                int clusterDelay = configuration.getClusterUpdateDelayTimeInSeconds();
//
//                clusterUpdateTimer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        logger.info("ClusterUpdateTimer task started at:" + new Date());
//                        updateCluster();
//                        logger.info("ClusterUpdateTimer task finished at:" + new Date());
//                    }
//                }, SECOND_IN_MILLI * clusterDelay, clusterUpdateTime * SECOND_IN_MILLI);
//            }
//        }
    }

    private void getRequestFromSharedData(ClientRequest request) {
        Map<String,Object> result = new HashMap<String, Object>();
        if (request.getName().equals("cluster"))
        {
            result.put("cluster",sharedData.getCluster());
        }
        else if(request.getName().equals("all"))
        {
            result.put("batteries", sharedData.getBatteries());
            if(sharedData.getHasCluster())
            {
                result.put("cluster",sharedData.getCluster());
            }
        }
        else if(StringUtils.isNumeric(request.getName()))
        {
            result.put(request.getName(), sharedData.getBatteries().get(request.getName()));
        }
        else
        {

        }

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        ResponseToClientRequest msg = new ResponseToClientRequest(gson.toJson(result));

        logger.info("MainLogic: posting cluster :\n" + msg.getResponse());

        eventBus.post(msg);
    }

    public void updateCluster()
    {
        try {
            sharedData.getCluster().Update();

            logger.info("MainLogic: cluster updated");
        }
        catch(Exception e)
        {
            logger.error("Error updating cluster",e);
        }

    }
}
