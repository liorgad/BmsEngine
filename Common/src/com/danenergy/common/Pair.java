package com.danenergy.common;

/**
 * Created by Lior Gad on 2/16/2017.
 */
public class Pair<T extends Comparable<T>,K > implements Comparable<Pair<T,K>>
{
    private T key;
    private K value;

    public Pair(T key,K value)
    {
        this.key = key;
        this.value = value;
    }

    public T getKey()
    {
        return this.key;
    }

    public K getValue()
    {
        return this.value;
    }

    public void setKey(T key)
    {
        this.key = key;
    }

    public void setValue(K value)
    {
        this.value = value;
    }

    @Override
    public int compareTo(Pair<T, K> o) {
        return this.key.compareTo(o.key);
    }
}