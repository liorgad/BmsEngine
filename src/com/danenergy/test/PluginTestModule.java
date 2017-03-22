package com.danenergy.test;

import com.danenergy.common.IPlugin;
import com.google.inject.multibindings.Multibinder;

/**
 * Created by dev on 21/03/2017.
 */
public class PluginTestModule extends com.google.inject.AbstractModule {
    @Override
    protected void configure() {
        Multibinder<IPlugin> binder = Multibinder.newSetBinder(binder(),IPlugin.class);
        binder.addBinding().to(PluginTest.class);
    }
}
