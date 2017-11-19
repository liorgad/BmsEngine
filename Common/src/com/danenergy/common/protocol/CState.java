package com.danenergy.common.protocol;

import com.danenergy.common.Utilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dev on 07/05/2017.
 */
public enum CState {
    IDLE(0,"idle status",1),

    CING(1,"charge status",1), //uint16_t CING:1; //charge status

    DING(2,"discharge status",1),//uint16_t DING:1; //discharge status

    OCCSG(4,"over-current charge",3),//uint16_t OCCSG:1; //over-current charge

    SHORT(8,"short-circuit protection",3),//uint16_t SHORT:1; //short-circuit protection

    OCDSG1(16,"over-current discharge first-grade",3),//uint16_t OCDSG1:1; //over-current discharge first-grade

    OCDSG2(32,"over-current discharge second-class",3),//uint16_t OCDSG2:1;//over-current discharge second-class

    wOCCSG(64,"charge current warning value",1),//uint16_t wOCCSG:1; //charge current warning value

    wOCDSG(128,"discharge current warning value",1),//uint16_t wOCDSG:1; //discharge current warning value

    Unknown(-1,"Unknown",1);

    private int value=0;
    private String description;
    private int status;

    CState(int value,String desc,int stat)
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

    private static final Map<Integer, CState> map;
    static {
        map = Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    public static CState fromInt(int value) {
        return Optional.ofNullable(map.get(value)).orElse(Unknown);
    }

    public static List<CState> getStates(int value)
    {
        List<CState> states = new LinkedList<>();
        CState state = fromInt(value);
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
