package com.danenergy.communications;

import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ClientsManager implements IPlugin{

    EventBus eventBus;
    Map<String,ICommPort> clientsMap;

    @Inject
    public ClientsManager(EventBus eventBus) {

        this.eventBus = eventBus;
        this.clientsMap = new HashMap<>();
    }

    @Override
    public void Start() {
        System.out.println("ClientManager started");
    }

    @Override
    public void Stop() {
        System.out.println("ClientManager stopped");
    }

    @Override
    public void Dispose() {
        System.out.println("ClientManager disposeed");

    }

    public void AddClient(String clientUid,ICommPort commPort){
        if(null==clientsMap )
        {
            throw new NullPointerException("clients map null");
        }
        if(!clientsMap.containsKey(clientUid))
        {
            clientsMap.put(clientUid,commPort);
        }
    }

}
