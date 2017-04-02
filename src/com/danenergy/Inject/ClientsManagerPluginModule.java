package com.danenergy.Inject;

import com.danenergy.common.IPlugin;
import com.danenergy.communications.ClientsManager;
import com.google.inject.multibindings.Multibinder;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public class ClientsManagerPluginModule extends com.google.inject.AbstractModule{

    @Override
    protected void configure() {
        Multibinder<IPlugin> plgnBinder = Multibinder.newSetBinder(binder(), IPlugin.class);
        plgnBinder.addBinding().to(ClientsManager.class);
    }
}
