package com.danenergy.common.dataObjects;

import com.danenergy.common.Configuration;
import com.danenergy.common.ResourcesGuiceModule;
import com.danenergy.common.protocol.CState;
import com.danenergy.common.protocol.RealtimeData;
import com.danenergy.common.protocol.TState;
import com.danenergy.common.protocol.VState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.logging.log4j.Logger;


import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;


public class Parallel extends BatteryBase implements Serializable {
    //logging
    //final static Logger logger = Logger.getLogger(Parallel.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    @Inject
    Configuration config;

    List<Battery> batteriesInParallel;

    public List<Battery> getBatteriesInParallel() {
        return batteriesInParallel;
    }

    public void setBatteriesInParallel(List<Battery> batteriesInParallel) {
        this.batteriesInParallel = batteriesInParallel;
    }


    public Parallel(List<Battery> batteriesInParallel) {

        Injector guice = Guice.createInjector(new ResourcesGuiceModule());
        guice.injectMembers(this);
        this.batteriesInParallel = batteriesInParallel;
    }

    @Override
    public void Update() {

        if(null == batteriesInParallel)
        {
            logger.warn("no batteries in parallel defined");
            return;
        }

        //batteriesInParallel.forEach(b -> b.Update());

        try {
            short avg = (short) batteriesInParallel.stream().mapToInt(b -> b.getStateOfCharge()).average().getAsDouble();
            setStateOfCharge(avg);

            double volt = batteriesInParallel.stream().mapToDouble(b -> b.getVoltage()).average().getAsDouble();
            setVoltage(volt);

            double sum = batteriesInParallel.stream().mapToDouble(b -> b.getCurrent()).sum();
            setCurrent(sum);

            double temp = batteriesInParallel.stream().mapToDouble(b -> b.getTemperature()).max().getAsDouble();
            setTemperature(temp);

            int chargeState = batteriesInParallel.stream().mapToInt(b -> b.getChargeState()).reduce((c1, c2) -> c1 | c2).getAsInt();
            setChargeState(chargeState);

            int tempState = batteriesInParallel.stream().mapToInt(b -> b.getTemperatureState()).reduce((t1, t2) -> t1 | t2).getAsInt();
            setTemperatureState(tempState);

            int voltState = batteriesInParallel.stream().mapToInt(b -> b.getVoltageState()).reduce((v1, v2) -> v1 | v2).getAsInt();
            setVoltageState(voltState);

            int statusChanged = chargeState | tempState | voltState;

            double currentThreashold = config.getCurrentThreashold();

            if (statusChanged > 0)
            {
                String status="";
                if(batteriesInParallel.size() > 1) {
                    Stream<Battery> batts = batteriesInParallel.stream()
                            .filter(b ->
                                    CState.fromInt(b.getChargeState()).getStatus() > 1
                                            || TState.fromInt(b.getTemperatureState()).getStatus() > 1
                                            || VState.fromInt(b.getVoltageState()).getStatus() > 1);
                    if(batts.toArray().length > 0) {
                        Stream<String> addresses = batts.mapToInt(b -> b.address)
                                .mapToObj(a -> String.format("%d", a));
                        if(addresses.count() > 1) {
                            status = addresses.reduce((addr1, addr2) -> String.format("%s,%s", addr1, addr2)).get();
                        }
                        else
                        {
                            status = addresses.findFirst().get();
                        }
                    }
                }
                setStatus(status);


                int cStatNum = batteriesInParallel.stream()
                        .mapToInt(b -> b.getStatusNum())
                        .max().getAsInt();

                int tStatNum = batteriesInParallel.stream()
                        .mapToInt(b -> b.getStatusNum())
                        .max().getAsInt();

                int vStatNum = batteriesInParallel.stream()
                        .mapToInt(b -> b.getStatusNum())
                        .max().getAsInt();

                int maxStat = Stream.of(cStatNum,tStatNum,vStatNum).mapToInt(i -> i).max().getAsInt();

                setStatusNum(maxStat);


            }
            else if (current > 0 && Math.abs(current) > currentThreashold)
            {
                setStatus("charge status");
                setStatusNum(1);
            }
            else if (current < 0 && Math.abs(current) > currentThreashold)
            {
                setStatus("discharge status");
                setStatusNum(1);
            }
            else
            {
                setStatus(null);
                setStatusNum(1);
            }

            logger.info("Batteries in parallel Updated");
        }
        catch(Exception e)
        {
            logger.error("Error updating Parallel",e);
        }
    }

    @Override
    public RealtimeData getRtData(short address) {

        boolean isPressent = isPresent(address);

        if(isPressent) {
            Battery bat = batteriesInParallel.stream().filter(b -> b.address == address).findFirst().get();
            if (null != bat) {
                return bat.getRtData();
            }
        }
        return null;
    }

    @Override
    public void setRtData(short address, RealtimeData rtData) {

        Battery bat = batteriesInParallel.stream().filter(b -> b.address == address).findFirst().get();
        if(null != bat)
        {
            bat.setRtData(rtData);
            bat.Update();
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

    public  boolean isPresent(short address)
    {
        return batteriesInParallel.stream().anyMatch(b -> b.address == address);
    }
}
