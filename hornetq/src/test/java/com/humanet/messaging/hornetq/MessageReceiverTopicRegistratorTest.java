package com.humanet.messaging.hornetq;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

@Test
public class MessageReceiverTopicRegistratorTest {

    MessageReceiverTopicRegistrator registrator;
    @Mock
    private MessagingService messagingService;
    @Mock
    private MessageReceiver messageReceiver;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        registrator = new MessageReceiverTopicRegistrator(messagingService, "topic", messageReceiver);
    }

    @Test
    public void testRegister() throws Exception {
        registrator.register();
        verify(messagingService).registerMessageReceiverForTopic(eq("topic"), eq(messageReceiver));
    }
}
