package com.tiger.rfchelp.demos.sap.server.beginner;

import java.io.IOException;

import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;

/**
 * This example shows the three different listeners which can be added to {@link JCoServer}. You can add a
 * {@link JCoServerErrorListener} and {@link JCoServerExceptionListener} to track problems. The
 * {@link JCoServerStateChangedListener} informs about {@link JCoServerState} changes.
 * 
 * To see an example error you can force a disconnect from the gateway monitor SMGW. Under clients, you can delete the
 * respective connections. You will notice, that the listener prints out the errors on the console.
 */
public class ServerListener
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=SimpleFunctionHandling.createServer();
        SimpleFunctionHandling.registerStfcConnection(server);

        MyServerFailureListener eListener=new MyServerFailureListener();
        server.addServerErrorListener(eListener);
        server.addServerExceptionListener(eListener);

        SimpleStateChangedListener slistener=new SimpleStateChangedListener();
        server.addServerStateChangedListener(slistener);

        server.start();

        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    private static class MyServerFailureListener implements JCoServerErrorListener, JCoServerExceptionListener
    {

        @Override
        public void serverErrorOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx, Error error)
        {
            System.out.println(">>> Error occured on "+jcoServer.getProgramID()+" connection "+connectionId);
            error.printStackTrace();
        }

        @Override
        public void serverExceptionOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx,
                Exception ex)
        {
            System.out.println(">>> Exception occured on "+jcoServer.getProgramID()+" connection "+connectionId);
            ex.printStackTrace();
        }
    }

    private static class SimpleStateChangedListener implements JCoServerStateChangedListener
    {

        @Override
        public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState)
        {

            // Defined states are: STARTED, DEAD, ALIVE, STOPPED, HA_BROKEN; see JCoServerState class for details.
            // Details for connections managed by a server instance are available via JCoServerMonitor.
            System.out.println("Server state changed from "+oldState+" to "+newState+" on server with program id "+server.getProgramID());
        }
    }
}
