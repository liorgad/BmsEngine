package com.danenergy;

import com.danenergy.common.Configuration;
import com.danenergy.common.ICommPort;
import com.danenergy.common.ResourcesGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import gnu.io.*;
import org.apache.log4j.Logger;
import static java.nio.charset.StandardCharsets.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;



public class SerialComm implements ICommPort, SerialPortEventListener{

    //logging
    final static Logger logger = Logger.getLogger(SerialComm.class);

    private final int TIMEOUT = 3000;
    private Configuration configuration;
    public CommPortIdentifier PortId = null;
    public SerialPort serialPort;
    public InputStream input;
    public OutputStream output;
    private boolean isOpen =false;

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {

        //close the serial port
        try
        {
            if (serialPort != null) {
                serialPort.close();
                input.close();
                output.close();
                isOpen = false;
                String logText = "Disconnected.";
                logger.info(logText);
            }
        }
        catch (Exception e)
        {
            String logText = "Failed to close " + serialPort.getName()
                    + "(" + e.toString() + ")";
            logger.error(logText,e);
        }
    }

    @Override
    public void dispose() {
    close();
    }

    @Override
    public void initializePort(String portName) {


        try {
            if(null == portName)
            {
                portName = this.configuration.getPortName();
            }
            Enumeration<?> enumComm2 = CommPortIdentifier.getPortIdentifiers();///Search all ports USB
            CommPortIdentifier currPortId=null;
            while (enumComm2.hasMoreElements()) {
                currPortId = (CommPortIdentifier) enumComm2.nextElement();
                if (currPortId.getName() == portName) {
                    break;
                }
            }

            if(null == currPortId )
            {
                System.out.println("Could not find port " + portName);
                System.exit(-1);
            }

            System.out.println("PortID " + currPortId.getName());

            //the method below returns an object of type CommPort
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort) currPortId.open("BmsEngine",TIMEOUT);
            System.out.println("connected to " + currPortId.getName());
            // set port parameters
//            serialPort.setSerialPortParams(9600,
//                    SerialPort.DATABITS_8,
//                    SerialPort.STOPBITS_1,
//                    SerialPort.PARITY_NONE);

            serialPort.setSerialPortParams(this.configuration.getBaudRate(),
                    this.configuration.getDataBits(),
                    this.configuration.getStopBitsType(),
                    this.configuration.getParityType());

            //serialPort.addEventListener(this);
            //serialPort.notifyOnDataAvailable(true);

            initIOStream();
        }
        catch (PortInUseException e)
        {
            String logText = portName + " is in use. (" + e.toString() + ")";

            logger.error(logText,e);
        }
        catch (Exception e)
        {
            String logText = "Failed to open " + portName + "(" + e.toString() + ")";
            logger.error(logText,e);
        }
    }

    StringBuilder sb = new StringBuilder();
    //what happens when data is received
    //pre style="font-size: 11px;": serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();

                if (singleData != '~')
                {
                    sb.append(singleData);
                }
                else
                {
                    sb.append(singleData);

                    logger.info(sb.toString());
                }
            }
            catch (Exception e)
            {
                logger.error("Error", e);
            }
        }
    }

    //open the input and output streams
    //pre style="font-size: 11px;": an open port
    //post: initialized input and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whether opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            //writeData(0, 0);

            successful = true;
            return successful;
        }
        catch (IOException e) {
            String logText = "I/O Streams failed to open. (" + e.toString() + ")";
            logger.error(logText,e);
            return successful;
        }
    }

    //method that can be called to send data
    //pre style="font-size: 11px;": open serial port
    //post: data sent to the other device
    public void writeData(int leftThrottle, int rightThrottle)
    {
        try
        {
            output.write(leftThrottle);
            output.flush();
        }
        catch (Exception e)
        {
            String logText = "Failed to write data. (" + e.toString() + ")";
            logger.error(logText,e);
        }
    }

    @Override
    public void open() {
        try {
            char ch = 1;
            output.write(ch);
            writeData(0, 0);
            isOpen = true;
        } catch (Exception e) {
            isOpen = false;
            System.err.println(e.toString());
        }
    }

    public String read()
    {
        try
        {
            byte[] buffer = new byte[250];
            int bytesRead  = input.read(buffer);
            String result = new String(buffer,0,bytesRead);

            logger.info("Read from serial : "+result);
            return  result;
        }
        catch (Exception e)
        {
            String logText = "Failed to read data. (" + e.toString() + ")";
            logger.error(logText,e);
        }

        return null;
    }



    @Override
    public String sendReceive(String data) {
        try {
            if(isOpen)
            {
                sendWrite(data);
                Thread.sleep(this.configuration.getWaitTimePeriodBetweenCommandSendMilliSec());
                return read();
            }
        }
        catch (Exception e)
        {
            logger.error("sendReceive Error",e);
        }
        return null;
    }

    @Override
    public void sendWrite(String data) {
        try
        {
            output.write(data.getBytes());
            output.flush();

            logger.info("Sent to serial : " + data);
        }
        catch(Exception e)
        {
            String logText = "Failed to read data. (" + e.toString() + ")";
            logger.error(logText,e);
        }
    }

    @Override
    public String[] getAvailablePorts() {
        Enumeration<?> enumComm = CommPortIdentifier.getPortIdentifiers();///Search all ports USB
        System.out.println("Loadding ports...");

        List<String> ports= new LinkedList<>();
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (enumComm.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) enumComm.nextElement();

            ports.add(currPortId.getName());

        }
        if (ports.size() == 0) {
            System.out.println("Could not find COM port.");
            return null;
        }
        else
        {
            for(String port : ports)
            {
                System.out.println(port);
            }
        }

        return ports.toArray(new String[0]);
    }

    @Inject
    public SerialComm(Configuration config)
    {

        this.configuration = config;

        logger.info("SerialComm loaded");
    }

//    public static final String PORT_NAMES[] = {
//            "/dev/tty.usbserial-A9007UX1", // Mac OS X
//            "/dev/ttyUSB0", // Linux
//            "COM11", // Windows
//            "","","","",""};


    public static void main(String[] args)
    {
        Injector guice = Guice.createInjector(new ResourcesGuiceModule());
        SerialComm serialComm = guice.getInstance(SerialComm.class);
        String[] strs = serialComm.getAvailablePorts();

        serialComm.initializePort(null);

        serialComm.open();


        String cmd = ":050252000EFC~";

        //System.out.println("Enter command to bms:");

        try {
            //byte[] buffer = new byte[250];
            //System.in.read(buffer);

            String asciiCmd = new String(cmd.getBytes(US_ASCII),US_ASCII);

            serialComm.writeData(0, 0);

            //
            Thread.sleep(200);

            //System.out.println("Enter key to send again");
            //System.in.read();

            serialComm.sendReceive(asciiCmd);
            //serialComm.sendWrite(cmd);

            serialComm.sendReceive(asciiCmd);

            serialComm.sendReceive(asciiCmd);

            System.in.read();

        }
        catch (Exception e)
        {
            logger.error("Error",e);
        }



        serialComm.close();
    }
}
