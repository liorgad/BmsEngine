package com.danenergy.common;

/**
 * Created by Lior Gad on 3/20/2017.
 */
public interface IPlugin {
    void Start();
    public void Stop();
    public void Dispose();
    public String getName();
}
