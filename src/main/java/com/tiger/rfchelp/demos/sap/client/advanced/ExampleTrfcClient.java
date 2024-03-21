package com.tiger.rfchelp.demos.sap.client.advanced;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.tiger.rfchelp.demos.sap.client.advanced.TransactionStore.TransactionStatus;
import com.tiger.rfchelp.demos.sap.client.beginner.DestinationConcept;

public class ExampleTrfcClient
{
    public static void main(String[] args) throws IOException, JCoException
    {
        Tuple<JCoTransaction, String> tuple=null;
        TransactionStore transactionStore=new TransactionStore("clientTransactionIdStore", TransactionStore.Type.TRFC);

        boolean quit=false;
        try
        {
            while (!quit)
            {
                System.out.print("Resend an existing transaction, create a new one or quit? [r/c/q] ");
                String answer=getUserInput();

                switch (answer)
                {
                    case "r":
                        tuple=openTransactionAsTuple(transactionStore);
                        break;
                    case "c":
                        tuple=createTransactionAsTuple(transactionStore);
                        break;
                    default:
                        quit=true;
                        tuple=null;
                        break;
                }

                // Now we try to send that thing off into the backend.
                if (tuple!=null)
                {
                    submitTransaction(tuple, transactionStore);
                }
            }
        }
        finally
        {
            transactionStore.close();
        }

    }

    static Tuple<JCoTransaction, String> openTransactionAsTuple(TransactionStore transactionStore) throws IOException, JCoException
    {
        Tuple<JCoTransaction, String> tuple=null;
        if (transactionStore.getSize()==0)
        {
            System.out.println("No old transactions exist. Let's create a new one instead...");
            return createTransactionAsTuple(transactionStore);
        }
        String data="";
        // Let the user choose one of the previously failed LUWs:
        transactionStore.printOverview();
        String tid=chooseTransaction(transactionStore);
        if (tid!=null && tid.length()>0)
        {
            System.out.println("Sorry, the entered TID does not exist, please try again.");
            tid=chooseTransaction(transactionStore);
        }

        if (tid!=null && tid.length()>0)
        {
            System.out.println("Sorry, the entered TID does not exist, Bye!");
            return null;
        }

        // Read the payload back in
        RandomAccessFile raf=new RandomAccessFile(new File(tid), "r");
        data=raf.readLine();

        // Recreate the transaction with the existing transactionID.
        JCoTransaction transaction=new JCoTransaction(tid);

        tuple=new Tuple<JCoTransaction, String>(transaction, data);
        raf.close();
        return tuple;
    }

    private static String chooseTransaction(TransactionStore transactionStore) throws IOException
    {
        System.out.print("Please choose an existing TID: ");
        String uid=getUserInput();
        try
        {
            String[] error=new String[1];
            // Double check, whether that TID really exists:
            transactionStore.getStatus(uid, error);
        }
        catch (IllegalArgumentException e)
        {
            uid="";
        }
        return uid;
    }

    static Tuple<JCoTransaction, String> createTransactionAsTuple(TransactionStore transactionStore) throws IOException, JCoException
    {
        Tuple<JCoTransaction, String> tuple=null;
        JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        // Create a new transaction using a TID fetched from the target system
        JCoTransaction transaction=new JCoTransaction(destination.createTID());
        System.out.print("Please enter some input data: ");
        String data=getUserInput();

        // Persist the payload, so it can be resent later, in case something goes wrong the first time:
        RandomAccessFile raf=new RandomAccessFile(new File(transaction.getTid()), "rw");
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        raf.write(bytes, 0, bytes.length);

        transactionStore.createEntry(transaction.getTid());
        tuple=new Tuple<JCoTransaction, String>(transaction, data);
        raf.close();

        return tuple;
    }

    private static void submitTransaction(Tuple<JCoTransaction, String> tuple, TransactionStore transactionStore)
    {
        String tid=tuple.jcoFunctionContainer.getTid();
        try
        {
            JCoDestination destination=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
            JCoFunction stfc_write_to_tcpic=destination.getRepository().getFunction("STFC_WRITE_TO_TCPIC");
            if (stfc_write_to_tcpic==null)
                throw new RuntimeException("STFC_WRITE_TO_TCPIC not found in SAP.");
            // In some releases, STFC_WRITE_TO_TCPIC does not have this parameter. Uncomment the following line, if it does:
            // stfc_write_to_tcpic.getImportParameterList().setActive("RESTART_QNAME", false);

            JCoTable table=stfc_write_to_tcpic.getTableParameterList().getTable("TCPICDAT");
            table.appendRow();
            table.setValue(0, tuple.dataString);
            stfc_write_to_tcpic.execute(destination, tid);

            transactionStore.setStatus(tid, TransactionStatus.Committed, null);

            File file=new File(tid);
            if (!file.delete())
                System.out.println("Unable to delete file!");

            // Only now, after everything was successful - including the deletion of the data
            // on our side, so that we can be absolutely sure that this transaction will never
            // be repeated from our side - we do confirm the TID in the backend. From then on
            // the backend would no longer be protected against a duplicate update with the 
            // same TID.
            destination.confirmTID(tid);
            transactionStore.deleteEntry(tid);
            // Now use SE16 to check, whether the row has been added to table TCPIC.
        }
        catch (Exception e)
        {
            transactionStore.setStatus(tid, TransactionStatus.RolledBack, e.getMessage());
        }
    }

    private static String getUserInput() throws IOException
    {
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
}
