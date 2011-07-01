package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.events.Event;
import com.humanet.messaging.hornetq.events.EventListener;
import com.humanet.messaging.hornetq.exceptions.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ForwardingEventListener implements EventListener {

    private static final Log log = LogFactory.getLog(ForwardingEventListener.class);

    private MessagingService messagingService;
    private String topic;

    private MessageSender sender;

    public ForwardingEventListener(MessagingService messagingService, String topic) {
        this.messagingService = messagingService;
        this.topic = topic;
    }

    @Override
    public void onEvent(Event event) {

        setSourceLocation(event);

        try {
            forwardToTopic(event);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setSourceLocation(Event event) {
        if (event instanceof LocationAware) {
            ((LocationAware) event).setCurrentLocation(messagingService.getLocation());
        }
    }

    private void forwardToTopic(Event event) throws MessagingException {
        log.debug("sending event " + event);
        //noinspection unchecked
        getMessageSenderInstance().send(event);
    }

    private MessageSender getMessageSenderInstance() throws MessagingException {
        if (sender == null) {
            log.debug("No sender found. Creating new sender.");
            sender = messagingService.createMessageSenderForTopic(topic);
        }
        return sender;
    }
}
