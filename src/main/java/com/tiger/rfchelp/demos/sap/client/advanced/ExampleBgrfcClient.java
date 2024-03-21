package com.tiger.rfchelp.demos.sap.client.advanced;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoBackgroundUnitAttributes;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionUnit;
import com.sap.conn.jco.JCoTable;
import com.tiger.rfchelp.demos.sap.client.advanced.TransactionStore.TransactionStatus;
import com.tiger.rfchelp.demos.sap.client.beginner.DestinationConcept;

/**
 * This example shows how to achieve transactional security when sending a bgRFC unit into the backend.
 */

public class ExampleBgrfcClient
{

    public static void main(String[] args) throws IOException
    {
        Tuple<JCoFunctionUnit, String> tuple=null;
        TransactionStore transactionStore=new TransactionStore("clientUnitIdStore", TransactionStore.Type.BGRFC);

        boolean quit=false;
        try
        {
            while (!quit)
            {
                System.out.print("Resend an existing LUW, create a new one or quit? [r/c/q] ");
                String answer=getUserInput();

                switch (answer)
                {
                    case "r":
                        tuple=openBgUnitAsTuple(transactionStore);
                        break;

                    case "c":
                        tuple=createBgUnitAsTuple(transactionStore);
                        break;

                    default:
                        quit=true;
                        tuple=null;
                        break;
                }

                // Now we try to send that thing off into the backend.
                if (tuple!=null)
                {
                    submitBgUnit(tuple, transactionStore);
                }
            }
        }
        finally
        {
            transactionStore.close();
        }

    }

    private static String chooseUnitId(TransactionStore transactionStore) throws IOException
    {
        System.out.print("Please choose an existing Unit ID: ");
        String uid=getUserInput();
        try
        {
            String[] error=new String[1];
            // Double check, whether that Unit ID really exists:
            transactionStore.getStatus(uid, error);
        }
        catch (IllegalArgumentException e)
        {
            uid="";
        }
        return uid;
    }

    static Tuple<JCoFunctionUnit, String> openBgUnitAsTuple(TransactionStore transactionStore) throws IOException
    {

        if (transactionStore.getSize()==0)
        {
            System.out.println("No old LUWs exist. Let's create a new one instead...");
            return createBgUnitAsTuple(transactionStore);
        }
        String data="";
        // Let the user choose one of the previously failed LUWs:
        transactionStore.printOverview();
        String unitID=chooseUnitId(transactionStore);
        if (unitID!=null && unitID.length()>0)
        {
            System.out.println("Sorry, the entered Unit ID does not exist, please try again.");
            unitID=chooseUnitId(transactionStore);
        }

        if (unitID!=null && unitID.length()>0)
        {
            System.out.println("Sorry, the entered Unit ID does not exist, Bye!");
            return null;
        }

        // Read the payload back in
        RandomAccessFile raf=new RandomAccessFile(new File(unitID), "r");
        data=raf.readLine();

        // Recreate the transaction with the existing uid.
        JCoBackgroundUnitAttributes attr=JCo.createBackgroundUnitAttributes();
        JCoFunctionUnit unit=JCo.createFunctionUnit(unitID, attr);

        Tuple<JCoFunctionUnit, String> tuple=new Tuple<JCoFunctionUnit, String>(unit, data);
        raf.close();
        return tuple;
    }

    static Tuple<JCoFunctionUnit, String> createBgUnitAsTuple(TransactionStore transactionStore) throws IOException
    {
        // Create a new unit with a freshly generated Unit ID
        JCoFunctionUnit unit=JCo.createFunctionUnit(JCo.createBackgroundUnitAttributes());
        String uid=unit.getIdentifier().getID().toString();
        System.out.print("Please enter some input data: ");
        String data=getUserInput();

        // Persist the payload, so it can be resent later, in case something goes wrong the first time:
        RandomAccessFile raf=new RandomAccessFile(new File(uid), "rw");
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        raf.write(bytes, 0, bytes.length);

        transactionStore.createEntry(uid);
        Tuple<JCoFunctionUnit, String> tuple=new Tuple<JCoFunctionUnit, String>(unit, data);
        raf.close();
        return tuple;
    }

    private static void submitBgUnit(Tuple<JCoFunctionUnit, String> tuple, TransactionStore transactionStore)
    {
        String unitID=tuple.jcoFunctionContainer.getIdentifier().getID().toString();
        try
        {
            JCoFunctionUnit unit=tuple.jcoFunctionContainer;
            JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
            JCoFunction stfc_write_to_tcpic=destination.getRepository().getFunction("STFC_WRITE_TO_TCPIC");
            if (stfc_write_to_tcpic==null)
                throw new RuntimeException("STFC_WRITE_TO_TCPIC not found in SAP.");

            // In some releases, STFC_WRITE_TO_TCPIC does not have this parameter. Uncomment the following line, if it does:
            // stfc_write_to_tcpic.getImportParameterList().setActive("RESTART_QNAME", false);

            JCoTable table=stfc_write_to_tcpic.getTableParameterList().getTable("TCPICDAT");
            table.appendRow();
            table.setValue(0, tuple.dataString);

            // Insert the function module into the background RFC function unit:
            unit.addFunction(stfc_write_to_tcpic);

            // In order to demonstrate that a bgRFC LUW can consist of several function modules,
            // we clone the function object, modify the data in a fixed way and add a second
            // FM to the transaction.
            // Of course in this particular case we could as well add a second line to the
            // original function object, but this is to demonstrate that several different
            // function modules (even of different type) can be added to one LUW and treated
            // as an "atomic unit".
            stfc_write_to_tcpic=stfc_write_to_tcpic.clone();
            table=stfc_write_to_tcpic.getTableParameterList().getTable("TCPICDAT");
            table.setValue(0, tuple.dataString+" -- data of the second function module");
            unit.addFunction(stfc_write_to_tcpic);
            unit.commit(destination);

            transactionStore.setStatus(unitID, TransactionStatus.Committed, null);

            File file=new File(unitID);
            if (!file.delete())
                System.out.println("Unable to delete file!");

            // Only now, after everything was successful - including the deletion of the data
            // on our side, so that we can be absolutely sure that this transaction will never
            // be repeated from our side - we do confirm the Unit ID in the backend. From then on
            // the backend would no longer be protected against a duplicate update with the 
            // same Unit ID.
            destination.confirmFunctionUnit(unit.getIdentifier());
            transactionStore.deleteEntry(unitID);
            // Now use SE16 to check, whether two rows have been added to table TCPIC.
        }
        catch (Exception e)
        {
            transactionStore.setStatus(unitID, TransactionStatus.RolledBack, e.getMessage());
        }
    }

    private static String getUserInput() throws IOException
    {
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

}
