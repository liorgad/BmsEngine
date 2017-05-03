package com.danenergy;

import com.danenergy.common.EventQueue;
import com.danenergy.common.IPlugin;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import java.util.Set;

public class MainLogic {
    EventBus eventBus;
    EventQueue<String> fromBmsDataEvQ;
    Set<IPlugin> plugins;

    @Inject
    public MainLogic(EventBus eventBus,Set<IPlugin> plugins)
    {
        this.eventBus = eventBus;
        this.plugins = plugins;

//        fromBmsDataEvQ = new EventQueue<>( (s) ->
//        {
//            System.out.println("MainLogic: fromBmsDataEvQ");
//        });


        eventBus.register(this);
    }

    public void start()
    {
        System.out.println("Dashboard MainLogic Started");

        for(IPlugin plgn : plugins)
        {
            plgn.Start();
        }
    }

    public void stop() {
        eventBus.unregister(this);

        for(IPlugin plgn : plugins)
        {
            plgn.Stop();
        }

        if(fromBmsDataEvQ != null)
        {
            fromBmsDataEvQ.stop();
        }
    }
}
