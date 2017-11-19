package com.danenergy;

import com.danenergy.common.IPlugin;

/**
 * Created by dev on 26/04/2017.
 */
public class DashboardViewModel implements IPlugin{
    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }

    @Override
    public void Dispose() {

    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
