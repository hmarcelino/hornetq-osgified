package com.humanet.messaging.hornetq;

/**
 * MessagingDestination
 *
 * @author ptraca
 * @version 1.0
 */
public class MessagingDestination {

    private DestinationType type;
    private String destinationName;

    public MessagingDestination(DestinationType type, String destinationName) {
        this.type = type;
        this.destinationName = destinationName;
    }

    public DestinationType getType() {
        return type;
    }

    public String getDestinationName() {
        return destinationName;
    }
}//MessagingDestination
