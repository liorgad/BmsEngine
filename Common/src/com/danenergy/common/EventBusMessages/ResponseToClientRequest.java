package com.danenergy.common.EventBusMessages;

/**
 * Created by dev on 28/05/2017.
 */
public class ResponseToClientRequest {

    private String response;

    public ResponseToClientRequest(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
