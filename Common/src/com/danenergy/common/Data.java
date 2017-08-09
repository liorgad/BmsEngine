package com.danenergy.common;

import com.danenergy.common.dataObjects.Battery;
import com.danenergy.common.dataObjects.BatteryBase;
import com.danenergy.common.dataObjects.Cluster;
import com.danenergy.common.dataObjects.Parallel;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Data implements Serializable
{
    public  static Data data = null;
    @SerializedName("HasCluster")
    @Expose
    private Boolean hasCluster;
    @SerializedName("IsParallel")
    @Expose
    private Boolean isParallel;
    @SerializedName("DefinedAddresses")
    @Expose
    @Valid
    private List<String> definedAddresses = null;
    @SerializedName("Group1")
    @Expose
    @Valid
    private List<String> group1 = null;
    @SerializedName("Group2")
    @Expose
    @Valid
    private List<String> group2 = null;
    private final static long serialVersionUID = -748666285434818178L;


    private Cluster cluster;
    private Battery singleBattery;
    private Map<String,Battery> batteries;


    /**
     * No args constructor for use in serialization
     *
     */
    public Data() {
    }

    /**
     *
     * @param hasCluster
     * @param definedAddresses
     * @param isParallel
     * @param group1
     * @param group2
     */
    public Data(Boolean hasCluster, Boolean isParallel, List<String> definedAddresses, List<String> group1, List<String> group2) {
        super();
        this.hasCluster = hasCluster;
        this.isParallel = isParallel;
        this.definedAddresses = definedAddresses;
        this.group1 = group1;
        this.group2 = group2;
    }

    public synchronized Map<String, Battery> getBatteries() {
        return batteries;
    }

    public Boolean getHasCluster() {
        return hasCluster;
    }

    public void setHasCluster(Boolean hasCluster) {
        this.hasCluster = hasCluster;
    }

    public Data withHasCluster(Boolean hasCluster) {
        this.hasCluster = hasCluster;
        return this;
    }

    public Boolean getIsParallel() {
        return isParallel;
    }

    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
    }

    public Data withIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
        return this;
    }

    public List<String> getDefinedAddresses() {
        return definedAddresses;
    }

    public void setDefinedAddresses(List<String> definedAddresses) {
        this.definedAddresses = definedAddresses;
    }

    public Data withDefinedAddresses(List<String> definedAddresses) {
        this.definedAddresses = definedAddresses;
        return this;
    }

    public List<String> getGroup1() {
        return group1;
    }

    public void setGroup1(List<String> group1) {
        this.group1 = group1;
    }

    public Data withGroup1(List<String> group1) {
        this.group1 = group1;
        return this;
    }

    public List<String> getGroup2() {
        return group2;
    }

    public void setGroup2(List<String> group2) {
        this.group2 = group2;
    }

    public Data withGroup2(List<String> group2) {
        this.group2 = group2;
        return this;
    }

    public synchronized BatteryBase getCluster() {
        if(getHasCluster()) {
            if(batteries.values().stream().anyMatch(b -> b.getStatusNum() != 1))
            {
                int maxStat = batteries.values().stream().filter( b ->b.getStatusNum() != 1).mapToInt(b -> b.getStatusNum()).max().getAsInt();
                cluster.setStatusNum(maxStat);
                String clusterStat  = batteries.values().stream()
                        .filter( b ->b.getStatusNum() != 1)
                        .mapToInt(b -> b.getAddress())
                        .mapToObj(a -> String.format("%d",a))
                        .reduce((addr1, addr2) -> String.format("%s,%s", addr1, addr2)).get();
                cluster.setStatus(clusterStat);
            }
            return cluster;
        }
        else
        {
            return singleBattery;
        }
    }

    public Battery getBattery() {return singleBattery; }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(hasCluster).append(isParallel).append(definedAddresses).append(group1).append(group2).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Data) == false) {
            return false;
        }
        Data rhs = ((Data) other);
        return new EqualsBuilder().append(hasCluster, rhs.hasCluster).append(isParallel, rhs.isParallel).append(definedAddresses, rhs.definedAddresses).append(group1, rhs.group1).append(group2, rhs.group2).isEquals();
    }

    public static Data Load()
    {
        Gson gson = new Gson();
        BufferedReader br = null;

        if(data == null)
        {
            try
            {
                br = new BufferedReader(new FileReader("resources/data.json"));
                data = gson.fromJson(br,Data.class);
                br.close();

                if(null != data.definedAddresses && data.definedAddresses.size() > 0)
                {
                    data.batteries = new HashMap<String,Battery>(data.definedAddresses.size());
                    data.definedAddresses.stream().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            if(!data.batteries.containsKey(s))
                            {
                                Battery b = new Battery();
                                b.setAddress(Short.parseShort(s));
                                b.setStatus(s);
                                data.batteries.put(s,b);
                            }
                        }
                    });
                }

                if(data.hasCluster)
                {
                    List<Battery> bl1 = new LinkedList<>();
                    data.getGroup1().stream()
//                    map(s ->
//                    {
//                        Battery b = new Battery();
//                        b.setAddress(Short.parseShort(s));
//                        return b;
//                    }).forEach(b -> bl1.add(b));
                        .forEach(b -> bl1.add(data.batteries.get(b)));

                    List<Battery> bl2 = new LinkedList<>();
                    data.getGroup2().stream()
//                            map(s ->
//                            {
//                                Battery b = new Battery();
//                                b.setAddress(Short.parseShort(s));
//                                return b;
//                            }).forEach(b -> bl2.add(b));
                        .forEach(b -> bl2.add(data.batteries.get(b)));

                    Parallel p1 = new Parallel(bl1);
                    Parallel p2 = new Parallel(bl2);

                    List<Parallel> parallelList = new LinkedList<>();
                    parallelList.add(p1);
                    parallelList.add(p2);

                    data.cluster = new Cluster(parallelList);

                    data.cluster.setStatusNum(3);

                    String addresses = data.definedAddresses.stream().reduce( (s1,s2)-> s1+"," + s2).get();

                    data.cluster.setStatus(addresses);
                }
                else
                {
                    String batAddr = data.definedAddresses.get(0);

                    data.singleBattery = data.batteries.get(batAddr);
                }

            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

        return data;
    }

}