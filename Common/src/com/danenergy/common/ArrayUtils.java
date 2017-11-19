package com.danenergy.common;

import java.nio.ByteBuffer;

public class ArrayUtils {
    public static int[] ConvertToIntArray(short[] arr)
    {
        if(null == arr)
        {
            return null;
        }

        int[] resultArr = new int[arr.length];
        int i=0;
        for(short s : arr)
        {
            resultArr[i] = (int)s;
            i++;
        }

        return resultArr;
    }

    public static byte[] toByteArray(int value) {
        return  ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] toByteArray(short value) {
        return  ByteBuffer.allocate(2).putShort(value).array();
    }

    public static byte[] toByteArray(long value) {
        return  ByteBuffer.allocate(8).putLong(value).array();
    }

    public static byte[] toByteArray(char value) {
        return  ByteBuffer.allocate(2).putChar(value).array();
    }
}
