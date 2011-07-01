package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.MessageReceiver;
import com.humanet.messaging.hornetq.MessagingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class MessagingServiceImplTest {

    public MessagingService service;

    @Mock
    MessagingManager hqManager;

    @Mock
    MessageReceiver subscriber;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new MessagingServiceImpl(hqManager);
    }

    @Test
    public void create_topic_publisher() throws Exception {
        when(hqManager.existsDestinationWithSameName(null, "theTopic")).thenReturn(false);
        when(hqManager.createDestination(DestinationType.Topic, "theTopic")).thenReturn(true);

        service.createMessageSenderForTopic("theTopic");
    }


    @Test()
    public void create_topic_publisher_for_topic_that_already_exists() throws Exception {
        when(hqManager.existsDestinationWithSameName(DestinationType.Topic, "theTopic"))
                .thenReturn(true);

        service.createMessageSenderForTopic("theTopic");

        verify(hqManager, never()).createDestination(DestinationType.Topic, "theTopic");
    }


    @Test
    public void register_topic_subscriber() throws Exception {
        when(hqManager.existsDestinationWithSameName(DestinationType.Topic, "theTopic"))
                .thenReturn(false);
        when(hqManager.createDestination(DestinationType.Topic, "theTopic")).thenReturn(true);

        service.registerMessageReceiverForTopic("theTopic", subscriber);
    }

    @Test
    public void create_queue_publisher() throws Exception {
        when(hqManager.existsDestinationWithSameName(DestinationType.Queue, "theQueue"))
                .thenReturn(false);
        when(hqManager.createDestination(DestinationType.Queue, "theQueue")).thenReturn(true);

        service.createMessageSenderForQueue("theQueue");
    }

    @Test()
    public void create_queue_publisher_for_queue_that_already_exists() throws Exception {
        when(hqManager.existsDestinationWithSameName(DestinationType.Queue, "theQueue"))
                .thenReturn(true);

        service.createMessageSenderForQueue("theQueue");

        verify(hqManager, never()).createDestination(DestinationType.Queue, "theQueue");
    }

    @Test
    public void register_queue_subscriber() throws Exception {
        when(hqManager.existsDestinationWithSameName(DestinationType.Queue, "theQueue"))
                .thenReturn(false);
        when(hqManager.createDestination(DestinationType.Queue, "theQueue")).thenReturn(true);

        service.registerMessageReceiverForQueue("theQueue", subscriber);
    }
}
