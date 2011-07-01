package com.humanet.messaging.hornetq.internal;

import com.humanet.messaging.hornetq.SilentTestCase;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hornetq.jms.server.JMSServerManager;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.*;

public class HornetQServerTest extends SilentTestCase {

    @Test
    public void test_server_netty() throws Exception {
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(
                "file:src/main/resources/META-INF/spring/bundle-hornetq.xml",
                "file:src/test/resources/META-INF/spring/bundle-context-test-netty.xml"
        );

        JMSServerManager server = (JMSServerManager) applicationContext.getBean("jmsServerManager");
        assertTrue(server.isStarted());
        assertThat(server, IsListeningOnPort(5445));

        server.stop();
        applicationContext.destroy();

        Thread.sleep(2000);

        assertFalse(server.isStarted());

        //Wait to make sure the server is stoped
        assertThat(server, not(IsListeningOnPort(5445)));
    }

    private Matcher<JMSServerManager> IsListeningOnPort(final int port) {
        return new TypeSafeMatcher<JMSServerManager>() {
            @Override
            public boolean matchesSafely(JMSServerManager server) {
                return canWeConnectToPort(port);
            }

            private boolean canWeConnectToPort(int port) {
                Socket socket = null;
                try {
                    InetAddress addr = InetAddress.getByName(
                            Inet4Address.getLocalHost().getHostAddress()
                    );
                    SocketAddress sockaddr = new InetSocketAddress(addr, port);

                    socket = new Socket();
                    socket.connect(sockaddr);
                } catch (IOException e) {
                    return false;

                } finally {
                    try {
                        if (socket != null) socket.close();
                    } catch (IOException e) {
                        fail("We couldn't close the connection to the hornetq messaging server", e);
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a server listening on port " + port);
            }
        };
    }
}
