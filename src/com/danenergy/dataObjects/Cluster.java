package com.danenergy.dataObjects;


import com.danenergy.protocol.RealtimeData;

import java.io.Serializable;
import java.util.List;

public class Cluster extends BatteryBase implements Serializable {

    List<Parallel> parallelGroups;

    public Cluster(List<Parallel> parallelGroups) {
        parallelGroups = parallelGroups;
    }



    @Override
    public void Update()
    {
        if(null == parallelGroups )
        {
            return;
        }

        parallelGroups.forEach(p -> p.Update());

        int soc = parallelGroups.stream().mapToInt(p -> p.getStateOfCharge()).min().getAsInt();
        setStateOfCharge((short)soc);

        double volt = parallelGroups.stream().mapToDouble(p -> p.getCurrent()).sum();
        setVoltage(volt);

        double current = parallelGroups.stream().mapToDouble(p -> p.getVoltage()).average().getAsDouble();
        setCurrent(current);

        double temp = parallelGroups.stream().mapToDouble(p -> p.getTemperature()).max().getAsDouble();
        setTemperature(temp);
    }

    @Override
    public RealtimeData getRtData(short address) {
        return parallelGroups.stream().filter(p -> p.getRtData(address) != null).findFirst().get().getRtData(address);
    }

    @Override
    public void setRtData(short address, RealtimeData rtData) {
        parallelGroups.stream().filter(p -> p.getRtData(address) != null).findFirst().get().setRtData(address,rtData);
    }


}
