package com.danenergy.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Lior Gad on 2/28/2017.
 */
public enum Command {
    ProtectionData(1),
    RealTimeData(2),
    DataSetting(5),
    FETOperation(6),
    Version(9),
    Unknown(-1);


    private int value = 0;

    Command(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    private static final Map<Integer, Command> map;
    static {
        map = Arrays.stream(values())
                .collect(Collectors.toMap(e -> e.value, e -> e));
    }

    public static Command fromInt(int value) {
        return Optional.ofNullable(map.get(value)).orElse(Unknown);
    }
}
