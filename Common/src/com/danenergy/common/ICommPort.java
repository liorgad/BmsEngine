package com.danenergy.common;



/**
 * Created by Lior Gad on 2/26/2017.
 */
public interface ICommPort
{
    boolean isOpen();
    void close();
    void dispose();
    void initializePort(String portName);
    void initializePort(String portName,int baudrate,int databits,int stopbits,int paritybits);
    void open();
    String sendReceive(String data);
    void sendWrite(String data);
    String[] getAvailablePorts();
    void sendWrite(byte[] bytes);
    byte[] sendReceive(byte[] data);
    public byte[] sendReceive(byte[] data,int waitTimeMilliSec);
}
