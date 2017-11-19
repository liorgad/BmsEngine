package com.danenergy.common.dataObjects;

import com.danenergy.common.ArrayUtils;
import com.danenergy.common.protocol.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.logging.log4j.Logger;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Battery extends  BatteryBase implements Serializable{

    //logging
    //final static Logger logger = Logger.getLogger(Battery.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    @Expose
    RealtimeData rtData;

    @Expose
    short address;

    @Expose
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
        setVoltage((rtData.Vbat * 2.0) /1000.0);

        double current ;
        if (rtData.Current[1] != 0 && rtData.Current[0] != 0)
        {
            current = 0;
        }
        else
        {
            current = rtData.Current[0] == 0 ? (rtData.Current[1] * (-1)) : rtData.Current[0];
        }

        //division by 100 since the rtdata current is in milli amp * 10
        setCurrent((current/100));

        int temp =  Arrays.stream(ArrayUtils.ConvertToIntArray(rtData.Temp)).max().getAsInt();
        setTemperature(temp-40);

        setStateOfCharge(rtData.SOC);

        setChargeState(rtData.CState);
        setVoltageState(rtData.VState);
        setTemperatureState(rtData.TState);

        int statusChanged = chargeState | temperatureState | voltageState;

        calculateStatus(current,statusChanged,0);

        logger.info("Battery " + address + " Updated");
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

    @Override
    public String getAsJson() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

// 2. Java object to JSON, and assign to a String
        String jsonInString = gson.toJson(this);

        return jsonInString;
    }

    public void calculateStatus(double current, int statusChanged, double currentThreashold) {
        if (statusChanged > 0)
        {
            List<VState> vStates = VState.getStates(getVoltageState());
            List<CState> cStates = CState.getStates(getChargeState());
            List<TState> tStates = TState.getStates(getTemperatureState());

            int vStatusNum = vStates.stream().mapToInt( vs -> vs.getStatus()).max().getAsInt();
            int cStatusNum = cStates.stream().mapToInt( cs -> cs.getStatus()).max().getAsInt();
            int tStatusNum = tStates.stream().mapToInt( ts -> ts.getStatus()).max().getAsInt();

            int maxStat = Stream.of(vStatusNum,cStatusNum,tStatusNum).mapToInt(i -> i).max().getAsInt();

            setStatusNum(maxStat);

            String vStatusStr = vStates.stream().
                    map(vs -> vs.getDescription()).
                    collect(Collectors.joining(", "));

            String cStatusStr = cStates.stream().
                    map(cs -> cs.getDescription()).
                    collect(Collectors.joining(", "));

            String tStatusStr = tStates.stream().
                    map(ts -> ts.getDescription()).
                    collect(Collectors.joining(", "));

            String statusStr = String.format("%s,%s,%s,%s",Integer.toString(address),vStatusStr,cStatusStr,tStatusStr);
            setStatusDetails(statusStr);
            setStatus(Integer.toString(address));
        }
        else if (current > 0 && Math.abs(current) > currentThreashold)
        {
            setStatus("Charge status");
            setStatusNum(1);
        }
        else if (current < 0 && Math.abs(current) > currentThreashold)
        {
            setStatus("Discharge status");
            setStatusNum(1);
        }
        else
        {
            setStatus(null);
            setStatusNum(1);
        }
    }
}
