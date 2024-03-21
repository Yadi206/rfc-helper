package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

import java.util.concurrent.ConcurrentHashMap;

import com.sap.conn.jco.server.JCoServerFunctionHandler;

public abstract class StatefulFunctionModule implements JCoServerFunctionHandler
{

    protected final ConcurrentHashMap<String, SessionDataStorage> statefulSessions;

    public StatefulFunctionModule(ConcurrentHashMap<String, SessionDataStorage> statefulSessions)
    {
        this.statefulSessions=statefulSessions;
    }
}