package com.tiger.rfchelp.demos.sap.client.beginner;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

/**
 * As a first step to use JCo, this example demonstrates the destination concept introduced with JCO 3. The application does not deal
 * with single connections anymore. Instead it works with logical destinations like ABAP_AS1 and ABAP_AS2 which separates the
 * application logic from technical configuration. See {@link DestinationConcept} and {@link CustomDestinationDataProvider} for
 * more details.
 */
public class Connect
{
    public static void main(String[] args) throws JCoException
    {
        JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);

        // optional ping to check if destination is available
        destination.ping();

        // just print the attributes here
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
    }

}
