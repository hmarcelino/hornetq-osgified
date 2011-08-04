package com.humanet.messaging.hornetq;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.IOException;

public class MessagingTestCase {

    protected static MessagingSpringContextTestHelper serverContext;

    @BeforeTest
    public void startServer() throws IOException {
        //this class cannot extend SilentTestCase, because the AfterClass turns the logging back on
        new SilentTestCase().turn_Logging_Off();

        serverContext = new MessagingSpringContextTestHelper("server1");
        serverContext.start();
    }

    @AfterTest
    public void setLoggingToOriginalState() throws Exception {
        new SilentTestCase().turn_Logging_Back_On();
    }

}
