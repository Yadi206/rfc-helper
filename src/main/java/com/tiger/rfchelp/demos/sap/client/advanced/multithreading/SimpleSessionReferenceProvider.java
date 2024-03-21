package com.tiger.rfchelp.demos.sap.client.advanced.multithreading;

import java.util.Collection;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionEventListener;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;

public class SimpleSessionReferenceProvider implements SessionReferenceProvider
{
    private SessionEventListener el;

    @Override
    public JCoSessionReference getCurrentSessionReference(String scopeType)
    {
        SimpleSessionReference sesRef=WorkerThread.localSessionReference.get();
        if (sesRef!=null)
            return sesRef;

        throw new RuntimeException("Unknown thread:"+Thread.currentThread().getId());
    }

    @Override
    public boolean isSessionAlive(String sessionId)
    {
        Collection<SimpleSessionReference> availableSessions=WorkerThread.sessions.values();
        for (SimpleSessionReference ref : availableSessions)
        {
            if (ref.getID().equals(sessionId))
                return true;
        }
        return false;
    }

    @Override
    public void jcoServerSessionContinued(String sessionID) throws SessionException
    {
    }

    @Override
    public void jcoServerSessionFinished(String sessionID)
    {
    }

    @Override
    public void jcoServerSessionPassivated(String sessionID) throws SessionException
    {
    }

    @Override
    public JCoSessionReference jcoServerSessionStarted() throws SessionException
    {
        return null;
    }

    @Override
    public boolean supportsEvents()
    {
        // return false, if you not intend or not able send events if a session is finished
        return true;
    }

    @Override
    public void setSessionEventListener(SessionEventListener eventListener)
    {
        // save the event listener instance
        el=eventListener;
    }

    public void fireServerFinishedEvent(String sessionID)
    {
        el.finished(sessionID);
    }
}
