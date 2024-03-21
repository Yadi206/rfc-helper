package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

import java.util.concurrent.ConcurrentHashMap;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;

public class ZIncrementCounterFunctionModule extends StatefulFunctionModule
{

    public ZIncrementCounterFunctionModule(ConcurrentHashMap<String, SessionDataStorage> statefulSessions)
    {
        super(statefulSessions);
    }

    @Override
    public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
    {
        System.out.println("ZIncrementCounterFunctionModule: increase counter");

        final SessionDataStorage sds=statefulSessions.get(serverCtx.getSessionID());
        if (sds==null)
            throw new RuntimeException("Unable to find the session data storage for session id "+serverCtx.getSessionID());

        // synchronize writes
        synchronized (sds)
        {
            sds.setCachedValueCounter(sds.getCachedValueCounter()+1);
        }
    }
}