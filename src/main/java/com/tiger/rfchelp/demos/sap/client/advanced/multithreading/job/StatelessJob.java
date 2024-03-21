package com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job;

import java.util.concurrent.atomic.AtomicInteger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

public class StatelessJob implements MultiStepJob
{
    private static AtomicInteger JOB_COUNT=new AtomicInteger(0);
    protected int jobID=JOB_COUNT.addAndGet(1);

    protected final JCoDestination destination;
    private final JCoFunction incrementCounter, getCounter;

    private final int calls;

    protected int executedCalls=0;
    protected Exception ex=null;
    private int remoteCounter;

    public StatelessJob(JCoDestination destination, int calls) throws JCoException
    {
        this.destination=destination;
        incrementCounter=destination.getRepository().getFunction("Z_INCREMENT_COUNTER");
        getCounter=destination.getRepository().getFunction("Z_GET_COUNTER");
        if (incrementCounter==null || getCounter==null)
            throw new RuntimeException("This example cannot run without Z_INCREMENT_COUNTER and Z_GET_COUNTER functions");

        this.calls=calls;
    }

    @Override
    public boolean isFinished()
    {
        return executedCalls==calls || ex!=null;
    }

    @Override
    public String getName()
    {
        return "stateless Job-"+jobID;
    }

    @Override
    public void runNextStep()
    {
        try
        {
            incrementCounter.execute(destination);
            executedCalls++;

            if (isFinished())
            {
                getCounter.execute(destination);
                remoteCounter=getCounter.getExportParameterList().getInt("GET_VALUE");
            }
        }
        catch (JCoException|RuntimeException e)
        {
            ex=e;
        }
    }

    @Override
    public void cleanUp()
    {
        StringBuilder sb=new StringBuilder("Task ").append(getName()).append(" is finished ");
        if (ex!=null)
            sb.append("with exception ").append(ex);
        else
            sb.append("successful. Counter is ").append(remoteCounter);
        System.out.println(sb.toString());
    }
}
