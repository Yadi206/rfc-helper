package com.tiger.rfchelp.demos.sap.client.advanced.multithreading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job.MultiStepJob;
import com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job.StatefulJob;
import com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job.StatelessJob;
import com.tiger.rfchelp.demos.sap.client.beginner.DestinationConcept;
import com.tiger.rfchelp.demos.sap.client.beginner.StatefulCalls;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.SessionReferenceProvider;

/**
 * This example demonstrates how to use the {@link SessionReferenceProvider}.
 * 
 * Before discussing situations requiring {@link SessionReferenceProvider}, we provide a short description of how the JCo
 * runtime handles the stateful and stateless calls by default. By default all RFC calls (JCoFunction.execute(JCoDestination))
 * are stateless. That means the ABAP context associated with the connection will be destroyed after each call. Some RFC modules
 * save a particular state/data in the ABAP context's area. In order to keep a JCo connection and use it for subsequent
 * (stateful) calls, the JCoConext.begin(JCoDestination) API can be used (see {@link StatefulCalls}. In the case of
 * multithreaded applications some calls to a destination can be executed concurrently, so JCo runtime needs to associate a
 * particular call or connection to an internal session. By default, i.e. when using the built-in
 * {@link SessionReferenceProvider}, JCo Runtime associates each thread with a session of its own, so that most applications
 * that execute all stateful requests en bloc or at least in the same thread will run correctly.
 * 
 * Applications that wish to execute calls belonging to a stateful sequence by employing different threads have to implement and
 * register an own implementation of the {@link SessionReferenceProvider}. The main goal of the implementation is to determine
 * to which session the calls executing in the current thread belong. Hence, JCo runtime will call
 * {@link SessionReferenceProvider.getCurrentSessionReference(String)} just before invoking function modules for the given
 * destination.
 * 
 * This example defines MultiStepJob having several execution steps. The test starts a certain number of threads (see runJobs).
 * Each thread is designed to take a job, execute one step, and put the job back to the shared job list. There are two jobs as
 * an example: {@link StatelessJob} and {@link StatefulJob}. Both invoke the same RFC modules, but {@link StatefulJob} uses
 * JCoContext.begin and JCoContext.end to specify the stateful calls.
 * 
 * To be able to execute a stateful call sequence distributed over several steps, we register a custom implementation of
 * {@link SessionReferenceProvider} called {@link SimpleSessionReferenceProvider}. The idea behind
 * {@link SimpleSessionReferenceProvider} is simple: each thread holds the current session reference in its local storage. To
 * achieve that, WorkerThread.run sets this session reference before executing the next step and removes it after the step is
 * finished.
 */
public class MultiThreadedStart
{

    public static void main(String[] argv) throws JCoException
    {
        SimpleSessionReferenceProvider sessionReferenceProvider=new SimpleSessionReferenceProvider();
        WorkerThread.setSessionReferenceProvider(sessionReferenceProvider);

        // registering the own implementation will replace the default implementation of JCo runtime.
        // Thus, coupling with some more general session management can be done
        Environment.registerSessionReferenceProvider(sessionReferenceProvider);

        final BlockingQueue<MultiStepJob> queue=new LinkedBlockingQueue<MultiStepJob>();
        runJobs(queue, 5, 2);
    }

    private static void runJobs(BlockingQueue<MultiStepJob> queue, int jobCount, int threadCount) throws JCoException
    {
        System.out.println(">>> Start");

        final JCoDestination destination=JCoDestinationManager
                .getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);

        for (int i=0; i<jobCount; i++)
        {
            queue.add(new StatelessJob(destination, 10));
            queue.add(new StatefulJob(destination, 10));
        }

        CountDownLatch doneSignal=new CountDownLatch(threadCount);
        for (int i=0; i<threadCount; i++)
            new WorkerThread(queue, doneSignal).start();

        System.out.print(">>> Wait ... ");
        try
        {
            doneSignal.await();
        }
        catch (InterruptedException ie)
        {
            // just leave
        }
        System.out.println(">>> Done");
    }
}
