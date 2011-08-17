package com.humanet.messaging.hornetq;

import com.humanet.messaging.hornetq.internal.MessagingManager;
import org.hornetq.jms.server.JMSServerManager;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.AfterClass;

import java.io.IOException;

public class MessagingSpringContextTestHelper {

    private String conf;

    private FileSystemXmlApplicationContext applicationContext;
    private MessagingService messagingService;
    private MessagingManager messagingManager;
    private JMSServerManager serverManager;

    public MessagingSpringContextTestHelper(String conf) throws IOException {
        this.conf = conf;
    }

    public void start() {
        applicationContext = new FileSystemXmlApplicationContext(
                "file:src/main/resources/META-INF/spring/bundle-hornetq.xml",
                "file:src/main/resources/META-INF/spring/bundle-context.xml",
                "file:src/test/resources/META-INF/" + conf + "/bundle-context.xml"
        );

        messagingService = (MessagingService) applicationContext.getBean("messagingService");
        messagingManager = (MessagingManager) applicationContext.getBean("messagingManager");
        serverManager = (JMSServerManager) applicationContext.getBean("serverManager");
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

    public MessagingManager getMessagingManager() {
        return messagingManager;
    }

    public JMSServerManager getServerManager() {
        return serverManager;
    }

    public void stop() {
        applicationContext.destroy();
    }

}