package com.danenergy.communications;

import com.danenergy.HttpServer;
import com.danenergy.common.IPlugin;
import com.google.common.eventbus.EventBus;
//import fi.iki.elonen.NanoHTTPD;
//import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ClientsManager implements IPlugin{
    //logging
    //final static Logger logger = Logger.getLogger(ClientsManager.class);

    EventBus eventBus;
    //Map<String,HttpServer> clientsMap;
    HttpServer server;

    @Inject
    public ClientsManager(EventBus eventBus,HttpServer server) {

        this.eventBus = eventBus;
        //this.clientsMap = new HashMap<>();
        this.server = server;
    }

    @Override
    public void Start() {
        //logger.info("ClientManager started");
        //server.Start();
    }

    @Override
    public void Stop() {
        //logger.info("ClientManager stopped");
    }

    @Override
    public void Dispose() {
        //logger.info("ClientManager disposed");

    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

//    public void AddClient(String clientUid,ICommPort commPort){
//        if(null==clientsMap )
//        {
//            throw new NullPointerException("clients map null");
//        }
//        if(!clientsMap.containsKey(clientUid))
//        {
//            clientsMap.put(clientUid,commPort);
//        }
//    }

}
