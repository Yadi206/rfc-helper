package com.tiger.rfchelp.demos.sap.server.advanced.trfc;

import java.util.Hashtable;
import java.util.Map;

import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerTIDHandler;

public class MyTIDHandler implements JCoServerTIDHandler
{
    private enum TIDState
    {
        CREATED, EXECUTED, COMMITTED, ROLLED_BACK, CONFIRMED;
    }

    private Map<String, TIDState> availableTIDs=new Hashtable<String, TIDState>();

    @Override
    public boolean checkTID(JCoServerContext serverCtx, String tid)
    {
        // This example uses a Hashtable to store status information. Normally, however,
        // you would use a database. If the DB is down throw a RuntimeException at
        // this point. JCo will then abort the tRFC and the ABAP backend will try
        // again later.

        System.out.println("TID Handler: checkTID for "+tid);
        TIDState state=availableTIDs.get(tid);
        if (state==null)
        {
            availableTIDs.put(tid, TIDState.CREATED);
            return true;
        }

        if (state==TIDState.CREATED || state==TIDState.ROLLED_BACK)
            return true;

        return false;
        // "true" means that JCo will now execute the transaction, "false" means
        // that we have already executed this transaction previously, so JCo will
        // skip the handleRequest() step and will immediately return an OK code to ABAP.
    }

    @Override
    public void commit(JCoServerContext serverCtx, String tid)
    {
        System.out.println("TID Handler: commit for "+tid);

        // react on commit, e.g. commit on the database;
        // if necessary throw a RuntimeException, if the commit was not possible
        availableTIDs.put(tid, TIDState.COMMITTED);
    }

    @Override
    public void rollback(JCoServerContext serverCtx, String tid)
    {
        System.out.println("TID Handler: rollback for "+tid);
        availableTIDs.put(tid, TIDState.ROLLED_BACK);

        // react on rollback, e.g. rollback on the database
    }

    @Override
    public void confirmTID(JCoServerContext serverCtx, String tid)
    {
        System.out.println("TID Handler: confirmTID for "+tid);

        try
        {
            // clean up the resources
        }
        // catch(Throwable t) {} //partner won't react on an exception at
        // this point
        finally
        {
            availableTIDs.remove(tid);
        }
    }

    // simple approach to set executed state  
    public void execute(JCoServerContext serverCtx)
    {
        String tid=serverCtx.getTID();
        if (tid!=null)
        {
            System.out.println("TID Handler: execute for "+tid);
            availableTIDs.put(tid, TIDState.EXECUTED);
        }
    }
}
