package com.tiger.rfchelp.demos.sap.server.beginner;

import java.io.IOException;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.ServerDataProvider;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.DefaultServerHandlerFactory.FunctionHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerFunctionHandlerFactory;
import com.sap.conn.jco.server.JCoServerState;

/**
 * With this first example we get an instance of the {@link JCoServer} through {@link JCoServerFactory}. Similar to client destinations, 
 * please register for productive usage an own {@link ServerDataProvider} instance in order to get the properties. The requested instance
 * will be created, or an existing one will be returned if the instance was created before. It is not possible to run more then
 * one instance with a particular configuration. Then we register the implementation for the function STFC_CONNECTION provided
 * by class {@link StfcConnectionHandler} through {@link FunctionHandlerFactory} provided by JCo. You are free to write your own
 * implementation {@link JCoServerFunctionHandlerFactory}, if you need more than simple mapping between function name and java
 * class implementing the function. Using the {@link DefaultServerHandlerFactory}, it is also possible to register one generic
 * handler via {@link DefaultServerHandlerFactory#registerGenericHandler(Object)}.
 * 
 * Now we can start the server instance. After a while the JCo runtime opens the server connections. You may check the server
 * connections via sm59 or invoke STFC_CONNECTION via se37.
 */
public class SimpleFunctionHandling
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=createServer();
        registerStfcConnection(server);
        server.start();

        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    public static JCoServer createServer()
    {
        try
        {
            return JCoServerFactory.getServer(ServerNameConcept.SomeSampleServers.SERVER_NAME1);
        }
        catch (JCoException ex)
        {
            throw new RuntimeException("Unable to create the server "+ServerNameConcept.SomeSampleServers.SERVER_NAME1
                    +" because of "+ex.getMessage(), ex);
        }
    }

    public static void registerStfcConnection(JCoServer server)
    {
        JCoServerFunctionHandler stfcConnectionHandler=new StfcConnectionHandler();
        DefaultServerHandlerFactory.FunctionHandlerFactory factory=new DefaultServerHandlerFactory.FunctionHandlerFactory();
        factory.registerHandler("STFC_CONNECTION", stfcConnectionHandler);

        server.setCallHandlerFactory(factory);
    }

    /**
     * This class provides the implementation for the function STFC_CONNECTION. You will find the RFC-enabled function
     * STFC_CONNECTION in any ABAP system as it is used in SRFCTEST report for demonstrating RFC with simple parameters. The
     * function implementation below is pretty simple as well - it has 1 input parameter and 2 output parameter. The content of
     * the input parameter REQUTEXT is copied to the output parameter ECHOTEXT. The output parameter RESPTEXT is set to "Hello
     * World".
     */
    public static class StfcConnectionHandler implements JCoServerFunctionHandler
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
            System.out.println("Is stateful       : "+serverCtx.isStatefulSession());
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
            System.out.println("Requtext: "+function.getImportParameterList().getString("REQUTEXT"));
            function.getExportParameterList().setValue("ECHOTEXT", function.getImportParameterList().getString("REQUTEXT"));
            function.getExportParameterList().setValue("RESPTEXT", "Hello World");
        }
    }
    
    /**
     * Wait (in this example 60 seconds) for the server to get stopped gracefully, otherwise return.
     */
    public static void waitForServerStop(JCoServer server) throws InterruptedException
    {
        for (int i=0; i<60 && server.getState()!=JCoServerState.STOPPED; i++)
            Thread.sleep(1000);
    }
}
