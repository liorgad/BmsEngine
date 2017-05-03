package com.danenergy.dataObjects;

import com.danenergy.common.ArrayUtils;
import com.danenergy.protocol.RealtimeData;
import com.danenergy.protocol.Version;

import java.io.Serializable;
import java.util.Arrays;


public class Battery extends  BatteryBase implements Serializable{

    RealtimeData rtData;
    short address;
    Version version;

    public RealtimeData getRtData() {
        return rtData;
    }

    public void setRtData(RealtimeData rtData) {
        this.rtData = rtData;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public short getAddress() {
        return address;
    }

    public void setAddress(short address) {
        this.address = address;
    }

    @Override
    public void Update() {

        setVoltage((rtData.Vbat * 2) /1000);

        double current ;
        if (rtData.Current[1] != 0 && rtData.Current[0] != 0)
        {
            current = 0;
        }
        else
        {
            current = rtData.Current[0] == 0 ? (rtData.Current[1] * (-1)) : rtData.Current[0];
        }
        setCurrent(current);

        int temp =  Arrays.stream(ArrayUtils.ConvertToIntArray(rtData.Temp)).max().getAsInt();
        setTemperature(temp-40);

        setStateOfCharge(rtData.SOC);
    }

    @Override
    public RealtimeData getRtData(short address) {
        if(address == this.address)
        {
            return rtData;
        }
        return null;
    }

    @Override
    public void setRtData(short address, RealtimeData rtData) {
        if(address == this.address)
        {
            this.rtData = rtData;
        }
    }
}
