package com.danenergy.common;

import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.zip.Checksum;

/**
 * Created by dev on 12/09/2017.
 */
public class Utilities {

    static short[]  crc_tab16 = new short[256];
    static boolean crc_tab16_init = false;

    public static List extractFlags(int number)
    {
        if(number == 0)
        {
            return null;
        }

        List<Integer> result = new LinkedList<Integer>();

        int count = 0;

        while(number != 0)
        {
            if((number&1) == 1)
            {
                result.add((int)Math.pow(2,count));

            }
            count++;
            number = number >>> 1;
        }

        return  result;
    }

    private static String encryptDecrypt(String input) {

        String k = "32510ba9a7b2bba9b8005d43a304b5714cc0bb0c8a34884dd91304b8ad40b62b07df44ba6e9d8a2368e51d04e0e7b" +
                "207b70b9b8261112bacb6c866a232dfe257527dc29398f5f3251a0d47e503c66e935de81230b59b7afb5f41afa8d661cb";
        char[] key = k.toCharArray();
        //char[] key = {'K', 'C', 'Q'}; //Can be any chars, and any length array
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key[i % key.length]));
        }

        return output.toString();
    }

    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
            result += ':';
        }
        return result.substring(0,result.length()-1);
    }

    public static byte[] getByteArrayFromHexString(String str)
    {
        if (str == null || (str.length () % 2) == 1)
            throw new IllegalArgumentException ();
        final char [] chars = str.toCharArray ();
        final int len = chars.length;
        final byte [] data = new byte [len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit (chars[i], 16) << 4) + Character.digit (chars[i + 1], 16));
        }
        return data;
    }

    public static boolean CheckLicense(String key) throws Exception
    {
        Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
        List<String> hwrAddrs = new LinkedList<String>();

        while(ifs.hasMoreElements())
        {
            NetworkInterface fs = ifs.nextElement();
            byte[] macb = fs.getHardwareAddress();

            if(null != macb) {
                String s = getHexString(macb);
                hwrAddrs.add(s);
            }
        }

        //byte[] bytes = key.getBytes("UTF-8");
        //String encoded = Base64.getEncoder().encodeToString(bytes);
        byte[] decoded = Base64.getDecoder().decode(key);

        String str = new String(decoded, StandardCharsets.UTF_8);

        String result = encryptDecrypt(str);

        return hwrAddrs.contains(result);
    }

    public static void init_crc16_tab()
    {
        short CRC_POLY_16	= (short)0xA001;
        short i;
        short j;
        short crc;
        short c;

        for (i=0; i<256; i++) {

            crc = 0;
            c   = i;

            for (j=0; j<8; j++) {

                if ( ((crc ^ c) & 0x0001) == 1 ) {
                    crc =  (short)((short)(crc >>  (short)1) ^  (short)CRC_POLY_16);
                }
                else {
                    crc =  (short)(crc >> 1);
                }

                c =  (short)(c >> 1);
            }

            crc_tab16[i] = crc;
        }

        crc_tab16_init = true;

    }

    /*
 * uint16_t crc_modbus( const unsigned char *input_str, size_t num_bytes );
 *
 * The function crc_modbus() calculates the 16 bits Modbus CRC in one pass for
 * a byte string of which the beginning has been passed to the function. The
 * number of bytes to check is also a parameter.
 */

    public static short crc_modbus( byte[] bytes )
    {
        short CRC_START_MODBUS =(short)0xFFFF;
        short crc;
        short tmp;
        short short_c;


        if ( ! crc_tab16_init ) init_crc16_tab();

        crc = CRC_START_MODBUS;

        for (int a=0; a<bytes.length; a++)
        {
            short_c = (short)((short)0x00ff & (short) bytes[a]);
            tmp     =  (short)(crc       ^ short_c);
            crc     = (short)((crc >> 8) ^ crc_tab16[ tmp & 0xff ]);
        }

        return crc;

    }  /* crc_modbus */

}
