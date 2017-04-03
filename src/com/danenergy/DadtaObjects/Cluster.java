package com.danenergy.DadtaObjects;


import java.io.Serializable;

public class Cluster implements Serializable {
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
}
