package com.tiger.rfchelp.demos.sap.server.advanced.trfc;

import java.io.IOException;

import com.tiger.rfchelp.demos.sap.server.beginner.SimpleFunctionHandling;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerTIDHandler;

/**
 * The following server example demonstrates how to implement the support for tRFC calls. These are CALL FUNCTIONs executed with
 * ABAP statement extension IN BACKGROUND TASK. At first we write an implementation for {@link JCoServerTIDHandler} interface.
 * This implementation is registered at the server instance and will be used for each call sent in "background task". Without
 * such an implementation JCo runtime will deny any tRFC calls. See javadoc for interface {@link JCoServerTIDHandler} for
 * details.
 */
public class TRfc
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=SimpleFunctionHandling.createServer();

        MyTIDHandler tidHandler=new MyTIDHandler();
        registerStfcInsertIntoTcpicWithTID(server, tidHandler);
        server.setTIDHandler(tidHandler);

        server.start();
        
        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    private static void registerStfcInsertIntoTcpicWithTID(JCoServer server, MyTIDHandler tidHandler)
    {
        JCoServerFunctionHandler stfcInsertIntoTcpicHandlerWithTID=new StfcInsertIntoTcpicHandlerWithTID(tidHandler);
        DefaultServerHandlerFactory.FunctionHandlerFactory factory=new DefaultServerHandlerFactory.FunctionHandlerFactory();
        factory.registerHandler("STFC_INSERT_INTO_TCPIC", stfcInsertIntoTcpicHandlerWithTID);

        server.setCallHandlerFactory(factory);
    }
}
