package com.danenergy.dataObjects;

import com.danenergy.protocol.RealtimeData;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by dev on 03/04/2017.
 */
public class Parallel extends BatteryBase implements Serializable {
    //logging
    final static Logger logger = Logger.getLogger(Parallel.class);

    List<Battery> batteriesInParallel;

    public List<Battery> getBatteriesInParallel() {
        return batteriesInParallel;
    }

    public void setBatteriesInParallel(List<Battery> batteriesInParallel) {
        this.batteriesInParallel = batteriesInParallel;
    }

    public Parallel(List<Battery> batteriesInParallel) {
        this.batteriesInParallel = batteriesInParallel;
    }

    @Override
    public void Update() {

        if(null == batteriesInParallel)
        {
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

    public  boolean isPresent(short address)
    {
        return batteriesInParallel.stream().anyMatch(b -> b.address == address);
    }
}
