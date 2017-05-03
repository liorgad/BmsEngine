package com.danenergy.dataObjects;


import com.danenergy.protocol.RealtimeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

public class Cluster extends BatteryBase implements Serializable {

    List<Parallel> parallelGroups;

    public Cluster(List<Parallel> parallelGroups) {
        this.parallelGroups = parallelGroups;
    }

    @Override
    public void Update()
    {
        if(null == parallelGroups )
        {
            return;
        }

        //parallelGroups.forEach(p -> p.Update());

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
    public void setRtData(short address, RealtimeData rtData) throws NullPointerException{
        if(null == parallelGroups)
        {
            throw new NullPointerException("parallelGroups");
        }

        boolean isPresent = parallelGroups.stream().anyMatch(p -> p.getBatteriesInParallel().stream().anyMatch(b -> b.address==address));
        if(isPresent)
        {
            Parallel par = parallelGroups.stream().filter(p -> p.isPresent(address)).findFirst().get();
            par.setRtData(address, rtData);
            par.Update();
        }

    }

    public String getAsJson()
    {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

// 2. Java object to JSON, and assign to a String
        String jsonInString = gson.toJson(this);

        return jsonInString;
    }


}
