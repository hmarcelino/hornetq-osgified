package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.MessageReceiver;
import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.MessagingService;
import com.humanet.messaging.hornetq.exceptions.MessagingException;

import javax.jms.JMSException;

/**
 * Internal implementation of our example OSGi service
 */
public final class MessagingServiceImpl implements MessagingService {

    private MessagingManager mmanager;

    public MessagingServiceImpl(MessagingManager manager) {
        this.mmanager = manager;
    }

    @Override
    public String getLocation() {
        return mmanager.getServerLocation();
    }


    @Override
    public MessageSender createMessageSenderForTopic(String topicName) throws MessagingException {

        try {
            createIfNotAvailable(DestinationType.Topic, topicName);
            return mmanager.createMessageSender(DestinationType.Topic, topicName);
        } catch (JMSException e) {
            throw new MessagingException("messaging.error.error-creating-topic-publisher", e);
        }
    }


    @Override
    public MessageSender createMessageSenderForQueue(String queueName) throws MessagingException {

        try {
            createIfNotAvailable(DestinationType.Queue, queueName);
            return mmanager.createMessageSender(DestinationType.Queue, queueName);
        } catch (JMSException e) {
            throw new MessagingException("messaging.error.error-creating-queue-publisher", e);
        }
    }


    @Override
    public void registerMessageReceiverForTopic(String topicName, MessageReceiver messageReceiver)
            throws MessagingException {

        try {
            createIfNotAvailable(DestinationType.Topic, topicName);
            mmanager.registerMessageReceiver(DestinationType.Topic, topicName, messageReceiver);
        } catch (JMSException e) {
            throw new MessagingException("messaging.error.error-creating-topic-subscriber", e);
        }
    }


    @Override
    public void registerMessageReceiverForQueue(String queueName, MessageReceiver messageReceiver)
            throws MessagingException {

        try {
            createIfNotAvailable(DestinationType.Queue, queueName);
            mmanager.registerMessageReceiver(DestinationType.Queue, queueName, messageReceiver);
        } catch (JMSException e) {
            throw new MessagingException("messaging.error.error-creating-queue-subscriber", e);
        }
    }


    private void createIfNotAvailable(DestinationType type, String destinationName)
            throws MessagingException {

        if (!mmanager.existsDestinationWithSameName(type, destinationName)) {
            createDestination(type, destinationName);
        }
    }

    private void createDestination(DestinationType destinationType, String destinationName)
            throws MessagingException {

        boolean success;

        try {
            success = mmanager.createDestination(destinationType, destinationName);
        } catch (Exception e) {
            throw new MessagingException("messaging.error.cant-create-topic", e);
        }

        if (!success) {
            throw new MessagingException(
                    "messaging.error.cant-create-" + destinationType.name().toLowerCase(),
                    "Can't create " + destinationType.name().toLowerCase()
                            + " possibly because a " + destinationType.name().toLowerCase()
                            + " with name " + destinationName + " already exist");
        }

    }
}//MessagingServiceImpl

