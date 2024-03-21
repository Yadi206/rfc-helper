package com.tiger.rfchelp.demos.sap.client.beginner;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;

/**
 * After executing a call in {@link SimpleCall}, we are now reading instead of a single value a structure from export parameter
 * RFCSI_EXPORT.
 */
public class CallUsingStructure
{

    public static void main(String[] args) throws JCoException
    {
        JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        JCoFunction function=destination.getRepository().getFunction("RFC_SYSTEM_INFO");
        if (function==null)
            throw new RuntimeException("RFC_SYSTEM_INFO not found in SAP.");

        try
        {
            function.execute(destination);
        }
        catch (AbapException e)
        {
            System.out.println(e);
            return;
        }

        // fetch the structure from the list of export parameters
        JCoStructure exportStructure=function.getExportParameterList().getStructure("RFCSI_EXPORT");
        System.out.println("System info for "+destination.getAttributes().getSystemID()+":\n");

        // The structure contains some fields. The loop just prints out each field with
        // its content.
        for (int i=0; i<exportStructure.getMetaData().getFieldCount(); i++)
            System.out.println(exportStructure.getMetaData().getName(i)+":\t"+exportStructure.getString(i));
        System.out.println();
    }
}
