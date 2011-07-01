package com.humanet.messaging.hornetq.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class can be composed onto other classes to give basic support for fireing events
 */
public class EventPublisherSupport implements EventPublisher {
    private static final Log log = LogFactory.getLog(EventPublisherSupport.class);

    List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();

    @Override
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void fireEvent(Event event) {
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (RuntimeException e) {
                log.error("Unexpected exception in listener", e);
            }
        }
    }

    public void clearListeners() {
        listeners.clear();
    }
}
