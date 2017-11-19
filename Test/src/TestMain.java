
import com.danenergy.SerialComm;
import com.danenergy.common.ParserDefinition;
import com.danenergy.common.TimeoutIndicator;
import com.danenergy.common.Utilities;
import com.danenergy.common.dataObjects.Battery;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.common.protocol.CState;
import com.danenergy.common.protocol.RealtimeData;
import com.danenergy.common.protocol.TState;
import com.danenergy.common.protocol.VState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import gnu.io.SerialPort;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.rmi.CORBA.Util;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMain {

    public static class WindSensorCommand {

        @Expose
        @ParserDefinition(Index=1,BytesLength = 1)
        public byte address;

        @Expose
        @ParserDefinition(Index=2,BytesLength = 1)
        public byte functionCode;

        @Expose
        @ParserDefinition(Index=3,BytesLength = 2)
        public short startingRegAddr;

        @Expose
        @ParserDefinition(Index=4,BytesLength = 2)
        public short dataLength;

        @Expose
        @ParserDefinition(Index=5,BytesLength = 2)
        public short crc;

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this);    }

        public String Serialize()
        {
            String result = GenericParser.Build(this, WindSensorCommand.class);

            if (StringUtils.isEmpty(result))
            {
                return null;
            }
            return result;
        }

        public byte[] getBytes()
        {
            return new byte[]{(byte)address,(byte)functionCode,(byte) (byte) ((startingRegAddr & 0x0000ff00) >>> 8),
                    (byte) ((startingRegAddr & 0x000000ff)), (byte) ((dataLength & 0x0000ff00) >>> 8),
                    (byte) ((dataLength & 0x000000ff))};
        }

        public String getAsJson()
        {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

// 2. Java object to JSON, and assign to a String
            String jsonInString = gson.toJson(this);

            return jsonInString;
        }

        public void calculateCRC()
        {
            byte[] cmdBytes = getBytes();

            int crcInt = com.invertor.modbus.utils.CRC16.calc(cmdBytes);

            byte[] byteStr = new byte[2];
            byteStr[0] = (byte) ((crcInt & 0x000000ff));
            byteStr[1] = (byte) ((crcInt & 0x0000ff00) >>> 8);

            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.put(byteStr[0]);
            bb.put(byteStr[1]);
            short shortVal = bb.getShort(0);

            crc = shortVal;
        }


    }

    public static class WindSensorResponse {
        @Expose
        @ParserDefinition(Index=1,BytesLength = 1)
        public byte address;

        @Expose
        @ParserDefinition(Index=2,BytesLength = 1)
        public byte functionCode;

        @Expose
        @ParserDefinition(Index=3,BytesLength = 1)
        public byte dataLenghtInBytes;

        @Expose
        @ParserDefinition(Index=4,BytesLength = 2)
        public short data;

        @Expose
        @ParserDefinition(Index=5,BytesLength = 2)
        public short crc;

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this);    }

        public String Serialize()
        {
            String result = GenericParser.Build(this, WindSensorCommand.class);

            if (StringUtils.isEmpty(result))
            {
                return null;
            }
            return result;
        }

        public String getAsJson()
        {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

// 2. Java object to JSON, and assign to a String
            String jsonInString = gson.toJson(this);

            return jsonInString;
        }

        public byte[] getBytes()
        {
            return new byte[]{(byte)address,(byte)functionCode,(byte)dataLenghtInBytes, (byte) ((data & 0x0000ff00) >>> 8),
                    (byte) ((data & 0x000000ff))};
        }

        public short calculateCRC()
        {
            byte[] cmdBytes = getBytes();

            int crcInt = com.invertor.modbus.utils.CRC16.calc(cmdBytes);

            byte[] byteStr = new byte[2];
            byteStr[0] = (byte) ((crcInt & 0x000000ff));
            byteStr[1] = (byte) ((crcInt & 0x0000ff00) >>> 8);

            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.put(byteStr[0]);
            bb.put(byteStr[1]);
            short shortVal = bb.getShort(0);

            return shortVal;
        }

        public boolean validateCRC()
        {
            return crc == calculateCRC();
        }
    }

    public static class TmpHULiSensorResponse {
        @Expose
        @ParserDefinition(Index=1,BytesLength = 1)
        public byte address;

        @Expose
        @ParserDefinition(Index=2,BytesLength = 1)
        public byte functionCode;

        @Expose
        @ParserDefinition(Index=3,BytesLength = 1)
        public byte dataLengthInBytes;

        @Expose
        @ParserDefinition(Index=4,BytesLength = 0,RelatedFieldLength = "dataLengthInBytes")
        public byte[] data;

        @Expose
        @ParserDefinition(Index=5,BytesLength = 2)
        public short crc;

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this);    }

        public String Serialize()
        {
            String result = GenericParser.Build(this, WindSensorCommand.class);

            if (StringUtils.isEmpty(result))
            {
                return null;
            }
            return result;
        }

        public String getAsJson()
        {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

// 2. Java object to JSON, and assign to a String
            String jsonInString = gson.toJson(this);

            return jsonInString;
        }
    }

    public static class THLSensorData
    {
        @Expose
        @ParserDefinition(Index=1,BytesLength = 2)
        public short luminance;

        @Expose
        @ParserDefinition(Index=2,BytesLength = 2)
        public short temperature ;

        @Expose
        @ParserDefinition(Index=3,BytesLength = 2)
        public short humidity;

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this);    }

        public String Serialize()
        {
            String result = GenericParser.Build(this, THLSensorData.class);

            if (StringUtils.isEmpty(result))
            {
                return null;
            }
            return result;
        }

        public String getAsJson()
        {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

// 2. Java object to JSON, and assign to a String
            String jsonInString = gson.toJson(this);

            return jsonInString;
        }
    }

    public static void main(String[] args)
    {
        //testParser();
        testWindSensorResponseParsing();
    }

    public static void testWindSensorResponseParsing()
    {

        try {
            //byte[] response = new byte[] {0x02 ,0x03 ,0x06 ,0x00 ,0x2F ,0x09 ,(byte)0xC5 ,0x16 ,0x0C ,(byte)0xFD ,(byte)0x87};
            byte[] response = new byte[]{0x02,0x03,0x06,0x01,(byte)0xcc,0x09,(byte)0xe4,0x0e,0x79,(byte)0xa2,0x6d};
            TmpHULiSensorResponse wr = GenericParser.ParseFromBytes(response, TmpHULiSensorResponse.class);

            String json = wr.getAsJson();
            System.out.println(json);

            THLSensorData sd = GenericParser.ParseFromBytes(wr.data,THLSensorData.class);

            json = sd.getAsJson();
            System.out.println(json);



            Thread.sleep(1000);

        }
        catch(Exception e) {

            System.out.println(e);
        }
        finally {

        }
    }

    public static  void testParser()
    {
        SerialComm serialComm= null;
        try {
            WindSensorCommand cmd = new WindSensorCommand();
            cmd.address = (byte) 0xFA;
            cmd.functionCode = 0x03;
            cmd.startingRegAddr = 0;
            cmd.dataLength = 0x0001;
            cmd.calculateCRC();

            byte[] request = GenericParser.BuildToBytes(cmd, WindSensorCommand.class, null);

            serialComm = new SerialComm();

            String[] ports = serialComm.getAvailablePorts();
            System.out.println("1 opening port " + ports[0]);
//        serialPort.setSerialPortParams(9600,
////                    SerialPort.DATABITS_8,
////                    SerialPort.STOPBITS_1,
////                    SerialPort.PARITY_NONE);
            serialComm.initializePort(ports[0], 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialComm.open();

            for(int i=0 ; i<10 ;i++) {
                System.out.println(Utilities.getHexString(request));

                byte[] response = serialComm.sendReceive(request);

                if (null == response || response.length == 0) {
                    System.out.println("Response is empty");
                    continue;
                }
                String resultStr = com.danenergy.common.Utilities.getHexString(response);

                System.out.println(resultStr);

                WindSensorResponse wr = GenericParser.ParseFromBytes(response, WindSensorResponse.class);


                String json = wr.getAsJson();

                System.out.println(json);
                Thread.sleep(1000);
            }
        }
        catch(Exception e) {

            System.out.println(e);
        }
        finally {
            serialComm.close();
        }


        //byte[] response = new byte[] {0x01,0x03,0x}
    }

    public static void testSerialComm()
    {
        try {
            SerialComm serialComm = new SerialComm();

            String[] ports = serialComm.getAvailablePorts();
            System.out.println("1 opening port " + ports[0]);
//        serialPort.setSerialPortParams(9600,
////                    SerialPort.DATABITS_8,
////                    SerialPort.STOPBITS_1,
////                    SerialPort.PARITY_NONE);
            serialComm.initializePort(ports[0], 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialComm.open();
            //byte[] cmd = {(byte)0xAA, 0x0F, 0x11, 0x09, 0x00, 0x01, 0x00, 0x02, 0x00, 0x0B, 0x09, 0x00, 0x70, 0x17, 0x01, 0x00, 0x7F};
            //byte[] off = {(byte)0xAA, 0x0F, 0x11, 0x09, 0x00, 0x01, 0x00, 0x02, 0x00, 0x0B, 0x09, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18};

            byte[] cmd = {(byte) 0xFA, 0x03, 0x00, 0x00, 0x00, 0x01, (byte) 0x91, (byte) 0x81};

            byte[] result = serialComm.sendReceive(cmd, 250);

            String resultStr = com.danenergy.common.Utilities.getHexString(result);

            System.out.println(resultStr);

            return;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

//        List<Integer> res = Utilities.extractFlags(-1);
//
//        List<VState> result = VState.getStates(84);
//
//        result = VState.getStates(0);
//        result = VState.getStates(-1);//        String s = System.getProperty("os.name");
//
//        Gson gson = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
//                .setPrettyPrinting()
//                .create();
//
//        Battery data=null;
//
//        try {
//            JsonReader reader = new JsonReader(new FileReader("C:\\Users\\dev\\Documents\\battery.json"));
//            data = gson.fromJson(reader, Battery.class);
//
//        }
//        catch (Exception e)
//        {
//
//        }
//
//        data.calculateStatus(1.94,1|0|0,0);

//
//        SerialComm serialComm = new SerialComm();
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.submit(() -> {
//            System.out.println("1 opening port");
//            serialComm.initializePort("COM3");
//            serialComm.open();
//
//            serialComm.close();
//            System.out.println("1 port close");
//        });
//
//        ExecutorService executor2 = Executors.newSingleThreadExecutor();
//        executor2.submit(() -> {
//            try {
//                Thread.sleep(3000);
//                System.out.println("2 opening port");
//                serialComm.initializePort("COM3");
//                serialComm.open();
//
//                serialComm.close();
//                System.out.println("2 port close");
//            }
//            catch (Exception e)
//            {}
//        });
//
//        try
//        {
//            Thread.sleep(10000);
//        }
//        catch (Exception e)
//        {}
//
//
//
//        String[] arr = {"3","5","7"};
//
//        TimeoutIndicator to = new TimeoutIndicator(3);
//
//        for(int i =0; i<arr.length; i++)
//        {
//            to.addEntry((String)arr[i]);
//        }
//
//        boolean t = to.allResponded();
//
//        to.setAllResponded();
//
//        t= to.allResponded();
//
//        to.setAllNotResponded();
//
//        t= to.allResponded();
//
//        to.setResponded("5");
//
//        t= to.allResponded();




//        try {
//            String portName = "/dev/ttyUSB0";
//
//            Enumeration<?> enumComm2 = CommPortIdentifier.getPortIdentifiers();///Search all ports USB
//            CommPortIdentifier currPortId = null;
//            while (enumComm2.hasMoreElements()) {
//                currPortId = (CommPortIdentifier) enumComm2.nextElement();
//                if (currPortId.getName() == portName) {
//                    break;
//                }
//            }
//
//            if (null == currPortId) {
//                System.out.println("Could not find port " + portName);
//                System.exit(-1);
//            }
//
//            System.out.println("PortID " + currPortId.getName());
//
//            //the method below returns an object of type CommPort
//            //the CommPort object can be casted to a SerialPort object
//            SerialPort serialPort = (SerialPort) currPortId.open("BmsEngine", 500);
//            System.out.println("connected to " + currPortId.getName());
//            // set port parameters
////            serialPort.setSerialPortParams(9600,
////                    SerialPort.DATABITS_8,
////                    SerialPort.STOPBITS_1,
////                    SerialPort.PARITY_NONE);
//
//            serialPort.setSerialPortParams(9600,
//                    8,
//                    1,
//                    0);
//
//
//
//            InputStream input = serialPort.getInputStream();
//            OutputStream output = serialPort.getOutputStream();
//
//        }
//        catch (Exception e)
//        {
//            System.out.println("Exception :" + e);
//        }
    }
}
