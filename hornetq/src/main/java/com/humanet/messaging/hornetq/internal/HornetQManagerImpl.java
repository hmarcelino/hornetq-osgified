package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.management.JMSServerControl;

import javax.jms.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HornetQManagerImpl implements MessagingManager, ExceptionListener {

    private static final Log log = LogFactory.getLog(HornetQManagerImpl.class);

    private JMSServerControl control;
    private Connection connection;

    private Map<Integer, MessageClient> sessions = new HashMap<Integer, MessageClient>();

    public HornetQManagerImpl(JMSServerControl control, Connection connection) throws JMSException {
        this.control = control;
        this.connection = connection;

        if (connection != null) {
            connection.setExceptionListener(this);
        } else {
            throw new JMSException("Connection is null. This is not acceptable!", "jms.error.connection-is-null");
        }
    }

    @Override
    public void onException(JMSException e) {
        log.error(e);
    }

    public boolean createDestination(DestinationType destinationType, String destinationName) throws Exception {

        boolean success;

        switch (destinationType) {
            case Topic:
                success = control.createTopic(destinationName);
                break;
            case Queue:
                success = control.createQueue(destinationName);
                break;
            default:
                throw new IllegalArgumentException(
                        "MessagingDestination type " + destinationType.name() + " not supported"
                );
        }
        return success;
    }

    public boolean existsDestinationWithSameName(DestinationType destinationType, String destinationName) {
        return getAllDestinationNames(destinationType).contains(destinationName);
    }

    private List<String> getAllDestinationNames(DestinationType destinationType) {
        switch (destinationType) {
            case Topic:
                return Arrays.asList(control.getTopicNames());
            case Queue:
                return Arrays.asList(control.getQueueNames());
            default:
                //My choice for an Runtime Exception has to do with the fact that this is a get.
                //and it is awkward to return a checked exception
                throw new IllegalArgumentException(
                        "MessagingDestination type " + destinationType.name() + " not supported"
                );
        }
    }

    public void registerMessageReceiver(DestinationType destinationType,
                                        String destinationName,
                                        MessageReceiver messageReceiver) throws JMSException {

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(getDestination(
                destinationType, destinationName
        ));

        JmsMessageConsumerAdapter adapter = new JmsMessageConsumerAdapter(
                session,
                consumer,
                messageReceiver,
                new MessagingDestination(destinationType, destinationName),
                this
        );

        sessions.put(adapter.hashCode(), adapter);
    }

    public MessageSender createMessageSender(DestinationType type, String destinationName)
            throws JMSException {

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(getDestination(type, destinationName));

        MessageSender sender = new JmsMessageSender(session, producer,
                new MessagingDestination(type, destinationName), this);

        sessions.put(sender.hashCode(), sender);

        return sender;

    }

    private Destination getDestination(DestinationType destinationType, String destinationName) {
        Destination destination;

        switch (destinationType) {
            case Topic:
                destination = HornetQJMSClient.createTopic(destinationName);
                break;
            case Queue:
                destination = HornetQJMSClient.createQueue(destinationName);
                break;
            default:
                throw new IllegalArgumentException(
                        "MessagingDestination type " + destinationType.name() + " not supported"
                );
        }
        return destination;
    }


    public void reconnetSession(MessageSender sender) throws JMSException {

        //close and invalidate current session
        closeClientSession(sender);

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(getDestination(
                sender.getType(), sender.getDestinationName()
        ));
        sender.setSession(session);
        ((JmsMessageSender) sender).setProducer(producer);
    }


    public void reconnetSession(JmsMessageConsumerAdapter adapter) throws JMSException {

        //close and invalidate current session
        closeClientSession(adapter);

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(getDestination(adapter.getType(),
                adapter.getDestinationName()));
        adapter.setSession(session);
        adapter.setConsumer(consumer);
    }


    private void closeClientSession(MessageClient client) throws JMSException {
        client.invalidateSession();
        sessions.remove(client.hashCode());
    }


    public void destroy() {
        try {
            //close all sessions
            for (MessageClient client : sessions.values()) {
                closeClientSession(client);
            }
            sessions.clear();

        } catch (JMSException e) {
            log.error(e.getMessage());
        }
    }
}
