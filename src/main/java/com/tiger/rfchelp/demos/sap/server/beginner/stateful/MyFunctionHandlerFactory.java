package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

import java.util.concurrent.ConcurrentHashMap;

import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerFunctionHandlerFactory;

/**
 * This class returns the respective {@link JCoServerFunctionHandler} (GET or INCREMENT) and tracks the current stateful
 * sessions with the session ID to the {@link SessionDataStorage} (which consists in this example only of an int value) in
 * {@link #statefulSessions}.
 */
public class MyFunctionHandlerFactory implements JCoServerFunctionHandlerFactory
{

    private final ConcurrentHashMap<String, SessionDataStorage> statefulSessions;

    private final ZGetCounterFunctionModule zGetCounterFM;
    private final ZIncrementCounterFunctionModule zIncrementCounterFM;

    public MyFunctionHandlerFactory()
    {
        statefulSessions=new ConcurrentHashMap<>();

        zGetCounterFM=new ZGetCounterFunctionModule(statefulSessions);
        zIncrementCounterFM=new ZIncrementCounterFunctionModule(statefulSessions);
    }

    @Override
    public JCoServerFunctionHandler getCallHandler(JCoServerContext serverCtx, String functionName)
    {
        if (functionName.equals("Z_INCREMENT_COUNTER"))
        {
            // this function needs to keep state, hence make sure that server is set to stateful mode
            addNewStatefulSession(serverCtx);
            return zIncrementCounterFM;
        }
        else if (functionName.equals("Z_GET_COUNTER"))
        {
            return zGetCounterFM;
        }
        else
        {
            return null;
        }
    }

    /**
     * mark all incoming sessions as stateful
     */
    private void addNewStatefulSession(JCoServerContext serverCtx)
    {
        if (!serverCtx.isStatefulSession())
        {
            serverCtx.setStateful(true);
            statefulSessions.putIfAbsent(serverCtx.getSessionID(), new SessionDataStorage());
        }
    }

    @Override
    public void sessionClosed(JCoServerContext serverCtx, String message, boolean error)
    {
        System.out.println("Session "+serverCtx.getSessionID()+" was closed "+(error?message:"by SAP system"));
        statefulSessions.remove(serverCtx.getSessionID());
    }
}
