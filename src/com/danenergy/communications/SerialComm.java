package com.danenergy.communications;

/**
 * Created by Lior Gad on 2/26/2017.
 */
public class SerialComm implements ICommPort
{

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void initializePort(String portName) {

    }

    @Override
    public void open() {

    }

    @Override
    public String sendReceive(String data) {
        return null;
    }

    @Override
    public void sendWrite(String data) {

    }

    @Override
    public String[] getAvailablePorts() {


        return new String[0];
    }
}
