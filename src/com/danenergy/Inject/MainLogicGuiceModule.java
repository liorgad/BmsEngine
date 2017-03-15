package com.danenergy.Inject;

import com.danenergy.common.ICommPort;
import com.danenergy.test.TestComm;

/**
 * Created by Lior Gad on 3/5/2017.
 */
public class MainLogicGuiceModule extends com.google.inject.AbstractModule{

    @Override
    protected void configure() {
        bind(ICommPort.class).to(TestComm.class);
    }
}
