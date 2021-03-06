package com.danenergy.Inject;

import com.danenergy.HttpServer;
import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.danenergy.communications.ClientsManager;
import com.danenergy.communications.ServerManager;
import com.danenergy.configuration.Data;
import com.danenergy.test.TestComm;
import com.google.common.eventbus.EventBus;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Created by Lior Gad on 3/5/2017.
 */
public class MainLogicGuiceModule extends com.google.inject.AbstractModule{

    private final EventBus eventBus = new EventBus("Default EventBus");
    private final Data sharedData = Data.Load();

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });
        bind(Data.class).toInstance(sharedData);
        bind(ICommPort.class).to(TestComm.class);
        //bind(ICommPort.class).to(HttpServer.class);
        Multibinder<IPlugin> binder = Multibinder.newSetBinder(binder(),IPlugin.class);
        binder.addBinding().to(ServerManager.class);
        binder.addBinding().to(ClientsManager.class);
        binder.addBinding().to(HttpServer.class);
    }
}
