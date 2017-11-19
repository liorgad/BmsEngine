package com.danenergy.common.protocol;

import com.danenergy.common.Utilities;

import javax.rmi.CORBA.Util;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dev on 07/05/2017.
 */
public enum VState {
    VNORM(0,"normal voltage",1),

    VOV(1,"single cell overvoltage",3), //uint16_t VOV:1; //single cell overvoltage

    VUV(2,"single cell undervoltage",2), //uint16_t VUV:1; //single cell undervoltage

    BVOV(4,"battery pack overvoltage",3),//uint16_t BVOV:1//battery pack overvoltage

    BVUV(8,"battery pack undervoltage",2),//uint16_t BVUV:1; //battery pack undervoltage

    wVOV(16,"single cell overvoltage warning value",1),//uint16_t wVOV:1; //single cell overvoltage warning value

    wVUV(32,"single cell undervoltage warning value",1),//uint16_t wVUV:1; //single cell undervoltage warning value

    wBVOV(64,"battery pack overvoltage warning value",1),//uint16_t wBVOV:1; //battery pack overvoltage warning value

    wBVUV (128,"battery pack undervoltage warning value",1),//uint16_t wBVUV:1; //battery pack undervoltage warning value

    VDIFF(256,"dropout voltage protection",3) ,//uint16_t VDIFF:1; //dropout voltage protection

    VBREAK(512,"disconnection",3),//uint16_t VBREAK:1; //disconnection

    CSGDIS(1024,"low voltage，prohibit charging",3),//uint16_t CSGDIS:1; //low voltage，prohibit charging

    Unknown(-1,"Unknown",1);

    private int value=0;
    private String description;
    private int status;

    VState(int value,String desc,int stat)
    {
        this.value = value;
        this.description = desc;
        this.status = stat;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
         return status;
    }

    public int getValue()
    {
        return this.value;
    }

    private static final Map<Integer, VState> map;
    static {
        map = Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    public static VState fromInt(int value) {
        return Optional.ofNullable(map.get(value)).orElse(Unknown);
    }

    public static List<VState> getStates(int value)
    {
        List<VState> states = new LinkedList<>();
        VState state = fromInt(value);
        if(state.equals(Unknown))
        {
            List<Integer> values = Utilities.extractFlags(value);
            values.forEach( v -> states.add(fromInt(v)));
        }
        else
        {
            states.add(state);
        }

        return states;
    }

}
