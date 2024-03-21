package com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class StatefulJob extends StatelessJob
{

    public StatefulJob(JCoDestination destination, int calls) throws JCoException
    {
        super(destination, calls);
    }

    @Override
    public String getName()
    {
        return "stateful Job-"+jobID;
    }

    @Override
    public void runNextStep()
    {
        if (executedCalls==0)
            JCoContext.begin(destination);
        super.runNextStep();
    }

    @Override
    public void cleanUp()
    {
        try
        {
            JCoContext.end(destination);
        }
        catch (JCoException je)
        {
            ex=je;
        }
        super.cleanUp();
    }
}
