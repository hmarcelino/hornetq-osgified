package com.humanet.messaging.hornetq;

public class DummyMessageReceiver implements MessageReceiver<String> {

    private String textMessage;
    private int messageCounter = 0;

    public String getTextMessage() {
        return textMessage;
    }

    public void resetCounter() {
        messageCounter = 0;
    }

    public int getCounter() {
        return messageCounter;
    }

    @Override
    public void receive(String textMessage) {
        messageCounter++;
        this.textMessage = textMessage;
    }

}