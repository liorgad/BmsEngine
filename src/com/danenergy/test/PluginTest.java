package com.danenergy.test;

import com.danenergy.common.IPlugin;

/**
 * Created by dev on 21/03/2017.
 */
public class PluginTest implements IPlugin {
    @Override
    public void Start() {
        System.out.println("PluginTest Start");
    }

    @Override
    public void Stop() {
        System.out.println("PluginTest Stop");

    }

    @Override
    public void Dispose() {
        System.out.println("PluginTest Dispose");

    }
}
