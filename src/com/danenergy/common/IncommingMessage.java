package com.danenergy.common;

/**
 * Created by Lior Gad on 3/29/2017.
 */
public class IncommingMessage {

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    String msg;

    public IncommingMessage(String inMsg)
    {
        this.msg = inMsg;
    }
}
