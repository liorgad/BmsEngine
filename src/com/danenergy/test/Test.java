package com.danenergy.test;

import com.danenergy.parser.GenericParser;
import com.danenergy.protocol.FrameFormat;
import com.danenergy.protocol.RealtimeData;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;

/**
 * Created by Lior Gad on 2/13/2017.
 */
public class Test
{
    public static void main(String[] args)
    {

        byte t = (byte)250;
        int t1 = 666666;

        String str = String.format("%2x", (int) t1);
        short c =(short) (t & 0xFF);
        int[] arr = new int[] {1,2,3,4,5};

        if(arr.getClass() == int[].class)
        {
            System.out.println("is array");
        }

        System.out.println(t);
        System.out.println(c);

        String realTimeData82 = ":038252007E000000000000001DA7040EC90EC30EEE0ED500000000053D3E3E3D3D00000000000000000F00000000000000000000000000014601A402BC1D~";

        byte[] a = new byte[] {0,126};

        int q = Integer.parseUnsignedInt("7E",16);

        int res = ByteBuffer.wrap(a).getShort();

        byte[] data = DatatypeConverter.parseHexBinary(realTimeData82.substring(1,realTimeData82.length()-1));

//        Object o = GenericParser.Parse(data,FrameFormat.class,2);

        FrameFormat frameFormat = (FrameFormat)GenericParser.Parse(realTimeData82, FrameFormat.class);

        String ff = GenericParser.Build(frameFormat,FrameFormat.class);

        RealtimeData rt= (RealtimeData)GenericParser.Parse(frameFormat.Data,RealtimeData.class);

        int l=5;
    }
}
