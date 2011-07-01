package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.AbstractMessagingServiceClient;
import com.humanet.messaging.hornetq.MessageReceiver;
import com.humanet.messaging.hornetq.MessagingDestination;
import org.hornetq.api.core.HornetQException;
import org.hornetq.core.logging.Logger;

import javax.jms.*;
import javax.jms.IllegalStateException;

public final class JmsMessageConsumerAdapter extends AbstractMessagingServiceClient {

    private static final Logger log = Logger.getLogger(JmsMessageConsumerAdapter.class);

    private AdapterTask adapterTask;
        
    private class AdapterTask extends Thread {

        private MessageConsumer consumer;
        private MessageReceiver receiver;

        public AdapterTask(MessageConsumer consumer, MessageReceiver receiver) {
            this.consumer = consumer;
            this.receiver = receiver;
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public void run() {

            log.debug("JmsMessageConsumerAdapter: Thread " + this.getName() + " is started.");
            boolean keepInLoop = true;

            while (keepInLoop) {
                try {
                    Message message = consumer.receive();
                    if (message != null) {
                        receiver.receive(((ObjectMessage) message).getObject());

                    } else {
                        log.warn("Received a null message: " + message);
                        reconnectSession();
                    }
                } catch (IllegalStateException e) {
                    log.error(e.getMessage(), e);
                    reconnectSession();
                    //https://issues.jboss.org/browse/HORNETQ-173
                    //acrescentei aqui este reconnect porque se encontrou
                    //um caso em que estavamos presos em ciclo infinito
                    //com a seguinte mensagem
                    //javax.jms.IllegalStateException: Consumer is closed
                } catch (JMSException e) {
                    if (e.getCause() instanceof HornetQException
                            && ((HornetQException) e.getCause()).getCode() == 102) {
                        log.info("Client connection closed");
                        keepInLoop = false;

                    } else {
                        log.error("messaging.error.error-receiving-a-message", e);
                    }
                }
            }
        }
    }

    public JmsMessageConsumerAdapter(Session session, MessageConsumer consumer, MessageReceiver receiver,
                                     MessagingDestination destination, MessagingManager manager) {

        super(session, destination, manager);
        this.adapterTask = new AdapterTask(consumer, receiver);
        this.adapterTask.start();
    }

    public void setConsumer(MessageConsumer consumer) {
        this.adapterTask.consumer = consumer;
    }


    private void reconnectSession() {
        try {
            getMessagingManager().reconnetSession(this);
        } catch (JMSException e) {
            log.error("messaging.error.error-receiving-a-message", e);
        }
    }

}
