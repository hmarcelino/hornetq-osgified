package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.AbstractMessagingServiceClient;
import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.MessagingDestination;
import com.humanet.messaging.hornetq.exceptions.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Serializable;

public class JmsMessageSender<T extends Serializable>
        extends AbstractMessagingServiceClient implements MessageSender<T> {

    private static final Log log = LogFactory.getLog(JmsMessageSender.class);

    private MessageProducer producer;

    public JmsMessageSender(Session session, MessageProducer producer, MessagingDestination destination,
                            MessagingManager manager) {
        super(session, destination, manager);
        this.producer = producer;
    }


    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

    @Override
    public void send(T message) throws MessagingException {

        int retryCount = 0, exit = 0;

        while (exit != 1 && retryCount < 3) {
            try {
                producer.send(getSession().createObjectMessage(message));
                exit = 1;
            } catch (IllegalStateException e) {
                log.warn(e);
                try {
                    getMessagingManager().reconnetSession(this);
                } catch (JMSException e1) {
                    log.error(e);
                    throw new MessagingException("messaging.error.not-possible-to-send-message", e);
                }
            } catch (JMSException e) {
                throw new MessagingException("messaging.error.not-possible-to-send-message", e);
            }
        }
    }
}