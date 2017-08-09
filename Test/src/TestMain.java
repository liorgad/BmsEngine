
import gnu.io.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.stream.IntStream;

public class TestMain {
    public static void main(String[] args)
    {
        try {
            String portName = "/dev/ttyUSB0";

            Enumeration<?> enumComm2 = CommPortIdentifier.getPortIdentifiers();///Search all ports USB
            CommPortIdentifier currPortId = null;
            while (enumComm2.hasMoreElements()) {
                currPortId = (CommPortIdentifier) enumComm2.nextElement();
                if (currPortId.getName() == portName) {
                    break;
                }
            }

            if (null == currPortId) {
                System.out.println("Could not find port " + portName);
                System.exit(-1);
            }

            System.out.println("PortID " + currPortId.getName());

            //the method below returns an object of type CommPort
            //the CommPort object can be casted to a SerialPort object
            SerialPort serialPort = (SerialPort) currPortId.open("BmsEngine", 500);
            System.out.println("connected to " + currPortId.getName());
            // set port parameters
//            serialPort.setSerialPortParams(9600,
//                    SerialPort.DATABITS_8,
//                    SerialPort.STOPBITS_1,
//                    SerialPort.PARITY_NONE);

            serialPort.setSerialPortParams(9600,
                    8,
                    1,
                    0);



            InputStream input = serialPort.getInputStream();
            OutputStream output = serialPort.getOutputStream();

        }
        catch (Exception e)
        {
            System.out.println("Exception :" + e);
        }

    }
}
