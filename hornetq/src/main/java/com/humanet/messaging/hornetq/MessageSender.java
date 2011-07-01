package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.exceptions.MessagingException;

import java.io.Serializable;

public interface MessageSender<T extends Serializable> extends MessageClient {

    public void send(T t) throws MessagingException;

}//MessageSender
