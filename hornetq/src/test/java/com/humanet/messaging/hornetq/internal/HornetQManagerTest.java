package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.DestinationType;
import com.humanet.messaging.hornetq.internal.hornetq.HornetQManagerImpl;
import org.apache.commons.logging.LogFactory;
import org.hornetq.api.jms.management.JMSServerControl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Test
public class HornetQManagerTest {

    private MessagingManager messagingManager;

    @Mock
    private JMSServerControl control;

    @Mock
    private ConnectionFactoryProvider connectionFactoryProvider;

    @BeforeClass
    protected void turnOf_logging() {
        //we need this to initialize the logger
        LogFactory.getLog(HornetQManagerImpl.class);

        for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers()) {
            handler.setLevel(Level.SEVERE);
        }
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        messagingManager = new HornetQManagerImpl(control, connectionFactoryProvider);
    }

    @Test(expectedExceptions = {Exception.class})
    public void when_create_topic_throws_exception_wrapped_exception_is_thrown() throws Exception {
        when(control.createTopic(any(String.class))).thenThrow(new Exception());

        messagingManager.createDestination(DestinationType.Topic, "asd");
    }

    @Test(expectedExceptions = {Exception.class})
    public void when_create_queue_throws_exception_wrapped_exception_is_thrown() throws Exception {
        when(control.createQueue(any(String.class))).thenThrow(new Exception());

        messagingManager.createDestination(DestinationType.Queue, "asd");
    }
}
