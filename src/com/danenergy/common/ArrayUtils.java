package com.danenergy.common;

/**
 * Created by dev on 03/04/2017.
 */
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
}
