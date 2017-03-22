package com.danenergy.Inject;

import com.danenergy.common.ICommPort;
import com.danenergy.common.IPlugin;
import com.danenergy.communications.ClientsManager;
import com.danenergy.communications.ServerManager;
import com.danenergy.configuration.Configuration;
import com.danenergy.test.PluginTest;
import com.danenergy.test.TestComm;
import com.google.inject.multibindings.Multibinder;

/**
 * Created by Lior Gad on 3/5/2017.
 */
public class MainLogicGuiceModule extends com.google.inject.AbstractModule{

    @Override
    protected void configure() {
        bind(ICommPort.class).to(TestComm.class);
        Multibinder<IPlugin> binder = Multibinder.newSetBinder(binder(),IPlugin.class);
        binder.addBinding().to(ServerManager.class);
        binder.addBinding().to(ClientsManager.class);
    }
}
