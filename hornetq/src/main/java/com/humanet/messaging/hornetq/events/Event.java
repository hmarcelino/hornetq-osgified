package com.humanet.messaging.hornetq.events;

import java.io.Serializable;

public interface Event extends Serializable {

    static final Event NULL_EVENT = new NullEvent();

    static final class NullEvent implements Event {
        @Override
        public String toString() {
            return "NULL_EVENT";
        }
    }
}
