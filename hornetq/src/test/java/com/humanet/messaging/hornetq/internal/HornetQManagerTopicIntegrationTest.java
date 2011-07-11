package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.DummyMessageReceiver;
import com.humanet.messaging.hornetq.MessageSender;
import com.humanet.messaging.hornetq.MessagingTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class HornetQManagerTopicIntegrationTest extends MessagingTestCase {

    private MessagingManager messagingManager;

    @BeforeClass
    protected void setUp() throws Exception {
        messagingManager = serverContext.getMessagingManager();
    }


    @Test
    public void createTopic() throws Exception {
        messagingManager.createDestination(DestinationType.Topic, "createTopic-topic");
        assertTrue(
                messagingManager.existsDestinationWithSameName(
                        DestinationType.Topic,
                        "createTopic-topic"
                )
        );
    }

    @Test
    public void check_topic_with_name() throws Exception {
        messagingManager.createDestination(DestinationType.Topic, "check_topic_with_name");
        assertTrue(
                messagingManager.existsDestinationWithSameName(
                        DestinationType.Topic,
                        "check_topic_with_name"
                )
        );
    }

    @Test
    public void send_text_message() throws Exception {

        String topicName = "MyTopic";

        if (!messagingManager.existsDestinationWithSameName(DestinationType.Topic, topicName)) {
            messagingManager.createDestination(DestinationType.Topic, topicName);
        }

        DummyMessageReceiver messageReceiver = new DummyMessageReceiver();
        messagingManager.registerMessageReceiver(
                DestinationType.Topic,
                topicName,
                messageReceiver
        );

        MessageSender messageSender = messagingManager.createMessageSender(
                DestinationType.Topic,
                topicName
        );
        //noinspection unchecked
        messageSender.send("Ola messageReceiver");

        Thread.sleep(500);

        assertEquals("Ola messageReceiver", messageReceiver.getTextMessage());
    }

}
