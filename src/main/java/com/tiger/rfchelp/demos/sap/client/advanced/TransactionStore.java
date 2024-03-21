package com.tiger.rfchelp.demos.sap.client.advanced;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Stack;

/**
 * A very basic file based transaction store. It is not fulfilling the needs for real productive usage, 
 * but helps to understand what needs to be done in a real implementation. 
 */
public class TransactionStore
{
    public enum TransactionStatus
    {
        Created, Executed, Committed, RolledBack, Confirmed;

        public static TransactionStatus fromByte(byte x)
        {
            switch (x)
            {
                case 1:
                    return Created;
                case 2:
                    return Executed;
                case 3:
                    return Committed;
                case 4:
                    return RolledBack;
                case 5:
                    return Confirmed;
            }
            return null;
        }

        public static byte toByte(TransactionStatus tid)
        {
            return (byte)(tid.ordinal()+1);
        }
    }

    public enum Type
    {
        TRFC, BGRFC
    }

    class StoreEntry
    {
        protected String transactionID;
        protected TransactionStatus status;
        protected int index;
    }

    final static int REMAINDER_SIZE=80;
    final static int TID_SIZE=24;
    final static int GUID_SIZE=32;
    final static byte[] EMPTY_REMAINDER=new byte[REMAINDER_SIZE];
    public static final int SIZE_OF_INT=4;

    private int entrySize, idSize;
    private byte[] empty_id;
    private Type type;
    private RandomAccessFile raf;
    private HashMap<String, StoreEntry> table;
    private Stack<StoreEntry> freeEntries=new Stack<StoreEntry>();
    int slots;

