package com.humanet.messaging.hornetq.events;

public interface EventListener {

    void onEvent(Event event);

    static EventListener NULL_LISTENER = new EventListener() {
        @Override
        public void onEvent(Event event) {
            //does nothing
        }
    };
}
