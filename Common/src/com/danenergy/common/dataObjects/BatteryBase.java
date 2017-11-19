package com.danenergy.common.dataObjects;

import com.danenergy.common.protocol.RealtimeData;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by dev on 03/04/2017.
 */
public abstract class BatteryBase {

    @Expose
    protected  double voltage;

    @Expose
    protected double temperature;

    @Expose
    protected double current;

    @Expose
    protected short stateOfCharge;

    @Expose
    protected String status;

    @Expose
    protected String statusDetails;

    @Expose
    protected int statusNum =3;

    @Expose
    protected int chargeState;

    @Expose
    protected int temperatureState;

    @Expose
    protected int voltageState;


    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int chargeState) {
        this.chargeState = chargeState;
    }

    public int getTemperatureState() {
        return temperatureState;
    }

    public void setTemperatureState(int temperatureState) {
        this.temperatureState = temperatureState;
    }

    public int getVoltageState() {
        return voltageState;
    }

    public void setVoltageState(int voltageState) {
        this.voltageState = voltageState;
    }

    public int getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(int statusNum) {
        this.statusNum = statusNum;
    }

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

    public abstract String getAsJson();

    public String getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(String statusDetails) {
        this.statusDetails = statusDetails;
    }
}
