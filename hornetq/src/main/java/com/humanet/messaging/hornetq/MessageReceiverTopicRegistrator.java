package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.exceptions.MessagingException;

import javax.annotation.PostConstruct;

public class MessageReceiverTopicRegistrator {
    private MessagingService messagingService;
    private String topic;
    private MessageReceiver messageReceiver;

    public MessageReceiverTopicRegistrator(MessagingService service, String topic, MessageReceiver receiver) {
        this.messagingService = service;
        this.topic = topic;
        this.messageReceiver = receiver;

        setLocationIfNeeded(receiver, service.getLocation());
    }

    private void setLocationIfNeeded(MessageReceiver receiver, String location) {
        if (receiver instanceof LocationAware)
            ((LocationAware) receiver).setCurrentLocation(location);
    }

    @PostConstruct
    public void register() throws MessagingException {
        messagingService.registerMessageReceiverForTopic(topic, messageReceiver);
    }
}
