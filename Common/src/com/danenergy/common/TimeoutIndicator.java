package com.danenergy.common;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class TimeoutIndicator {

    private ConcurrentMap<String,Boolean> responseMap;

    public TimeoutIndicator(int capacity) {
        this.responseMap = new ConcurrentHashMap<>(capacity);
    }

    public void setResponded(String key)
    {
        responseMap.replace(key, true);
    }

    public void setNotResponded(String key)
    {
        responseMap.replace(key,false);
    }

    public void addEntry(String key)
    {
        responseMap.putIfAbsent(key,false);
    }

    public void removeEntry(String key)
    {
        responseMap.remove(key);
    }

    public void setAllResponded()
    {
        responseMap.forEach( (k,v) -> setResponded(k));
    }

    public void setAllNotResponded()
    {
        responseMap.forEach( (k,v) -> setNotResponded(k));
    }

    public boolean allResponded()
    {
        return responseMap.values().stream().allMatch( v-> v ? true: false);
    }

    public void dispose()
    {
        responseMap.clear();
    }
}
