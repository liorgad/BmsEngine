package com.danenergy.test;

import com.danenergy.EventBusMessages.IncommingBmsData;
import com.danenergy.Inject.MainLogicGuiceModule;
import com.danenergy.common.EventQueue;
import com.danenergy.common.ICommPort;
import com.danenergy.communications.ServerManager;
import com.danenergy.configuration.Configuration;
import com.danenergy.configuration.Data;
import com.danenergy.logic.MainLogic;
import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.Command;
import com.danenergy.protocol.FrameFormat;
import com.danenergy.protocol.Version;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.corba.se.spi.activation.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Lior Gad on 2/13/2017.
 */
public class Test
{
    static final String realTimeData82 = ":038252007E000000000000001DA7040EC90EC30EEE0ED500000000053D3E3E3D3D00000000000000000F00000000000000000000000000014601A402BC1D~";
    static void TestEventQueue()
    {
        EventQueue myQ = new EventQueue<String>(str -> System.out.println(str));

        myQ.add("1");
        myQ.add("1");
        myQ.add("2");
        myQ.add("1");
        myQ.add("5");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        //TestTcpClient();

        //TestCalculateLength();

        //TestEventBus();
        
        TestInject();

//        String crc = FrameFormat.CalculateCRC("ABC");
//
//        byte t = (byte)250;
//        int t1 = 666666;
//
//        String str = String.format("%2x", (int) t1);
//        short c =(short) (t & 0xFF);
//        int[] arr = new int[] {1,2,3,4,5};
//
//        if(arr.getClass() == int[].class)
//        {
//            System.out.println("is array");
//        }
//
//        System.out.println(t);
//        System.out.println(c);
//        byte[] a = new byte[] {0,126};
//
//        int q = Integer.parseUnsignedInt("7E",16);
//
//        int res = ByteBuffer.wrap(a).getShort();
//
//        byte[] data = DatatypeConverter.parseHexBinary(realTimeData82.substring(1,realTimeData82.length()-1));
//
////        Object o = GenericParser.Parse(data,FrameFormat.class,2);
//
//        FrameFormat frameFormat = (FrameFormat)GenericParser.Parse(realTimeData82, FrameFormat.class);
//
//        String ff = GenericParser.Build(frameFormat,FrameFormat.class);
//
//        RealtimeData rt= (RealtimeData)GenericParser.Parse(frameFormat.Data,RealtimeData.class);

    }

    private static void TestEventBus() {


        EventBus eb = new EventBus();
        ICommPort cp = new TestComm();
        ServerManager sm = new ServerManager(eb,cp);
        MainLogic ml = new MainLogic(eb,null, Data.Load(),null);

        IncommingBmsData in = new IncommingBmsData();
        in.data = "TEST";

        sm.publishIncomingData(in);

        try {
            System.in.read();
        }
        catch (Exception e)
        {

        }
    }

    private static void TestCalculateLength() {
        FrameFormat ff = new FrameFormat();
        ff.Address = Short.parseShort("5");
        ff.Cmd = (short) Command.RealTimeData.getValue();
        ff.Version = (short) Version.Version82.getValue();

        ff.Length = ff.CalculateLength();

        String cmd = GenericParser.Build(ff, FrameFormat.class);

        String cmdSub = cmd.substring(1,cmd.length()-3);

        String crc = FrameFormat.CalculateCRC(cmdSub);

        String finalCmd = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;
    }

    private static void TestTcpClient() {
        ICommPort comm = new TestComm();

        comm.sendReceive(":050252000E00~");
    }

    private static void TestInject() {
        Injector guice = Guice.createInjector(new MainLogicGuiceModule());
        MainLogic logic = guice.getInstance(MainLogic.class);
        logic.start();

        //logic.handleParsing(realTimeData82);

        try
        {
            System.in.read();
        }
        catch (Exception e)
        {

        }
    }

    private static void TestGson() {
        Gson gson = new Gson();
        BufferedReader br = null;
        Configuration conf = null;

        try
        {
            br = new BufferedReader(new FileReader("C:\\Users\\Lior Gad\\IdeaProjects\\BmsEngine\\src\\com\\danenergy\\test\\configuration.json"));
            conf = gson.fromJson(br,Configuration.class);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
