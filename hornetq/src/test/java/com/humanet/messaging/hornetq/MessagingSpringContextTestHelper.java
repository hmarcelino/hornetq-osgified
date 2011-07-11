package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.internal.MessagingManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.IOException;

public class MessagingSpringContextTestHelper {

    private String suffixSpringConfig;

    private FileSystemXmlApplicationContext testApplicationContext;
    private MessagingService messagingService;
    private MessagingManager messagingManager;

    public MessagingSpringContextTestHelper(String suffixSpringConfig) throws IOException {
        this.suffixSpringConfig = suffixSpringConfig;
    }

    public void start() {
        testApplicationContext = new FileSystemXmlApplicationContext(
                "file:src/main/resources/META-INF/spring/bundle-hornetq.xml",
//                "file:src/main/resources/META-INF/spring/bundle-context.xml",
                "file:src/test/resources/META-INF/spring/bundle-context-test-" + suffixSpringConfig + ".xml"
        );

        messagingService    = (MessagingService) testApplicationContext.getBean("messagingService");
        messagingManager = (MessagingManager) testApplicationContext.getBean("hornetQManager");
    }

    public ApplicationContext getSpringApplicationContext() {
        return testApplicationContext;
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

    public MessagingManager getMessagingManager() {
        return messagingManager;
    }

}