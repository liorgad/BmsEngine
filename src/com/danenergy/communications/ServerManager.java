package com.danenergy.communications;

import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ServerManager implements IPlugin {

    EventBus eventBus;
    ICommPort commPort;

    @Inject
    public ServerManager(EventBus eventBus,ICommPort commPort) {
        this.eventBus = eventBus;
        this.commPort = commPort;

    }

    @Override
    public void Start() {
        System.out.println("ServerManager Started");
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
