package com.humanet.messaging.hornetq.events;

/**
 * This should be implemented by all classes that wish to publish events
 */
public interface EventPublisher {

    void addListener(EventListener listener);
}
