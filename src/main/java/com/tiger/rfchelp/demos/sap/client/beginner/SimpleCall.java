package com.tiger.rfchelp.demos.sap.client.beginner;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

/**
 * After showing the first steps in {@link Connect}, we are now calling a simple RFC function: STFC_CONNECTION. In contrast to
 * JCo 2, you do not need to take care of repository management. JCo 3 manages the repository caches internally and shares the
 * available function metadata as much as possible. Simply fetch the repository from the destination to make use of this
 * beneficial feature.
 */
public class SimpleCall
{

    public static void main(String[] args) throws JCoException
    {
        // JCoDestination is the logic address of an ABAP system and ...
        JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        // ... it always has a reference to a metadata repository
        JCoFunction function=destination.getRepository().getFunction("STFC_CONNECTION");
        if (function==null)
            throw new RuntimeException("STFC_CONNECTION not found in SAP.");

        // JCoFunction is the container for function values. Each function consists of separate
        // containers for import, export, changing and table parameters.
        // To set or get the parameters use the APIS setXXX() and getXXX().
        function.getImportParameterList().setValue("REQUTEXT", "Hello SAP");

        try
        {
            // execute, i.e. send the function representation to the ABAP system addressed
            // by the specified destination, invoke it there, and retrieve the the function
            // result sent back by the system
            // All necessary conversions between Java and ABAP data types are done automatically.
            function.execute(destination);
        }
        catch (AbapException e)
        {
            System.out.println(e);
            return;
        }

        System.out.println("STFC_CONNECTION finished:");
        System.out.println(" Echo: "+function.getExportParameterList().getString("ECHOTEXT"));
        System.out.println(" Response: "+function.getExportParameterList().getString("RESPTEXT"));
        System.out.println();
    }
}
