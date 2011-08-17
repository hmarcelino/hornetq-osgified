package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.MessageReceiver;
import com.humanet.messaging.hornetq.MessageSender;

import javax.jms.JMSException;

public interface MessagingManager {

    public boolean existsDestinationWithSameName(DestinationType destinationType, String destinationName);

    public boolean createDestination(DestinationType destinationType, String destinationName) throws Exception;

    public void registerMessageReceiver(DestinationType destinationType, String destinationName, MessageReceiver messageReceiver) throws JMSException;

    public MessageSender createMessageSender(DestinationType type, String destinationName) throws JMSException;

    public void destroy();
}
