package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

public class SessionDataStorage
{

    private int cachedValueCounter;

    // add more objects here

    public int getCachedValueCounter()
    {
        return cachedValueCounter;
    }

    public void setCachedValueCounter(int cachedValueCounter)
    {
        this.cachedValueCounter=cachedValueCounter;
    }
}
