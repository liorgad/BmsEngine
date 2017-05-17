package com.danenergy.common.protocol;

import com.danenergy.common.Pair;
import com.danenergy.common.ParserDefinition;
import com.danenergy.common.parser.GenericParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lior Gad on 2/13/2017.
 */

public class FrameFormat {

    @Expose
    @ParserDefinition(Index = 0, BytesLength = 1, ASCIILength = 1)
    public static char SOI = ':';

    @Expose
    @ParserDefinition(Index = 1, ASCIILength = 2, BytesLength = 1)
    public short Address;

    @Expose
    @ParserDefinition(Index = 2, ASCIILength = 2, BytesLength = 1)
    public short Cmd;

    @Expose
    @ParserDefinition(Index = 3, ASCIILength = 2, BytesLength = 1)
    public short Version;

    @Expose
    @ParserDefinition(Index = 4, ASCIILength = 4, BytesLength = 2)
    public int Length;

    @Expose
    //[ParserDefinition(5, -1, "Length")]
    @ParserDefinition(Index = 5, RelatedFieldLength = "Length")
    public String Data;

    @Expose
    //[ParserDefinition(6, 2)]
    @ParserDefinition(Index = 6, ASCIILength = 2, BytesLength = 1)
    public short CRC;

    @Expose
    //[ParserDefinition(7, 1)]
    @ParserDefinition(Index = 7, ASCIILength = 1, BytesLength = 1)
    public static char EOI = '~';

    public FrameFormat() {
    }

    public static String CalculateCRC(String str)
    {
        //crc cala verification method (C language)
        // i = length of string

        char[] strAsChars = str.toCharArray();

        byte sum = 0;

        for (int i = 0; i < strAsChars.length; i++)
        {
            sum += (byte)strAsChars[i];
        }

        return String.format("%02X",(sum ^= 0xFF));
    }

    public int CalculateLength()
    {
        try
        {
            List<Pair<String, ParserDefinition>> orderedProperties = GenericParser.ExtractAndOrderFields(this.getClass());

            if (null == orderedProperties)
            {
                return -1;
            }

            int totalKnownLength = orderedProperties.stream().
                    filter(p -> p.getValue().ASCIILength() != 0).
                    mapToInt(p -> p.getValue().ASCIILength()).sum();


            int totalLength = 0;

            for (Pair<String, ParserDefinition> item : orderedProperties)
            {

                String lengthProp = item.getValue().RelatedFieldLength();


                int length = item.getValue().ASCIILength();

                Field objectField = this.getClass().getDeclaredField(item.getKey());

                if (StringUtils.isNotEmpty(lengthProp))
                {
                    if (length == -1)
                    {
                        if(objectField.getType().isArray())
                        //if (item.getValue().PropertyType.IsArray)
                        {
                            ArrayList arr = (ArrayList)objectField.get(this);
                            //Array arr = (Array)item.GetValue(this);
                            totalLength += arr.size();
                        }
                        else if (objectField.getType() == String.class)
                        {
                            String str = (String)objectField.get(this);
                            if (StringUtils.isNotEmpty(str))
                            {
                                totalLength += str.length();
                            }
                        }
                    }
                }
                else
                {
                    totalLength += length;
                }
            }

            return totalLength;

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return -1;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String serialize()
    {
        Length = CalculateLength();

        String tempStr = GenericParser.Build(this,FrameFormat.class);

        if (StringUtils.isEmpty(tempStr))
        {
            return null;
        }

        String cmdSub = tempStr.substring(1, tempStr.length() - 3);

        String crc = FrameFormat.CalculateCRC(cmdSub);

        String result = FrameFormat.SOI + cmdSub + crc + FrameFormat.EOI;

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








