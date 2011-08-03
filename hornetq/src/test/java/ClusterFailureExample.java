import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class ClusterFailureExample {

    private Map<String, Queue> queuesMap = new HashMap<String, Queue>();
    protected Connection connection;

    private Session createSession(String host, int port) throws JMSException {

        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(TransportConstants.HOST_PROP_NAME, host);
        connectionParams.put(TransportConstants.PORT_PROP_NAME, port);

        HornetQConnectionFactory connectionFactory = HornetQJMSClient.createConnectionFactoryWithoutHA(
                JMSFactoryType.CF,
                new TransportConfiguration(
                        NettyConnectorFactory.class.getName(),
                        connectionParams
                )
        );

        connectionFactory.setConsumerWindowSize(0);

        connection = connectionFactory.createConnection();
        connection.start();

        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public Queue getQueue(String queueName) {

        if (!queuesMap.containsKey(queueName)) {
            Queue queue = HornetQJMSClient.createQueue(queueName);
            queuesMap.put(queueName, queue);

            return queue;
        }

        return queuesMap.get(queueName);
    }

    private AProducer createProducerForQueue(Session session, String queueName) throws JMSException {
        return new AProducer(
                session,
                session.createProducer(getQueue(queueName))
        );
    }

    private AConsumer createConsumerForQueue(Session session, String queueName) throws JMSException {
        return new AConsumer(session,
                session.createConsumer(getQueue(queueName))
        );
    }

    public class AProducer {
        private Session session;
        private MessageProducer producer;

        public AProducer(Session session, MessageProducer producer) {
            this.session = session;
            this.producer = producer;
        }

        public void send(int numOfMessages) throws JMSException {
            for (int i = 0; i < numOfMessages; i++) {
                System.out.println("Sending message " + (i + 1));

                TextMessage textMessage = session.createTextMessage("A simple message " + (i + 1));
                producer.send(textMessage);
            }
        }
    }

    public class AConsumer {
        private Session session;
        private MessageConsumer messageConsumer;
        private int received;

        public AConsumer(Session session, MessageConsumer messageConsumer) {
            this.session = session;
            this.messageConsumer = messageConsumer;
        }

        public void resetReceivedCounter() {
            received = 0;
        }

        public int getReceived() {
            return received;
        }

        public void receive(int numberOfMessages) throws JMSException {
            int counter = numberOfMessages;
            received = 0;

            try {
                while (counter > 0) {
                    TextMessage message = (TextMessage) messageConsumer.receive(1000);
                    System.out.println("message received with Text = " + message.getText());
                    counter--;
                    received++;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws JMSException {
        ClusterFailureExample clusterFailureExampleExample = new ClusterFailureExample();

        Session session = clusterFailureExampleExample.createSession("192.168.1.76", 5445);

        int numOfMessages = 10000;
        AConsumer consumer = null;
        try {
            clusterFailureExampleExample.createProducerForQueue(session, "aQueue").send(numOfMessages);

            consumer = clusterFailureExampleExample.createConsumerForQueue(session, "aQueue");
            consumer.receive(numOfMessages);

        } catch (JMSException e) {
            //ignore
            e.printStackTrace();

        } catch (NullPointerException npe) {
            //ignore
            npe.printStackTrace();
        }

        System.out.println("num of messages received : " + consumer.getReceived());

        session.close();
        clusterFailureExampleExample.connection.close();
    }

}
