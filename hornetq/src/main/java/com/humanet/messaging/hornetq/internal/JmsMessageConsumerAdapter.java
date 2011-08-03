package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.MessageReceiver;
import org.hornetq.api.core.HornetQException;
import org.hornetq.core.logging.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

public final class JmsMessageConsumerAdapter {

    private static final Logger log = Logger.getLogger(JmsMessageConsumerAdapter.class);

    private AdapterTask task;

    public JmsMessageConsumerAdapter(MessageConsumer consumer, MessageReceiver receiver) {
        task = new AdapterTask(consumer, receiver);
        task.start();
    }

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
                    }

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

    public void stop() {
        log.info("Stopping consumer thread " + task.getName());
        task.interrupt();
    }

}
