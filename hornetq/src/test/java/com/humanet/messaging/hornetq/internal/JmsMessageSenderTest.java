package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.MessagingDestination;
import com.humanet.messaging.hornetq.exceptions.MessagingException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Serializable;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

@Test
public class JmsMessageSenderTest {

    private JmsMessageSender sender;

    @Mock
    private Session session;
    @Mock
    private MessageProducer producer;
    @Mock
    private MessagingDestination destination;
    @Mock
    private MessagingManager mmanager;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sender = new JmsMessageSender(session, producer, destination, mmanager);
    }

    @Test(expectedExceptions = {MessagingException.class})
    public void when_send_throws_JMSException_wrap_with_MessagingException_and_throw() throws Exception {
        doThrow(new JMSException("")).when(producer).send(any(Message.class));

        sender.send(new DummyMessage());
    }

    class DummyMessage implements Serializable {

    }
}
