package com.danenergy.configuration;

import com.danenergy.dataObjects.Battery;
import com.danenergy.dataObjects.Cluster;
import com.danenergy.dataObjects.Parallel;
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
import java.util.LinkedList;
import java.util.List;

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

    public Cluster getCluster() {return cluster;}

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

                if(data.hasCluster)
                {
                    List<Battery> bl1 = new LinkedList<>();
                    data.getGroup1().stream().
                    map(s ->
                    {
                        Battery b = new Battery();
                        b.setAddress(Short.parseShort(s));
                        return b;
                    }).forEach(b -> bl1.add(b));

                    List<Battery> bl2 = new LinkedList<>();
                    data.getGroup2().stream().
                            map(s ->
                            {
                                Battery b = new Battery();
                                b.setAddress(Short.parseShort(s));
                                return b;
                            }).forEach(b -> bl2.add(b));

                    Parallel p1 = new Parallel(bl1);
                    Parallel p2 = new Parallel(bl2);

                    List<Parallel> parallelList = new LinkedList<>();
                    parallelList.add(p1);
                    parallelList.add(p2);

                    data.cluster = new Cluster(parallelList);
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