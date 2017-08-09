package com.danenergy.test;

import com.danenergy.inject.MainLogicGuiceModule;
import com.danenergy.common.EventQueue;
import com.danenergy.common.ICommPort;
import com.danenergy.common.Configuration;
import com.danenergy.common.Data;
import com.danenergy.logic.MainLogic;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.common.protocol.Command;
import com.danenergy.common.protocol.FrameFormat;
import com.danenergy.common.protocol.Version;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Lior Gad on 2/13/2017.
 */
public class Test
{
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

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
        int statusChanged = 0 | 0 | 34;
        //TestHttpClient();
        //TestTcpClient();

        //TestCalculateLength();

        //TestEventBus();
        
        //TestInject();

        //TestDataLoad();

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

    public static void TestDataLoad()
    {
        Data d = Data.Load();
    }

    private static void TestEventBus() {


//        EventBus eb = new EventBus();
//        ICommPort cp = new TestComm();
//        ServerManager sm = new ServerManager();
//        MainLogic ml = new MainLogic(eb,null, Data.Load(),null);
//
//        IncommingBmsData in = new IncommingBmsData();
//        in.data = "TEST";
//
//        sm.publishIncomingData(in);
//
//        try {
//            System.in.read();
//        }
//        catch (Exception e)
//        {
//
//        }
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
        finally {
            logic.stop();
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

    public static class BmsEngineUrl extends GenericUrl {

        public BmsEngineUrl(String encodedUrl) {
            super(encodedUrl);
        }

        @Key
        public String fields;
    }

    private static void TestHttpClient()
    {
        try {
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    });
            BmsEngineUrl url = new BmsEngineUrl("http://localhost:8080");
            url.set("name","cluster");
            //url.fields = "id,tags,title,url";
            HttpRequest request = requestFactory.buildGetRequest(url);



//            String requestBody = "{'name': 'all', 'type' : 'realtimeData' }";
//            HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString(null, requestBody));
//            request.getHeaders().setContentType("application/json");

            HttpResponse response = request.execute();

            System.out.println(response.getContent());
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    private static void TestHttpServerLogic()
    {

    }
}
