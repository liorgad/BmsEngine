package com.danenergy.common.protocol;

/**
 * Created by Lior Gad on 2/16/2017.
 */
public class SyncMarkers
{    
    private static StringBuilder buffer =  new StringBuilder();

    /*@ParserDefinition(Index = 0, BytesLength = 1, ASCIILength = 1)
    public static char SOI = ':';

    //[ParserDefinition(7, 1)]
    @ParserDefinition(Index = 7, ASCIILength = 1, BytesLength = 1)
    public static char EOI = '~';
*/


    public static String SyncMessage(String message,char SOI,char EOI)
    {
        int sIdx = message.indexOf(SOI);
        int eIdx = message.indexOf(EOI);
        String result = "";

        if (sIdx < 0 && eIdx < 0)
        {
            buffer.setLength(0);
            return "";
        }

        if(sIdx <0 && eIdx >=0)
        {
            buffer.append(message.substring(0, eIdx + 1));
            result = buffer.toString();
            buffer.setLength(0);
            return result;
        }

        if(eIdx < 0 && sIdx >=0)
        {
            buffer.setLength(0);
            buffer.append(message.substring(sIdx));
            return "";
        }

        if(eIdx < sIdx)
        {
            if(buffer.length() > 0)
            {
                buffer.append(message.substring(0, eIdx + 1));
                result = buffer.toString();
            }

            buffer.setLength(0);
            buffer.append(message.substring(sIdx));
            return result;
        }

        buffer.setLength(0);
        return message.substring(sIdx, eIdx - sIdx +1);
    }
}
