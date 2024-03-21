package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

import java.util.concurrent.ConcurrentHashMap;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;

public class ZGetCounterFunctionModule extends StatefulFunctionModule
{

    public ZGetCounterFunctionModule(ConcurrentHashMap<String, SessionDataStorage> statefulSessions)
    {
        super(statefulSessions);
    }

    @Override
    public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
    {
        System.out.println("ZGetCounterFunctionModule: return counter");

        final SessionDataStorage sds=statefulSessions.get(serverCtx.getSessionID());
        //if there is no stateful session counter is considered to be 0
        function.getExportParameterList().setValue("GET_VALUE", sds==null?0:sds.getCachedValueCounter());
    }

}