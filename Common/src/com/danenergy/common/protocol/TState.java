package com.danenergy.common.protocol;

import com.danenergy.common.Utilities;

import java.util.*;
import java.util.stream.Collectors;


public enum TState {
    TCELL_NORMAL(0,"normal temperature",1),

    TCELL_CSGH(1,"charge high temperature",3),//uint16_t TCELL_CSGH:1; //charge high temperature

    TCELL_CSGL(2,"charge low temperature",3),//uint16_t TCELL_CSGL:1; //charge low temperature

    TCELL_DSGH (4,"discharge high temperature",3),//uint16_t TCELL_DSGH:1; //discharge high temperature

    TCELL_DSGL(8,"discharge low temperature",3),//uint16_t TCELL_DSGL:1; //discharge low temperature

    TENV_H(16,"environment high temperature",3),//uint16_t TENV_H:1; //environment high temperature

    TENV_L(32,"environment low temperature",3),//uint16_t TENV_L:1; //environment low temperature

    TFET_H(64,"power high temperature",3),//uint16_t TFET_H:1; //power high temperature

    TFET_L(128,"power low temperature",3),//uint16_t TFET_L:1; //power low temperature

    wTCELL_H(256,"battery cell high temperature warning",1),//uint16_t wTCELL_H:1; //battery cell high temperature warning

    wTCELL_L(512,"battery cell low temperature warning",1),//uint16_t wTCELL_L:1; // battery cell low temperature warning

    wTENV_H(1024,"environment high temperature warning",1),//uint16_t wTENV_H:1; // environment high temperature warning

    wTENV_L(2048,"environment low temperature warning",1),//uint16_t wTENV_L:1; // environment low temperature warning

    wTFET_H(4096,"power high temperature warning",1),//uint16_t wTFET_H:1; // power high temperature warning

    wTFET_L(8192,"power low temperature warning",1),//uint16_t wTFET_L:1; // power low temperature warning

    Unknown(-1,"Unknown",1);

    private int value=0;
    private String description;
    private int status;

    TState(int value,String desc,int stat)
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

    private static final Map<Integer, TState> map;
    static {
        map = Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    public static TState fromInt(int value) {
        return Optional.ofNullable(map.get(value)).orElse(Unknown);
    }

    public static List<TState> getStates(int value)
    {
        List<TState> states = new LinkedList<>();
        TState state = fromInt(value);
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
