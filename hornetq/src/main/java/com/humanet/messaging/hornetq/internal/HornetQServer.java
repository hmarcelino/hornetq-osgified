package com.humanet.messaging.hornetq.internal;

import org.hornetq.integration.spring.SpringJmsBootstrap;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;

public class HornetQServer extends SpringJmsBootstrap {

    public String getNodeId() {
        return serverManager.getHornetQServer().getNodeID().toString();
    }

    public JMSServerManagerImpl getServerManager() {
        return serverManager;
    }

}
