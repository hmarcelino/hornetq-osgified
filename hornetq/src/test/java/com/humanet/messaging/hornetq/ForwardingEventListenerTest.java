package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.events.Event;
import com.humanet.messaging.hornetq.exceptions.MessagingException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

@Test
public class ForwardingEventListenerTest {

    private ForwardingEventListener listener;
    @Mock
    private MessagingService messagingService;
    @Mock
    private MessageSender messageSender;

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        listener = new ForwardingEventListener(messagingService, "topic");
    }

    @Test
    public void on_event() throws MessagingException {
        when(messagingService.createMessageSenderForTopic("topic")).thenReturn(messageSender);
        listener.onEvent(Event.NULL_EVENT);

        verify(messageSender).send(eq(Event.NULL_EVENT));
    }

    @Test
    public void on_second_event_reuse_message_sender() throws MessagingException {
        when(messagingService.createMessageSenderForTopic("topic")).thenReturn(messageSender);
        listener.onEvent(Event.NULL_EVENT);
        listener.onEvent(Event.NULL_EVENT);

        verify(messagingService, times(1)).createMessageSenderForTopic("topic");
        verify(messageSender, times(2)).send(eq(Event.NULL_EVENT));
    }
}
