package com.danenergy.protocol;

/**
 * Created by Lior Gad on 2/13/2017.
 */

enum Command
{
    ProtectionData(1),
    RealTimeData(2),
    DataSetting(5),
    FETOperation(6),
    Version(9);


    private int value = 0;

    Command(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}

enum CommandResponse
{
    RealTimeData(0x82);

    private int value=0;

    CommandResponse(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}

enum Version
{
    Version82(82);

    private int value=0;

    Version(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}

public class FrameFormat {

    @ParserDefinition(Index = 0, BytesLength = 1, ASCIILength = 1)
    public static char SOI = ':';

    @ParserDefinition(Index = 1, ASCIILength = 2, BytesLength = 1)
    public short Address;

    @ParserDefinition(Index = 2, ASCIILength = 2, BytesLength = 1)
    public short Cmd;

    @ParserDefinition(Index = 3, ASCIILength = 2, BytesLength = 1)
    public short Version;

    @ParserDefinition(Index = 4, ASCIILength = 4, BytesLength = 2)
    public int Length;

    //[ParserDefinition(5, -1, "Length")]
    @ParserDefinition(Index = 5, RelatedFieldLength = "Length")
    public String Data;

    //[ParserDefinition(6, 2)]
    @ParserDefinition(Index = 6, ASCIILength = 2, BytesLength = 1)
    public short CRC;

    //[ParserDefinition(7, 1)]
    @ParserDefinition(Index = 7, ASCIILength = 1, BytesLength = 1)
    public static char EOI = '~';

    public FrameFormat() {
    }
}
    /*    @Override
        public String ToString()
    {
        int length = CalculateLength();

        Length = Convert.ToUInt16(length);

        string tempStr = (string)GenericParser.Build<FrameFormat>(this);

        if (string.IsNullOrWhiteSpace(tempStr))
        {
            return null;
        }

        var subStr = tempStr.TrimStart(new char[] { SOI }).TrimEnd(new char[] { EOI });

        subStr = subStr.Substring(0, subStr.Length - 2);

        var crc = CalculateCRC(subStr);

        var result = string.Format("{0}{1}{2}{3}", SOI, subStr, crc, EOI);



        return result;
    }

        public int CalculateLength()
        {
            try
            {
                var properties = this.GetType().GetProperties().Where(prop => prop.IsDefined(typeof(ParserDefinitionAttribute), false));

                if (null == properties)
                {
                    return -1;
                }

                var orderedProperties = properties.OrderBy(p => ((ParserDefinitionAttribute)p.GetCustomAttribute(typeof(ParserDefinitionAttribute))).Index);


                if (null == orderedProperties)
                {
                    return -1;
                }

                int totalLength = 0;

                foreach (var item in orderedProperties)
                {
                    var lengthProp = item.GetCustomAttribute<ParserDefinitionAttribute>().DynamicLength;
                    var length = item.GetCustomAttribute<ParserDefinitionAttribute>().Length;
                    if (!string.IsNullOrWhiteSpace(lengthProp))
                    {
                        if (length == -1)
                        {
                            if (item.PropertyType.IsArray)
                            {
                                Array arr = (Array)item.GetValue(this);
                                totalLength += arr.Length;
                            }
                            else if (item.PropertyType == typeof(string))
                            {
                                string str = (string)item.GetValue(this);
                                if (!string.IsNullOrWhiteSpace(str))
                                {
                                    totalLength += str.Length;
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
                logger.Error(e, "Error calculating length");
            }

            return -1;
        }

        public static string CalculateCRC(string str)
        {
            //crc cala verification method (C language)
            // i = length of string

            char[] strAsChars = str.ToCharArray();

            byte sum = 0;

            for (int i = 0; i < strAsChars.Length; i++)
            {
                sum += Convert.ToByte(strAsChars[i]);
            }

            return (sum ^= 0xFF).ToString("X2");
        }
        */

