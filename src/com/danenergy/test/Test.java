package com.danenergy.test;

import com.danenergy.Inject.MainLogicGuiceModule;
import com.danenergy.Inject.ServerManagerPluginModule;
import com.danenergy.logic.MainLogic;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Lior Gad on 2/13/2017.
 */
public class Test
{
    public static void main(String[] args)
    {
        String realTimeData82 = ":038252007E000000000000001DA7040EC90EC30EEE0ED500000000053D3E3E3D3D00000000000000000F00000000000000000000000000014601A402BC1D~";
//        Gson gson = new Gson();
//        BufferedReader br = null;
//        Configuration conf = null;
//
//        try
//        {
//            br = new BufferedReader(new FileReader("C:\\Users\\Lior Gad\\IdeaProjects\\BmsEngine\\src\\com\\danenergy\\test\\configuration.json"));
//            conf = gson.fromJson(br,Configuration.class);
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.getMessage());
//        }

//        EventQueue myQ = new EventQueue<String>(str -> System.out.println(str));
//
//        myQ.add("1");
//        myQ.add("1");
//        myQ.add("2");
//        myQ.add("1");
//        myQ.add("5");
//
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        Injector guice = Guice.createInjector(new MainLogicGuiceModule(),new ServerManagerPluginModule());
        MainLogic logic = guice.getInstance(MainLogic.class);

        try
        {
            Executor task = Executors.newSingleThreadExecutor();

            task.execute(() -> logic.start());

            Thread.currentThread().join();

            byte[] b = new byte[256];
            System.in.read(b);
        }
        catch (Exception e)
        {

        }


        //logic.handleParsing(realTimeData82);


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
}
