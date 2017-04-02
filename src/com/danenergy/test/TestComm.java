package com.danenergy.test;

import com.danenergy.common.ICommPort;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
        //return  ":038252007E000000000000001DA7040EC90EC30EEE0ED500000000053D3E3E3D3D00000000000000000F00000000000000000000000000014601A402BC1D~";
        try {
            String sentence = data;
            String modifiedSentence;
            //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket("192.168.10.16", 11000);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence);
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);

            clientSocket.close();

            return modifiedSentence;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public void sendWrite(String data) {

    }

    @Override
    public String[] getAvailablePorts() {
        return new String[]{"Com1"};
    }
}
