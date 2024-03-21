package com.tiger.rfchelp.demos.sap.client.beginner;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.tiger.rfchelp.demos.sap.client.advanced.multithreading.MultiThreadedStart;
import com.sap.conn.jco.ext.SessionReferenceProvider;

/**
 * This example shows a stateful call sequence. With the default implementation of the {@link SessionReferenceProvider} provided
 * by JCo, each thread is considered a session and hence stateful sequences will work as long as all calls belonging to one
 * session are executed within the same thread. If calls belonging to one session need to be executed in different threads, an
 * own implementation of {@link SessionReferenceProvider} needs to be taken into account, which is shown in the more complex
 * {@link MultiThreadedStart}.
 * 
 * Note: this example uses Z_GET_COUNTER and Z_INCREMENT_COUNTER. Most ABAP systems contain function modules GET_COUNTER and
 * INCREMENT_COUNTER that are not remote-enabled. Copy these functions to Z_GET_COUNTER and Z_INCREMENT_COUNTER (or implement as
 * wrapper) and declare them to be remote enabled. (See {@link com.tiger.rfchelp.demos.sap.server.beginner.stateful.StatefulServer}
 * for the implementation description.) It shows that the counter increases when using state and does not when executing the
 * requests in a stateless sequence.
 */
public class StatefulCalls
{

    private static JCoDestination destination;
    private static JCoFunction incrementCounter, getCounter;

    public static void main(String[] args) throws JCoException
    {
        // this example shows if stateful calls are not tagged, they are executed as
        // stateless.
        statefulCallsFail();

        // this is how stateful calls should be implemented
        statefulCallsSuccess();
    }

    public static void statefulCallsFail() throws JCoException
    {
        init();
        executeCalls(destination, incrementCounter, getCounter);
    }

    public static void statefulCallsSuccess() throws JCoException
    {
        init();

        // in order to execute a function in a stateful context in the ABAP system, the executions have to
        // be surrounded with JCoContext.begin and JCoContext.end
        // 
        JCoContext.begin(destination);
        try
        {
            executeCalls(destination, incrementCounter, getCounter);
        }
        finally
        {
            // make sure to end the stateful context when it is supposed to be over so that resources are
            // freed. In this simple example it is clear where it needs to be, in real world scenarios it 
            // is very likely more difficult to identify the best location
            JCoContext.end(destination);
        }
    }

    private static void init() throws JCoException
    {
        destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        incrementCounter=destination.getRepository().getFunction("Z_INCREMENT_COUNTER");
        getCounter=destination.getRepository().getFunction("Z_GET_COUNTER");
        if (incrementCounter==null||getCounter==null)
            throw new RuntimeException("This example cannot run without Z_INCREMENT_COUNTER and Z_GET_COUNTER functions");
    }

    private static void executeCalls(JCoDestination destination, final JCoFunction incrementCounter,
            final JCoFunction getCounter)
        throws JCoException
    {
        final int loops=5;
        for (int i=0; i<loops; i++)
            incrementCounter.execute(destination);

        getCounter.execute(destination);
        final int remoteCounter=getCounter.getExportParameterList().getInt("GET_VALUE");
        System.out.println("Remote counter value is "+(loops==remoteCounter?"correct":"wrong")+" value ["+remoteCounter+"]");
    }

}
