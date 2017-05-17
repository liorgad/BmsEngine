package com.danenergy.common;

import com.google.common.eventbus.EventBus;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ResourcesGuiceModule extends com.google.inject.AbstractModule {
    private final EventBus eventBus = new EventBus("Default EventBus");
    private final Data sharedData = Data.Load();
    private final Configuration config = Configuration.Load();

    @Override
    protected void configure() {

        bind(Data.class).toInstance(sharedData);
        bind(Configuration.class).toInstance(config);
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
        Multibinder<IPlugin> binder = Multibinder.newSetBinder(binder(), IPlugin.class);
    }
}
