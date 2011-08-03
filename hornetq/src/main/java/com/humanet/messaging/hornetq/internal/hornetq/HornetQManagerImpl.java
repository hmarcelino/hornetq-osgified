package com.humanet.messaging.hornetq.internal.hornetq;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.MessageReceiver;
import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.MessagingDestination;
import com.humanet.messaging.hornetq.internal.JmsMessageConsumerAdapter;
import com.humanet.messaging.hornetq.internal.JmsMessageSender;
import com.humanet.messaging.hornetq.internal.MessagingManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.management.JMSServerControl;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

public class HornetQManagerImpl implements MessagingManager, ExceptionListener {

    private static final Log log = LogFactory.getLog(HornetQManagerImpl.class);

    private JMSServerControl control;

    private ConnectionFactory connectionFactory;

    private Connection connection;
    private Session session;

    public HornetQManagerImpl(JMSServerControl control, ConnectionFactory connectionFactory) {
        this.control = control;
        this.connectionFactory = connectionFactory;
    }

    public void init() throws JMSException {
        createSession(createConnection());
    }

    private Connection createConnection() throws JMSException {
        connection = connectionFactory.createConnection();
        connection.setExceptionListener(this);
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        return session;
    }

    @Override
    public void onException(JMSException e) {
        log.error(e);
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

    public boolean existsDestinationWithSameName(DestinationType destinationType, String destinationName) {
        return getAllDestinationNames(destinationType).contains(destinationName);
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

        MessageConsumer consumer = session.createConsumer(getDestination(
                destinationType, destinationName
        ));

        new JmsMessageConsumerAdapter(
                session,
                consumer,
                messageReceiver,
                new MessagingDestination(destinationType, destinationName),
                this
        );
    }

    public MessageSender createMessageSender(DestinationType type, String destinationName)
            throws JMSException {

        MessageProducer producer = session.createProducer(getDestination(type, destinationName));
        return new JmsMessageSender(session, producer, new MessagingDestination(type, destinationName), this);

    }

    public void destroy() {
        try {
            session.close();
            connection.stop();

        } catch (JMSException e) {
            log.error(e.getMessage());
        }
    }

}
