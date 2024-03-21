package com.tiger.rfchelp.demos.sap.server.advanced.trfc;

import com.sap.conn.jco.JCoFunction;
import com.tiger.rfchelp.demos.sap.server.advanced.StfcInsertIntoTcpicHandler;
import com.sap.conn.jco.server.JCoServerContext;

/**
 * Demonstrate usage of StfcInsertIntoTcpicHandler with TIDs. Setting it as a member is the most simplest option that should not
 * be used in application servers when not knowing how the function comes in. There, a function module handler independent
 * approach should be chosen.
 */
public class StfcInsertIntoTcpicHandlerWithTID extends StfcInsertIntoTcpicHandler
{
    private final MyTIDHandler tidHandler;

    public StfcInsertIntoTcpicHandlerWithTID(MyTIDHandler tidHandler)
    {
        this.tidHandler=tidHandler;
    }

    @Override
    public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
    {
        super.handleRequest(serverCtx, function);

        tidHandler.execute(serverCtx);
    }
}
