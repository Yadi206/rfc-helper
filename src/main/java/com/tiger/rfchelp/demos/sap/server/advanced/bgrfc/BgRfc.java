package com.tiger.rfchelp.demos.sap.server.advanced.bgrfc;

import java.io.IOException;

import com.tiger.rfchelp.demos.sap.server.advanced.trfc.TRfc;
import com.tiger.rfchelp.demos.sap.server.beginner.SimpleFunctionHandling;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerTIDHandler;
import com.sap.conn.jco.server.JCoServerUnitIDHandler;

/**
 * This example is similar to {@link TRfc}, the difference is the usage of {@link JCoServerUnitIDHandler} instead of
 * {@link JCoServerTIDHandler}. A bgRFC can be triggered in ABAP when adding the ABAP statement extension IN BACKGROUND TASK to
 * CALL FUNCTION. The {@link JCoServerUnitIDHandler} implementation is also registered at the server instance and will be used
 * for each call sent in "background unit". Without such an implementation JCo runtime will deny any bgRFC calls. See javadoc
 * for interface {@link JCoServerUnitIDHandler} for details.
 */
public class BgRfc
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=SimpleFunctionHandling.createServer();

        MyUnitIDHandler unitHandler=new MyUnitIDHandler();
        registerStfcInsertIntoTcpicWithUnitID(server, unitHandler);
        server.setUnitIDHandler(unitHandler);

        server.start();
        
        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    private static void registerStfcInsertIntoTcpicWithUnitID(JCoServer server, MyUnitIDHandler unitHandler)
    {
        JCoServerFunctionHandler stfcInsertIntoTcpicHandlerWithUnitID=new StfcInsertIntoTcpicHandlerWithUnitID(unitHandler);
        DefaultServerHandlerFactory.FunctionHandlerFactory factory=new DefaultServerHandlerFactory.FunctionHandlerFactory();
        factory.registerHandler("STFC_CONNECTION", stfcInsertIntoTcpicHandlerWithUnitID);

        server.setCallHandlerFactory(factory);
    }
}