    // Opens an existing TransactionStore, or creates a fresh one, if the given file does not yet exist.
    public TransactionStore(String fileName, Type type)
    {
        this.type=type;
        idSize=type==Type.BGRFC?GUID_SIZE:TID_SIZE;
        entrySize=idSize+REMAINDER_SIZE;
        empty_id=new byte[idSize];

        if (fileName==null || fileName.length()==0)
            fileName="TransactionStore.dat";
        try
        {
            raf=new RandomAccessFile(new File(fileName), "rw");
            long l=raf.length();
            if (l<entrySize) // A newly created empty store file
            {
                slots=0;
                raf.seek(0);
                raf.writeInt(slots);
            }
            else
            {
                raf.seek(0);
                byte[] temp=new byte[(int)raf.length()];
                raf.read(temp);
                raf.seek(0);
                slots=raf.readInt();
            }
            table=new HashMap<String, StoreEntry>();

            for (int i=0; i<slots; ++i)
            {
                StoreEntry entry=new StoreEntry();
                entry.index=(int)raf.getChannel().position();
                // Read transactionID/uid
                char[] tempId=new char[idSize];
                int j=0;
                for (j=0; j<idSize; j++)
                    tempId[j]=(char)raf.readByte();
                if (j!=idSize)
                    throw new Exception("Unable to read entry at position "+i);
                // Read status
                l=raf.getChannel().position();
                entry.status=TransactionStatus.fromByte(raf.readByte());
                if (entry.status==null)
                    throw new Exception("False Tid status in file");
                // Skip REMAINDER
                raf.seek(raf.getChannel().position()+REMAINDER_SIZE);

                if (tempId[0]==0)
                {
                    freeEntries.push(entry);
                }
                else
                {
                    entry.transactionID=new String(tempId);
                    table.put(entry.transactionID, entry);
                }
            }
        }
        catch (Exception e)
        {
            if (raf!=null)
                try
                {
                    raf.close();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
        }
    }

    public void close()
    {
        synchronized (this)
        {
            try
            {
                if (raf!=null)
                    raf.close();
            }
            catch (Exception e)
            {
            }
            freeEntries.clear();
            table.clear();
        }
    }

    public TransactionStatus createEntry(String transactionID)
    {
        synchronized (this)
        {
            if (transactionID==null || transactionID.length()!=idSize)
                throw new IllegalArgumentException("Invalid Transaction ID");

            StoreEntry entry=table.get(transactionID);
            if (entry!=null)
                return entry.status;

            try
            {
                if (freeEntries.size()>0)
                {
                    entry=freeEntries.pop();
                }
                else
                {
                    entry=new StoreEntry();
                    entry.index=(int)raf.getChannel().position();

                    raf.seek(0);
                    raf.writeInt(++slots);
                }
                entry.status=TransactionStatus.Created;
                entry.transactionID=transactionID;
                table.put(transactionID, entry);
                raf.seek(entry.index);
                raf.write(transactionID.getBytes(), 0, idSize);
                raf.write(TransactionStatus.toByte(TransactionStatus.Created));
                raf.write(EMPTY_REMAINDER, 0, REMAINDER_SIZE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return TransactionStatus.Created;
        }
    }

    // The following should be self-explanatory.
    public void deleteEntry(String transactionID)
    {
        synchronized (this)
        {
            StoreEntry entry=table.get(transactionID);
            if (entry!=null)
            {
                try
                {
                    table.remove(transactionID);
                    raf.seek(entry.index);
                    raf.write(empty_id, 0, idSize);
                    freeEntries.push(entry);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStatus(String transactionID, TransactionStatus transactionStatus, String errorMessage)
    {
        synchronized (this)
        {

            StoreEntry entry=table.get(transactionID);
            if (entry==null)
                throw new IllegalArgumentException("Invalid transaction ID");

            try
            {
                entry.status=transactionStatus;
                raf.seek(entry.index+idSize);
                raf.write(TransactionStatus.toByte(transactionStatus));

                if (errorMessage!=null)
                {
                    byte[] err=errorMessage.getBytes(StandardCharsets.UTF_8);
                    int result=err.length<REMAINDER_SIZE?err.length:REMAINDER_SIZE;
                    raf.write(err, 0, result);
                    if (result<REMAINDER_SIZE)
                        raf.write((byte)0);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Tuple<String, String> getStatusAsTuple(String transactionID, String errorMessage)
    {
        TransactionStatus transactionStatus=TransactionStatus.Created;
        synchronized (this)
        {
            errorMessage=null;

            StoreEntry entry=table.get(transactionID);
            if (entry==null)
                throw new IllegalArgumentException("Invalid transaction ID");

            transactionStatus=entry.status;

            if (transactionStatus==TransactionStatus.RolledBack)
            {
                try
                {
                    raf.seek(entry.index+idSize+1);
                    byte[] err=new byte[REMAINDER_SIZE];
                    for (int j=0; j<REMAINDER_SIZE; j++)
                        err[j]=raf.readByte();
                    errorMessage=new String(err, StandardCharsets.UTF_8);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        Tuple<String, String> tuple=new Tuple<String, String>(transactionID, errorMessage);
        return tuple;
    }

    public TransactionStatus getStatus(String transactionID, String[] errorMessage)
    {
        TransactionStatus transactionStatus=TransactionStatus.Created;
        synchronized (this)
        {
            errorMessage[0]="";

            StoreEntry entry=table.get(transactionID);
            if (entry==null)
                throw new IllegalArgumentException("Invalid transaction ID");

            transactionStatus=entry.status;

            if (transactionStatus==TransactionStatus.RolledBack)
            {
                try
                {
                    raf.seek(entry.index+idSize+1);
                    byte[] err=new byte[REMAINDER_SIZE];
                    for (int j=0; j<REMAINDER_SIZE; j++)
                        err[j]=raf.readByte();
                    errorMessage[0]=new String(err, StandardCharsets.UTF_8);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return transactionStatus;
    }

    // Prints all details for a given entry to the console.
    public void printEntry(String transactionID)
    {
        synchronized (this)
        {
            TransactionStatus transactionStatus;
            String[] error=new String[1];
            transactionStatus=getStatus(transactionID, error);
            System.out.println("Transaction ID: "+transactionID);
            System.out.println("Status:         "+transactionStatus);
            if (transactionStatus==TransactionStatus.RolledBack)
                System.out.println("Error message:  '"+transactionStatus+'\'');
        }
    }

    // Prints a list of all existing entries to the console.
    public void printOverview()
    {
        if (table.size()==0)
        {
            if (type==Type.TRFC)
                System.out.println("No tRFC LUWs received yet\n");
            else
                System.out.println("No bgRFC LUWs received yet\n");
            return;
        }

        synchronized (this)
        {
            String format=(type==Type.BGRFC?"%10s    %-32s    %-20s    %s":"%10s    %-24s    %-20s   %s");
            System.out.println();
            System.out.printf(format, "Index", type==Type.BGRFC?"GUID":"TID", "Status", "Error message");
            System.out.println();
            try
            {
                String errorMessage="";
                raf.seek(SIZE_OF_INT);
                for (int i=0; i<slots; ++i)
                {
                    char[] temp=new char[idSize];
                    for (int j=0; j<idSize; j++)
                        temp[j]=(char)raf.readByte();
                    if (temp[0]==0)
                    {
                        raf.seek(1+REMAINDER_SIZE);
                        continue;
                    }
                    TransactionStatus status=TransactionStatus.fromByte(raf.readByte());
                    if (status==null)
                        throw new Exception("False Tid status in file");
                    if (status==TransactionStatus.RolledBack)
                    {
                        byte[] byteBuf=new byte[REMAINDER_SIZE];
                        for (int j=0; j<REMAINDER_SIZE; j++)
                            byteBuf[j]=raf.readByte();

                        int result=0;
                        while (result<byteBuf.length && byteBuf[result]!=0)
                            ++result;
                        errorMessage=new String(byteBuf, StandardCharsets.UTF_8);
                    }
                    System.out.printf(format, i, new String(temp), status, errorMessage);
                    System.out.println();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public int getSize()
    {
        return table.size();
    }

}
