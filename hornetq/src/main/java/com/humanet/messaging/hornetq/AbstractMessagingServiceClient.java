package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.internal.MessagingManager;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * AbstractMessagingServiceClient
 *
 * @author ptraca
 * @version 1.0
 */
public abstract class AbstractMessagingServiceClient implements MessageClient{

    private MessagingDestination    destination;
    private Session                 session;
    private MessagingManager        mmanager;

    protected AbstractMessagingServiceClient(Session session, MessagingDestination destination,
                                             MessagingManager manager) {
        this.destination    = destination;
        this.session        = session;
        this.mmanager       = manager;
    }

    @Override
    public DestinationType getType() {
        return destination.getType();
    }

    @Override
    public String getDestinationName() {
        return destination.getDestinationName();
    }

    public MessagingDestination getDestination() {
        return destination;
    }

    public Session getSession() {
        return session;
    }

    public MessagingManager getMessagingManager() {
        return mmanager;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void invalidateSession() throws JMSException {
        if(session != null)
            session.close();
        session = null;
    }

    public boolean hasValidSession(){
        return session != null;
    }
}//AbstractMessagingServiceClient
