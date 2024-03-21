package com.tiger.rfchelp.demos.sap.client.advanced;

import java.util.Properties;

import com.sap.conn.jco.JCoException;
import com.tiger.rfchelp.demos.sap.client.beginner.CustomDestinationDataProvider;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.MessageServerDataEventListener;
import com.sap.conn.jco.ext.MessageServerDataProvider;
import com.sap.conn.jco.ms.JCoApplicationServer;
import com.sap.conn.jco.ms.JCoLogonGroup;
import com.sap.conn.jco.ms.JCoMessageServer;
import com.sap.conn.jco.ms.JCoMessageServerFactory;
import com.sap.conn.jco.ms.MessageServerQueryException;

/**
 * This example shows how to initiate a message server instance and retrieves some basic information from it. The
 * {@link MessageServerDataProvider} is conceptually similar to {@link DestinationDataProvider}, you can see in
 * {@link CustomDestinationDataProvider} how this concept works in detail.
 */
public class MessageServer
{

    public static void main(String[] args) throws MessageServerQueryException, JCoException
    {
        // register provider
        Environment.registerMessageServerDataProvider(new StaticMessageServerProvider());

        // get instance...
        JCoMessageServer messageServer=JCoMessageServerFactory.getMessageServer("test");

        // ... and retrieve all needed information (some sample information are printed
        // here)
        System.out.println("system ID: "+messageServer.getSystemId());
        System.out.println("service name: "+messageServer.getServiceName());
        System.out.println("service port: "+messageServer.getServicePort());
        System.out.println("host name: "+messageServer.getHostName());
        System.out.println("configuration name: "+messageServer.getName());

        for (JCoApplicationServer appServer : messageServer.getApplicationServers())
            System.out.println("Appserver: "+appServer.getHostName()+" | "+appServer.getInstanceNumber());

        for (JCoLogonGroup logonGroup : messageServer.getLogonGroups())
            System.out.println("Logon group: "+logonGroup.getName());
    }

    private static class StaticMessageServerProvider implements MessageServerDataProvider
    {

        @Override
        public Properties getMessageServerProperties(String messageServerName)
        {
            if (messageServerName.equals("test"))
            {
                final Properties msgServerProperties=new Properties();
                msgServerProperties.setProperty(MessageServerDataProvider.MESSAGE_SERVER_HOST, "msserver");
                msgServerProperties.setProperty(MessageServerDataProvider.MESSAGE_SERVER_SERVICE, "5371");
                return msgServerProperties;
            }
            else
                return null;
        }

        @Override
        public void setMessageServerDataEventListener(MessageServerDataEventListener eventListener)
        {
        }

        @Override
        public boolean supportsEvents()
        {
            return false;
        }
    }
}
