package com.danenergy.common.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Lior Gad on 2/28/2017.
 */
public enum CommandResponse {
    RealTimeData(0x82),
    Unknown(-1);

    private int value=0;

    CommandResponse(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    private static final Map<Integer, CommandResponse> map;
    static {
        map = Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    public static CommandResponse fromInt(int value) {
        return Optional.ofNullable(map.get(value)).orElse(Unknown);
    }

}
