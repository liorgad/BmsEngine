package com.danenergy.protocol;

import com.danenergy.common.ParserDefinition;

/**
 * Created by Lior Gad on 2/22/2017.
 */

public class RealtimeData
{
    @ParserDefinition(Index =1, ASCIILength = 14, BytesLength =7)
    public long Time_t ; //expressed in seconds, to the number of seconds 1970-01-01 00:00:00 UTC 0 time zone

    @ParserDefinition(Index =2,ASCIILength = 4, BytesLength =2)
    public int Vbat ; //battery voltage output is 0.5 times of the total voltage

    @ParserDefinition(Index =3,ASCIILength = 2, BytesLength =1)
    public short VCell_num ; //Vcell[16) in mV, a String of all voltages by order in ASCII

    @ParserDefinition(Index =4,ASCIILength = 4,RelatedFieldLength = "VCell_num", BytesLength =2)
    public int[] VCell ; //Vcell[16) in mV, a String of all voltages by order in ASCII

    @ParserDefinition(Index =5,ASCIILength = 8, BytesLength =4)
    public int[] Current ; //Current[0) = CHG current[10mA)
    //Current[1) = DSG Current[10mA)
    //In ASCII

    @ParserDefinition(Index =6,ASCIILength = 2, BytesLength =1)
    public short TempNum ; //Number of temp sensors(m)

    @ParserDefinition(Index =7,ASCIILength = 2 ,RelatedFieldLength = "TempNum", BytesLength =1)
    public short[] Temp ; //In 1°C + 40°C in ASCII

    @ParserDefinition(Index =8,ASCIILength = 4, BytesLength =2)
    public int VState ;

    @ParserDefinition(Index =9,ASCIILength = 4, BytesLength =2)
    public int CState ;

    @ParserDefinition(Index =10,ASCIILength = 4, BytesLength =2)
    public int TState ;

    @ParserDefinition(Index =11,ASCIILength = 4, BytesLength =2)
    public int Alarm ;

    @ParserDefinition(Index =12,ASCIILength = 2, BytesLength =2)
    public short FETState ;

    @ParserDefinition(Index =13,ASCIILength = 4, BytesLength =2)
    public int Warn_VOV ;

    @ParserDefinition(Index =14,ASCIILength = 4, BytesLength =2)
    public int Warn_VUV ;

    @ParserDefinition(Index =15,ASCIILength = 4, BytesLength =2)
    public int Warn_VHigh ;

    @ParserDefinition(Index =16,ASCIILength = 4, BytesLength =2)
    public int Warn_VLow ;

    @ParserDefinition(Index =17,ASCIILength = 4, BytesLength =2)
    public int BalanceState ;

    @ParserDefinition(Index =18,ASCIILength = 4, BytesLength =2)
    public int DchgNum ;

    @ParserDefinition(Index =19,ASCIILength = 4, BytesLength =2)
    public int ChgNum ;

    @ParserDefinition(Index =20,ASCIILength = 2, BytesLength =2)
    public short SOC ;

    @ParserDefinition(Index =21,ASCIILength = 4, BytesLength =2)
    public int CapNow ;

    @ParserDefinition(Index =22,ASCIILength = 4, BytesLength =2)
    public int CapFull ;

    //@ParserDefinition(Index =7, 4)
    //public short WorkState ; //Status of battery:
    //                                    //uint16 CING: 1; // state of charge, charging 1
    //                                    //uint16 DING: 1; // discharging state
    //                                    //uint16 VoltH: 1; // overvoltage protection, 1, alarm
    //                                    //uint16 VoltL: 1; // Over-discharge protection
    //                                    //uint16 CurrC: 1; // charge overcurrent
    //                                    //uint16 CurrS: 1; // short-circuit protection
    //                                    //uint16 CurrD1: 1; // discharge overcurrent 1
    //                                    //uint16 CurrD2: 1; // discharge overcurrent 2
    //                                    //uint16 TempCH: 1; // charging temperature
    //                                    //uint16 TempCL: 1; // charging low
    //                                    //uint16 TempDH: 1; // discharge temperature
    //                                    //uint16 TempDL: 1; // low temperature discharge
    //                                    //uint16 DFET: 1; // DFET state 1, open
    //                                    //uint16 CFET: 1; // CFET state
    //                                    //uint16 SDFET: 1; // DFET switch, 1, open
    //                                    //uint16 SCFET: 1; // CFET switch

    @Override
    public String toString()
    {
//            String tempStr = (String) GenericParser.Build<RealtimeDataMap_V82>(this);
//
//            if (StringUtils.isEmpty(tempStr))
//            {
//                return null;
//            }
//
//            return tempStr;

        return "";
    }

}

