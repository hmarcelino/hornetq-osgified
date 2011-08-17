package com.humanet.messaging.hornetq.internal;

import org.hornetq.api.core.HornetQException;

import javax.jms.ConnectionFactory;

/**
 * User: Hugo Marcelino
 * Date: 3/8/11
 */
public interface ConnectionFactoryProvider {

    public ConnectionFactory getConnectionFactory(String name) throws HornetQException;

}
