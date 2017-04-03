package com.danenergy.dataObjects;

import com.danenergy.protocol.RealtimeData;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by dev on 03/04/2017.
 */
public abstract class BatteryBase {
    double voltage;
    double temperature;
    double current;
    short stateOfCharge;
    String status;

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public short getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(short stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void Update();

    public abstract RealtimeData getRtData(short address);

    public abstract void setRtData(short address, RealtimeData rtData);

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
