package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.exceptions.MessagingException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Serializable;

public class JmsMessageSender<T extends Serializable> implements MessageSender<T> {

    private Session session;
    private MessageProducer producer;

    public JmsMessageSender(Session session, MessageProducer producer) {
        this.session = session;
        this.producer = producer;
    }

    public void send(T message) throws MessagingException {
        try {
            producer.send(session.createObjectMessage(message));
        } catch (JMSException e) {
            throw new MessagingException("messaging.error.not-possible-to-send-message", e);
        }
    }

    public void shutdown() throws MessagingException {
        try {
            producer.close();
        } catch (JMSException e) {
            throw new MessagingException(e.getErrorCode(), e);
        }
    }

}