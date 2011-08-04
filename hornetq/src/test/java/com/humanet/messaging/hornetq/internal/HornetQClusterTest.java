package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.*;
import com.humanet.messaging.hornetq.exceptions.MessagingException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class HornetQClusterTest extends MessagingTestCase {

    private static final String TEST_MSG = "Ola clustered subscribers";

    public MessagingSpringContextTestHelper serverContext_2;

    private MessageSender publisher;
    private DummyMessageReceiver server1Subscriber, server2Subscriber;

    @BeforeClass
    private void startServer_2() throws InterruptedException, MessagingException, IOException {
        MessagingService messagingServiceServer1 = (MessagingService) serverContext.getSpringApplicationContext().getBean("messagingService");
        server1Subscriber = new DummyMessageReceiver();
        messagingServiceServer1.registerMessageReceiverForTopic("clusteredTopic", server1Subscriber);

        publisher = messagingServiceServer1.createMessageSenderForTopic("clusteredTopic");

        serverContext_2 = new MessagingSpringContextTestHelper("server2");
        serverContext_2.start();

        MessagingService messagingServiceServer2 = (MessagingService) serverContext_2.getSpringApplicationContext().getBean("messagingService");
        server2Subscriber = new DummyMessageReceiver();
        messagingServiceServer2.registerMessageReceiverForTopic("clusteredTopic", server2Subscriber);
    }

    @Test
    public void make_sure_subscribers_are_notified_in_a_cluster_env() throws MessagingException, InterruptedException {
        publisher.send(TEST_MSG);

        //Wait to receive the message
        Thread.sleep(500);

        assertEquals(server1Subscriber.getTextMessage(), TEST_MSG, "Subscriber1 should have received the message");
        assertEquals(server2Subscriber.getTextMessage(), TEST_MSG, "Subscriber1 should have received the message");
    }

    @Test
    public void send_1000_messages() throws MessagingException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            publisher.send(TEST_MSG);
        }

        //Wait to receive all messages
        Thread.sleep(500);

        assertEquals(server1Subscriber.getCounter(), 1000, "Subscriber1 should have received 1000 messages");
        assertEquals(server2Subscriber.getCounter(), 1000, "Subscriber2 should have received 1000 messages");
    }

    @AfterMethod
    public void resetCounters() {
        server1Subscriber.resetCounter();
        server2Subscriber.resetCounter();
    }

}
