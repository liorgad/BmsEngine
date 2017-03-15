

package com.danenergy.configuration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Configuration implements Serializable
{

    @SerializedName("PortName")
    @Expose
    private String portName;
    @SerializedName("BaudRate")
    @Expose
    private Integer baudRate;
    @SerializedName("ParityType")
    @Expose
    private Integer parityType;
    @SerializedName("DataBits")
    @Expose
    private Integer dataBits;
    @SerializedName("StopBitsType")
    @Expose
    private Integer stopBitsType;
    @SerializedName("HandShakeType")
    @Expose
    private Integer handShakeType;
    @SerializedName("ReadTimeout")
    @Expose
    private Integer readTimeout;
    @SerializedName("WriteTimeout")
    @Expose
    private Integer writeTimeout;
    @SerializedName("SamplingTimerIntervalMilisec")
    @Expose
    private Double samplingTimerIntervalMilisec;
    @SerializedName("VoltageDifferenceThreshold")
    @Expose
    private Double voltageDifferenceThreshold;
    @SerializedName("WaitTimePeriodBetweenCommandSendMilliSec")
    @Expose
    private Integer waitTimePeriodBetweenCommandSendMilliSec;
    @SerializedName("CurrentThreashold")
    @Expose
    private Double currentThreashold;
    private final static long serialVersionUID = -731230855835483319L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Configuration() {
    }

    /**
     *
     * @param writeTimeout
     * @param readTimeout
     * @param waitTimePeriodBetweenCommandSendMilliSec
     * @param parityType
     * @param handShakeType
     * @param stopBitsType
     * @param dataBits
     * @param baudRate
     * @param samplingTimerIntervalMilisec
     * @param voltageDifferenceThreshold
     * @param currentThreashold
     * @param portName
     */
    public Configuration(String portName, Integer baudRate, Integer parityType, Integer dataBits, Integer stopBitsType, Integer handShakeType, Integer readTimeout, Integer writeTimeout, Double samplingTimerIntervalMilisec, Double voltageDifferenceThreshold, Integer waitTimePeriodBetweenCommandSendMilliSec, Double currentThreashold) {
        super();
        this.portName = portName;
        this.baudRate = baudRate;
        this.parityType = parityType;
        this.dataBits = dataBits;
        this.stopBitsType = stopBitsType;
        this.handShakeType = handShakeType;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.samplingTimerIntervalMilisec = samplingTimerIntervalMilisec;
        this.voltageDifferenceThreshold = voltageDifferenceThreshold;
        this.waitTimePeriodBetweenCommandSendMilliSec = waitTimePeriodBetweenCommandSendMilliSec;
        this.currentThreashold = currentThreashold;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Configuration withPortName(String portName) {
        this.portName = portName;
        return this;
    }

    public Integer getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }

    public Configuration withBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public Integer getParityType() {
        return parityType;
    }

    public void setParityType(Integer parityType) {
        this.parityType = parityType;
    }

    public Configuration withParityType(Integer parityType) {
        this.parityType = parityType;
        return this;
    }

    public Integer getDataBits() {
        return dataBits;
    }

    public void setDataBits(Integer dataBits) {
        this.dataBits = dataBits;
    }

    public Configuration withDataBits(Integer dataBits) {
        this.dataBits = dataBits;
        return this;
    }

    public Integer getStopBitsType() {
        return stopBitsType;
    }

    public void setStopBitsType(Integer stopBitsType) {
        this.stopBitsType = stopBitsType;
    }

    public Configuration withStopBitsType(Integer stopBitsType) {
        this.stopBitsType = stopBitsType;
        return this;
    }

    public Integer getHandShakeType() {
        return handShakeType;
    }

    public void setHandShakeType(Integer handShakeType) {
        this.handShakeType = handShakeType;
    }

    public Configuration withHandShakeType(Integer handShakeType) {
        this.handShakeType = handShakeType;
        return this;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Configuration withReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Configuration withWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public Double getSamplingTimerIntervalMilisec() {
        return samplingTimerIntervalMilisec;
    }

    public void setSamplingTimerIntervalMilisec(Double samplingTimerIntervalMilisec) {
        this.samplingTimerIntervalMilisec = samplingTimerIntervalMilisec;
    }

    public Configuration withSamplingTimerIntervalMilisec(Double samplingTimerIntervalMilisec) {
        this.samplingTimerIntervalMilisec = samplingTimerIntervalMilisec;
        return this;
    }

    public Double getVoltageDifferenceThreshold() {
        return voltageDifferenceThreshold;
    }

    public void setVoltageDifferenceThreshold(Double voltageDifferenceThreshold) {
        this.voltageDifferenceThreshold = voltageDifferenceThreshold;
    }

    public Configuration withVoltageDifferenceThreshold(Double voltageDifferenceThreshold) {
        this.voltageDifferenceThreshold = voltageDifferenceThreshold;
        return this;
    }

    public Integer getWaitTimePeriodBetweenCommandSendMilliSec() {
        return waitTimePeriodBetweenCommandSendMilliSec;
    }

    public void setWaitTimePeriodBetweenCommandSendMilliSec(Integer waitTimePeriodBetweenCommandSendMilliSec) {
        this.waitTimePeriodBetweenCommandSendMilliSec = waitTimePeriodBetweenCommandSendMilliSec;
    }

    public Configuration withWaitTimePeriodBetweenCommandSendMilliSec(Integer waitTimePeriodBetweenCommandSendMilliSec) {
        this.waitTimePeriodBetweenCommandSendMilliSec = waitTimePeriodBetweenCommandSendMilliSec;
        return this;
    }

    public Double getCurrentThreashold() {
        return currentThreashold;
    }

    public void setCurrentThreashold(Double currentThreashold) {
        this.currentThreashold = currentThreashold;
    }

    public Configuration withCurrentThreashold(Double currentThreashold) {
        this.currentThreashold = currentThreashold;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(portName).append(baudRate).append(parityType).append(dataBits).append(stopBitsType).append(handShakeType).append(readTimeout).append(writeTimeout).append(samplingTimerIntervalMilisec).append(voltageDifferenceThreshold).append(waitTimePeriodBetweenCommandSendMilliSec).append(currentThreashold).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Configuration) == false) {
            return false;
        }
        Configuration rhs = ((Configuration) other);
        return new EqualsBuilder().append(portName, rhs.portName).append(baudRate, rhs.baudRate).append(parityType, rhs.parityType).append(dataBits, rhs.dataBits).append(stopBitsType, rhs.stopBitsType).append(handShakeType, rhs.handShakeType).append(readTimeout, rhs.readTimeout).append(writeTimeout, rhs.writeTimeout).append(samplingTimerIntervalMilisec, rhs.samplingTimerIntervalMilisec).append(voltageDifferenceThreshold, rhs.voltageDifferenceThreshold).append(waitTimePeriodBetweenCommandSendMilliSec, rhs.waitTimePeriodBetweenCommandSendMilliSec).append(currentThreashold, rhs.currentThreashold).isEquals();
    }

}