package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.exceptions.MessagingException;

public interface MessagingService {

    String getLocation();

    MessageSender createMessageSenderForTopic(String topicName) throws MessagingException;

    MessageSender createMessageSenderForQueue(String queueName) throws MessagingException;

    void registerMessageReceiverForTopic(String topicName, MessageReceiver messageReceiver) throws MessagingException;

    void registerMessageReceiverForQueue(String queueName, MessageReceiver messageReceiver) throws MessagingException;

}

