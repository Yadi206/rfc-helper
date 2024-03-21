package com.tiger.rfchelp.demos.sap.client.beginner;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

/**
 * Each application using Java Connector 3 deals with destinations. A destination represents a logical address of an
 * ABAP system and thus separates the destination configuration from application logic. JCo retrieves the destination
 * parameters required at runtime from the DestinationDataProvider registered in the runtime environment. If no provider
 * is registered, JCo uses its default implementation that reads the configuration from a properties file. It is
 * expected that each environment provides a suitable implementation that meets security and other requirements.
 * Furthermore, only a single DestinationDataProvider can be registered per process. The reason behind this design
 * decision is the following: the provider implementations are part of the environment infrastructure. The
 * implementation must not be application specific, and in particular must be separated from application logic.
 * 
 * This example demonstrates a simple implementation of the DestinationDataProvider interface and shows how to register
 * it. A real world implementation should save the configuration data in a secure way (Here, for simplicity purposes it
 * is stored in memory)
 */
public class CustomDestinationDataProvider
{

    public static void main(String[] args)
    {
        InMemoryDestinationDataProvider memoryProvider=new CustomDestinationDataProvider.InMemoryDestinationDataProvider();

        // register the provider with the JCo environment;
        // catch IllegalStateException if an instance is already registered
        try
        {
            Environment.registerDestinationDataProvider(memoryProvider);
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            // somebody else registered its implementation stop the execution, alternatively youc
            // could write it to your logs
            throw new Error(providerAlreadyRegisteredException);
        }

        // set properties for the destination ABAP_AS1 and ...
        memoryProvider.changeProperties(DestinationConcept.SomeSampleDestinations.ABAP_AS1,
                getDestinationPropertiesFromUI());

        // ... work with it
        executeCalls(DestinationConcept.SomeSampleDestinations.ABAP_AS1);

        // now remove the properties and ...
        memoryProvider.changeProperties(DestinationConcept.SomeSampleDestinations.ABAP_AS1, null);
        // ... and let the test fail
        executeCalls(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
    }

    /**
     * The custom destination data provider implements DestinationDataProvider and provides an implementation for at
     * least getDestinationProperties(String). Whenever possible the implementation should support events and notify the
     * JCo runtime if a destination is being created, changed, or deleted. Otherwise JCo runtime will check regularly if
     * a cached destination configuration is still valid which incurs a performance penalty.
     */
    private static class InMemoryDestinationDataProvider implements DestinationDataProvider
    {
        private DestinationDataEventListener eL;
        private HashMap<String, Properties> secureDBStorage=new HashMap<String, Properties>();

        @Override
        public Properties getDestinationProperties(String destinationName)
        {
            try
            {
                // read the destination from DB
                Properties p=secureDBStorage.get(destinationName);

                // check if all is correct
                if (p!=null&&p.isEmpty())
                    throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,
                            "destination configuration is incorrect", null);

                return p;
            }
            catch (RuntimeException re)
            {
                throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
            }
        }

        // An implementation supporting events has to retain the eventListener instance
        // provided by the JCo runtime. This listener instance shall be used to notify
        // the JCo runtime about all changes in destination configurations.
        @Override
        public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
        {
            this.eL=eventListener;
        }

        @Override
        public boolean supportsEvents()
        {
            return true;
        }

        /** implementation that saves the properties in memory */
        void changeProperties(String destName, Properties properties)
        {
            synchronized (secureDBStorage)
            {
                if (properties==null)
                {
                    if (secureDBStorage.remove(destName)!=null)
                        eL.deleted(destName);
                }
                else
                {
                    secureDBStorage.put(destName, properties);
                    eL.updated(destName); // create or updated
                }
            }
        }
    }

    private static Properties getDestinationPropertiesFromUI()
    {
        // adapt parameters in order to configure a valid destination
        Properties connectProperties=new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "appserver");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "000");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "JCOTESTER");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "JCOTESTERSPASSWORD");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
        return connectProperties;
    }

    private static void executeCalls(String destName)
    {
        try
        {
            JCoDestination dest=JCoDestinationManager.getDestination(destName);
            dest.ping();
            System.out.println("Destination "+destName+" works");
        }
        catch (JCoException e)
        {
            e.printStackTrace();
            System.out.println("Execution on destination "+destName+" failed");
        }
    }

}
