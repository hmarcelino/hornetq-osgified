package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.internal.MessagingManager;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * MessagingServiceClient
 *
 * @author ptraca
 * @version 1.0
 */
public interface MessageClient {

    MessagingDestination getDestination();

    DestinationType getType();

    String getDestinationName();

    Session getSession();

    void setSession(Session session);

    void invalidateSession() throws JMSException;

    boolean hasValidSession();

    MessagingManager getMessagingManager();

}//MessagingServiceClient
