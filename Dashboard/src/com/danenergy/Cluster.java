package com.danenergy;

import com.google.api.client.util.Key;

public class Cluster {
    @Key
    public double voltage;

    @Key
    public double temperature;

    @Key
    public double current;

    @Key
    public int stateOfCharge;

    @Key
    public String status;

    @Key
    public int statusNum;

    @Key
    public int chargeState;

    @Key
    public int temperatureState;

    @Key
    public int voltageState;
}
