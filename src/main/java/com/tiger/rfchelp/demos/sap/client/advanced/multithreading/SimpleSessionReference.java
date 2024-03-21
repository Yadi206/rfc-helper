package com.tiger.rfchelp.demos.sap.client.advanced.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

import com.sap.conn.jco.ext.JCoSessionReference;

public class SimpleSessionReference implements JCoSessionReference
{
    static AtomicInteger atomicInt=new AtomicInteger(0);
    private String id="session-"+String.valueOf(atomicInt.addAndGet(1));

    @Override
    public void contextFinished()
    {
    }

    @Override
    public void contextStarted()
    {
    }

    @Override
    public String getID()
    {
        // the ID needs to be unique in the process, typically sessions have some ID that guarantee this
        // here, in our simple example an AtomicInteger value as basis that is incremented for each new Session is fully sufficient
        return id;
    }
}
