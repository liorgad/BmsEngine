package com.danenergy.test;

import com.danenergy.common.ICommPort;

/**
 * Created by Lior Gad on 3/5/2017.
 */
public class TestComm implements ICommPort {
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
        return  ":038252007E000000000000001DA7040EC90EC30EEE0ED500000000053D3E3E3D3D00000000000000000F00000000000000000000000000014601A402BC1D~";
    }

    @Override
    public void sendWrite(String data) {

    }

    @Override
    public String[] getAvailablePorts() {
        return new String[]{"Com1"};
    }
}
