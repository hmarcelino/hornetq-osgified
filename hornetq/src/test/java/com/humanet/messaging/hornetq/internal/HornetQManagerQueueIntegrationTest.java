package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.DummyMessageReceiver;
import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.MessagingTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class HornetQManagerQueueIntegrationTest extends MessagingTestCase {

    private MessagingManager mmanager;

    @BeforeMethod
    protected void setUp() throws Exception {
        mmanager = serverContext.getMessagingManager();
    }

    @Test
    public void createQueue() throws Exception {
        mmanager.createDestination(DestinationType.Queue, "queue");
    }


    @Test(dependsOnMethods = {"createQueue"})
    public void check_queue_with_name() {
        assertTrue(mmanager.existsDestinationWithSameName(DestinationType.Queue, "queue"));
    }

    @Test
    public void send_text_message() throws Exception {

        String queueName = "queue";

        if (!mmanager.existsDestinationWithSameName(DestinationType.Queue, queueName)) {
            mmanager.createDestination(DestinationType.Queue, queueName);
        }

        DummyMessageReceiver messageReceiver = new DummyMessageReceiver();
        mmanager.registerMessageReceiver(DestinationType.Queue, queueName, messageReceiver);

        MessageSender messageSender = mmanager.createMessageSender(DestinationType.Queue, queueName);

        //noinspection unchecked
        messageSender.send("Ola messageReceiver");

        Thread.sleep(500);

        assertEquals("Ola messageReceiver", messageReceiver.getTextMessage());
    }

}
