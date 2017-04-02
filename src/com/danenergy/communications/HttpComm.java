package com.danenergy.communications;

import com.danenergy.common.ICommPort;
import com.danenergy.common.SimpleHttpClient;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * Created by Lior Gad on 3/30/2017.
 */
public class HttpComm implements ICommPort {

    SimpleHttpClient client;
    EventBus eventBus;

    @Inject
    public HttpComm(SimpleHttpClient client,EventBus eventBus)
    {
        this.client =client;
        this.eventBus = eventBus;
    }

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

        try {
            String response = client.sendGet("http://localhost:8005", "address");
            return response;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

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
