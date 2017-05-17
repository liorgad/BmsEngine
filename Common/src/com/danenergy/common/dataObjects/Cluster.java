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
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public class Cluster extends BatteryBase implements Serializable {

    //logging
    final static Logger logger = Logger.getLogger(Cluster.class);

    @Inject
    Configuration config;

    List<Parallel> parallelGroups;

    public Cluster(List<Parallel> parallelGroups) {
        Injector guice = Guice.createInjector(new ResourcesGuiceModule());
        guice.injectMembers(this);

        this.parallelGroups = parallelGroups;
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

        double volt = parallelGroups.stream().mapToDouble(p -> p.getVoltage()).sum();
        setVoltage(volt);

        double current = parallelGroups.stream().mapToDouble(p -> p.getCurrent()).average().getAsDouble();
        setCurrent(current);

        double temp = parallelGroups.stream().mapToDouble(p -> p.getTemperature()).max().getAsDouble();
        setTemperature(temp);

        int chargeState = parallelGroups.stream().mapToInt(b -> b.getChargeState()).reduce((c1, c2) -> c1 | c2).getAsInt();
        setChargeState(chargeState);

        int tempState = parallelGroups.stream().mapToInt(b -> b.getTemperatureState()).reduce((t1, t2) -> t1 | t2).getAsInt();
        setTemperatureState(tempState);

        int voltState = parallelGroups.stream().mapToInt(b -> b.getVoltageState()).reduce((v1, v2) -> v1 | v2).getAsInt();
        setVoltageState(voltState);

        int statusChanged = chargeState | tempState | voltState;

        double currentThreashold = config.getCurrentThreashold();

        double diff = parallelGroups.stream().mapToDouble(p -> p.getVoltage()).reduce((v1,v2) -> Math.abs(v1 - v2 )).getAsDouble();

        //double diff = Math.Abs(SeriesVm[0].Voltage - SeriesVm[1].Voltage);

        if (diff > config.getVoltageDifferenceThreshold())
        {
            setStatus("CLUSTER MIS-BALANCE");
            setStatusNum(3);
        }
        else
        {

            //var protecList = SeriesVm.Where(bvm =>
            //((bvm.ChargeState != 0) && (bvm.CurrentForeColor == System.Drawing.Color.Red)) ||
            //((bvm.VoltageState != 0) && (bvm.VoltageForeColor == System.Drawing.Color.Red)) ||
            //((bvm.TemperatureState != 0) && (bvm.TemperatureForeColor == System.Drawing.Color.Red)));

            if (statusChanged > 0)
            {
                String status = parallelGroups.stream()
                        .filter(b -> (b.getChargeState() | b.getVoltageState() | b.getTemperatureState()) > 0)
                        .map(b -> b.getStatus())
                        .reduce((stat1, stat2) -> String.format("%s,%s", stat1, stat2)).get();
                setStatus(status);

                int cStatNum = parallelGroups.stream()
                        .mapToInt(b -> CState.fromInt(b.getChargeState()).getStatus())
                        .max().getAsInt();

                int tStatNum = parallelGroups.stream()
                        .mapToInt(b -> TState.fromInt(b.getTemperatureState()).getStatus())
                        .max().getAsInt();

                int vStatNum = parallelGroups.stream()
                        .mapToInt(b -> VState.fromInt(b.getVoltageState()).getStatus())
                        .max().getAsInt();

                int maxStat = Stream.of(cStatNum, tStatNum, vStatNum).max((o1,o2) -> Math.max(o1,o2)).get();

                setStatusNum(maxStat);
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
        }

        logger.info("Cluster Updated");
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
