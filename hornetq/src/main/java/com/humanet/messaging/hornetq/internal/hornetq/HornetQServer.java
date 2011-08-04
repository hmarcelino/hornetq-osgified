package com.humanet.messaging.hornetq.internal.hornetq;

import com.humanet.messaging.hornetq.internal.JmsServer;
import org.hornetq.integration.spring.SpringJmsBootstrap;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;

public class HornetQServer extends SpringJmsBootstrap implements JmsServer {

    public String getNodeId() {
        return serverManager.getHornetQServer().getNodeID().toString();
    }

    public JMSServerManagerImpl getServerManager() {
        return serverManager;
    }

}
