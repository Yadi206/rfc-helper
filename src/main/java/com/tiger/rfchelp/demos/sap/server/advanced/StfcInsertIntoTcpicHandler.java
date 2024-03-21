/**
 * Property of SAP SE, Walldorf
 * (c) Copyright SAP SE, Walldorf, 2019.
 * All rights reserved.
 */
package com.tiger.rfchelp.demos.sap.server.advanced;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;

/**
 * This class provides the implementation for the function STFC_INSERT_INTO_TCPIC. You will find the RFC-enabled function
 * STFC_INSERT_INTO_TCPIC in any ABAP system as it is used in SRFCTEST report for demonstrating the exactly once scenario with
 * SRFCTEST report The function implementation below is pretty simple - it has 1 input parameter and 1 table parameter. The
 * content of the input parameter NRCALL is indicating the sequence number of multiple calls in a row and the data sent by the
 * content of the table is simply printed to the console.
 */
public class StfcInsertIntoTcpicHandler implements JCoServerFunctionHandler
{

    @Override
    public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
    {
        System.out.println("----------------------------------------------------------------");
        System.out.println("Called function   : "+function.getName());
        System.out.println("ConnectionId      : "+serverCtx.getConnectionID());
        System.out.println("SessionId         : "+serverCtx.getSessionID());
        System.out.println("Repository name   : "+serverCtx.getRepository().getName());
        System.out.println("Is in transaction : "+serverCtx.isInTransaction());
        System.out.println("TID               : "+serverCtx.getTID());
        System.out.println("Unit ID           : "+(serverCtx.getUnitIdentifier()==null?"":serverCtx.getUnitIdentifier().getID()));
        System.out.println("----------------------------------------------------------------");
        System.out.println("Gateway host      : "+serverCtx.getServer().getGatewayHost());
        System.out.println("Gateway service   : "+serverCtx.getServer().getGatewayService());
        System.out.println("Program ID        : "+serverCtx.getServer().getProgramID());
        System.out.println("----------------------------------------------------------------");
        System.out.println("Attributes        : ");
        System.out.println(serverCtx.getConnectionAttributes().toString());
        System.out.println("----------------------------------------------------------------");
        System.out.println("CPIC conversation ID: "+serverCtx.getConnectionAttributes().getCPICConversationID());
        System.out.println("----------------------------------------------------------------");
        System.out.println("CallNumber:   "+function.getImportParameterList().getString("NRCALL"));
        JCoTable tcpicDat=function.getTableParameterList().getTable("TCPICDAT");
        for (int i=0; i<tcpicDat.getNumRows(); i++)
        {
            tcpicDat.setRow(i);
            System.out.println("Tcpic row "+(i+1)+":   "+tcpicDat.getString("LINE"));
        }
    }
}