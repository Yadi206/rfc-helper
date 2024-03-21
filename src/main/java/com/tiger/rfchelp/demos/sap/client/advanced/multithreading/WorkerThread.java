package com.tiger.rfchelp.demos.sap.client.advanced.multithreading;

import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job.MultiStepJob;

public class WorkerThread extends Thread
{
    static Hashtable<MultiStepJob, SimpleSessionReference> sessions=new Hashtable<>();
    static ThreadLocal<SimpleSessionReference> localSessionReference=new ThreadLocal<>();
    private static SimpleSessionReferenceProvider simpleSessionReferenceProvider;
    
    private BlockingQueue<MultiStepJob> queue;
    private CountDownLatch doneSignal;


    public static void setSessionReferenceProvider(SimpleSessionReferenceProvider simpleSessionReferenceProvider)
    {
        WorkerThread.simpleSessionReferenceProvider=simpleSessionReferenceProvider;
    }

    WorkerThread(BlockingQueue<MultiStepJob> queue, CountDownLatch doneSignal)
    {
        this.queue=queue;
        this.doneSignal=doneSignal;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                MultiStepJob job=queue.poll(10, TimeUnit.SECONDS);

                // stop if nothing to do
                if (job==null)
                    return;

                SimpleSessionReference sesRef=sessions.get(job);
                if (sesRef==null)
                {
                    sesRef=new SimpleSessionReference();
                    sessions.put(job, sesRef);
                }
                localSessionReference.set(sesRef);

                System.out.println("Task "+job.getName()+" is started.");
                try
                {
                    job.runNextStep();
                }
                catch (Throwable th)
                {
                    th.printStackTrace();
                }

                if (job.isFinished())
                {
                    System.out.println("Task "+job.getName()+" is finished.");
                    sesRef=sessions.remove(job);
                    // tell the SessionReferenceProvider to fire the finished event
                    // in real world scenarios this is done by some session management
                    // component that should be aware of such situations
                    if (sesRef!=null)
                        simpleSessionReferenceProvider.fireServerFinishedEvent(sesRef.getID());
                    job.cleanUp();
                }
                else
                {
                    System.out.println("Task "+job.getName()+" is passivated.");
                    queue.add(job);
                }
                localSessionReference.set(null);
            }
        }
        catch (InterruptedException e)
        {
            // just leave
        }
        finally
        {
            doneSignal.countDown();
        }
    }
}