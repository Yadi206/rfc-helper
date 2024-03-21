package com.tiger.rfchelp.demos.sap.client.beginner;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * After executing a call in {@link SimpleCall}, we are now reading instead of a single value a table COMPANYCODE_LIST. After
 * printing it out, we are using its values in the field COMP_CODE to use them as input for another table COMPANYCODEID for
 * another function module BAPI_COMPANYCODE_GETDETAIL that provides more info related to this company.
 */
public class CallUsingTable
{

    public static void main(String[] args) throws JCoException
    {
        JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        JCoFunction function=destination.getRepository().getFunction("BAPI_COMPANYCODE_GETLIST");
        if (function==null)
            throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");
        try
        {
            function.execute(destination);
        }
        catch (AbapException e)
        {
            System.out.println(e);
            return;
        }

        JCoTable codes=function.getTableParameterList().getTable("COMPANYCODE_LIST");
        printCompanyCodes(codes);
        printCompanyCodeDetails(destination, codes);
    }

    private static void printCompanyCodes(JCoTable codes)
    {
        // this is the first option to iterate a table
        for (int i=0; i<codes.getNumRows(); i++)
        {
            codes.setRow(i);
            System.out.println(codes.getString("COMP_CODE")+'\t'+codes.getString("COMP_NAME"));
        }
    }

    private static void printCompanyCodeDetails(JCoDestination destination, JCoTable codes) throws JCoException
    {
        // this is the second option to iterate a table, move the table cursor to first row ... 
        codes.firstRow();
        do
        {
            JCoFunction function=destination.getRepository().getFunction("BAPI_COMPANYCODE_GETDETAIL");
            if (function==null)
                throw new RuntimeException("BAPI_COMPANYCODE_GETDETAIL not found in SAP.");

            function.getImportParameterList().setValue("COMPANYCODEID", codes.getString("COMP_CODE"));

            // We do not need the addresses, so set the corresponding parameter to inactive.
            // Inactive parameters will be either not generated or at least converted.
            function.getExportParameterList().setActive("COMPANYCODE_ADDRESS", false);

            try
            {
                function.execute(destination);
            }
            catch (AbapException e)
            {
                System.out.println(e);
                return;
            }

            JCoStructure detail=function.getExportParameterList().getStructure("COMPANYCODE_DETAIL");

            System.out.println(detail.getString("COMP_CODE")+'\t'+detail.getString("COUNTRY")+'\t'+detail.getString("CITY"));

        } // ... and move the cursor as long as there are further rows to be processed
        while (codes.nextRow());
    }

}
