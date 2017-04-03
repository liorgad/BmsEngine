package com.danenergy.DadtaObjects;

import com.danenergy.protocol.RealtimeData;
import com.danenergy.protocol.Version;

import java.io.Serializable;


public class Battery implements Serializable{

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
}
