package com.tiger.rfchelp.demos.sap.server.advanced;

import java.io.IOException;

import com.tiger.rfchelp.demos.sap.server.beginner.SimpleFunctionHandling;
import com.sap.conn.jco.server.JCoApplicationAuthenticationException;
import com.sap.conn.jco.server.JCoApplicationAuthorizationException;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerAuthenticationData;
import com.sap.conn.jco.server.JCoServerAuthenticationData.AuthenticationMode;
import com.sap.conn.jco.server.JCoServerAuthorizationData;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerSecurityHandler;

/**
 * In this example additional security features are used. For that, a {@link JCoServerSecurityHandler} is added. The
 * implementation is artificial, but should demonstrate how to use it. At the beginning of each session, the authentication is
 * checked, in this example all SSO based logins need to have the SSO ticket "ABCD-1234". Furthermore, for each remote function
 * call the authorization is checked. This example allows only the function module STFC_CONNECTION.
 */
public class Security
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=SimpleFunctionHandling.createServer();
        SimpleFunctionHandling.registerStfcConnection(server);

        server.setSecurityHandler(new SecurityHandler());

        server.start();

        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    private static class SecurityHandler implements JCoServerSecurityHandler
    {

        @Override
        public void checkAuthorization(JCoServerContext serverCtx, String functionName,
                JCoServerAuthorizationData authorizationData)
            throws JCoApplicationAuthorizationException
        {
            if (!functionName.equals("STFC_CONNECTION"))
                throw new JCoApplicationAuthorizationException("Only function STFC_CONNECTION is allowed");
        }

        @Override
        public void checkAuthentication(JCoServerContextInfo serverCtxInfo, JCoServerAuthenticationData... authenticationData)
            throws JCoApplicationAuthenticationException
        {
            for (JCoServerAuthenticationData dataEntry : authenticationData)
            {
                if (dataEntry.getAuthenticationMode()==AuthenticationMode.SSO)
                {
                    if (!dataEntry.getSSOTicket().equals("ABCD-1234"))
                        throw new JCoApplicationAuthenticationException("The SSO ticket "+dataEntry.getSSOTicket()+" is not valid");
                }
            }
        }
    }
}
