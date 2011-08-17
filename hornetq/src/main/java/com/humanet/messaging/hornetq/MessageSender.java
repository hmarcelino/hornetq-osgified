package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.exceptions.MessagingException;

import java.io.Serializable;

public interface MessageSender<T extends Serializable> {

    public void send(T t) throws MessagingException;

    public void shutdown() throws MessagingException;
}
