package com.tiger.rfchelp.demos.sap.server.advanced.bgrfc;

import java.util.Hashtable;
import java.util.Map;

import com.sap.conn.jco.JCoFunctionUnitState;
import com.sap.conn.jco.JCoUnitIdentifier;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerUnitIDHandler;

public class MyUnitIDHandler implements JCoServerUnitIDHandler
{
    private enum UnitIDState
    {
        CREATED, EXECUTED, COMMITTED, ROLLED_BACK, CONFIRMED;
    }

    private Map<String, UnitIDState> availableUnitIDs=new Hashtable<String, UnitIDState>();

    @Override
    public boolean checkUnitID(JCoServerContext serverCtx, JCoUnitIdentifier unitIdentifier)
    {
        // This example uses a Hashtable to store status information. Normally, however,
        // you would use a database. If the DB is down throw a RuntimeException at
        // this point. JCo will then abort the tRFC and the S4 backend will try
        // again later.

        System.out.println("Unit ID Handler: checkUnitID for "+unitIdentifier.toString());
        UnitIDState state=availableUnitIDs.get(unitIdentifier.getID());
        if (state==null)
        {
            availableUnitIDs.put(unitIdentifier.getID(), UnitIDState.CREATED);
            return true;
        }

        if (state==UnitIDState.CREATED || state==UnitIDState.ROLLED_BACK)
            return true;

        return false;
        // "true" means that JCo will now execute the transaction, "false" means
        // that we have already executed this transaction previously, so JCo will
        // skip the handleRequest() step and will immediately return an OK code to S4.
    }

    @Override
    public void commit(JCoServerContext serverCtx, JCoUnitIdentifier unitIdentifier)
    {
        System.out.println("Unit ID Handler: commit for "+unitIdentifier);

        // react on commit, e.g. commit on the database;
        // if necessary throw a RuntimeException, if the commit was not possible
        availableUnitIDs.put(unitIdentifier.getID(), UnitIDState.COMMITTED);
    }

    @Override
    public void rollback(JCoServerContext serverCtx, JCoUnitIdentifier unitIdentifier)
    {
        System.out.println("Unit ID Handler: rollback for "+unitIdentifier);
        availableUnitIDs.put(unitIdentifier.getID(), UnitIDState.ROLLED_BACK);

        // react on rollback, e.g. rollback on the database
    }

    @Override
    public void confirmUnitID(JCoServerContext serverCtx, JCoUnitIdentifier unitIdentifier)
    {
        System.out.println("Unit ID Handler: confirmTID for "+unitIdentifier);

        try
        {
            // clean up the resources
        }
        // catch(Throwable t) {} //partner won't react on an exception at
        // this point
        finally
        {
            availableUnitIDs.remove(unitIdentifier.getID());
        }
    }

    // simple approach to set executed state
    public void execute(JCoServerContext serverCtx)
    {
        JCoUnitIdentifier unitIdentifier=serverCtx.getUnitIdentifier();
        if (unitIdentifier!=null)
        {
            System.out.println("Unit ID Handler: execute for "+unitIdentifier);
            availableUnitIDs.put(unitIdentifier.getID(), UnitIDState.EXECUTED);
        }
    }

    @Override
    public JCoFunctionUnitState getFunctionUnitState(JCoServerContext serverCtx, JCoUnitIdentifier unitIdentifier)
    {
        UnitIDState currentState=availableUnitIDs.get(unitIdentifier.getID());
        if (currentState==null)
            return JCoFunctionUnitState.NOT_FOUND;

        switch (currentState)
        {
            case CREATED:
            case EXECUTED:
                return JCoFunctionUnitState.IN_PROCESS;
            case COMMITTED:
                return JCoFunctionUnitState.COMMITTED;
            case ROLLED_BACK:
                return JCoFunctionUnitState.ROLLED_BACK;
            case CONFIRMED:
                return JCoFunctionUnitState.CONFIRMED;
            default:
                return JCoFunctionUnitState.NOT_FOUND;
        }
    }
}