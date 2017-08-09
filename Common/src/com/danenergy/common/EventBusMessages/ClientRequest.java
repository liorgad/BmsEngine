package com.danenergy.common.EventBusMessages;

/**
 * Created by dev on 28/05/2017.
 */
public class ClientRequest {

    private String name;

    public ClientRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
