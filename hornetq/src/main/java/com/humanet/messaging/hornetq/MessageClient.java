package com.humanet.messaging.hornetq;

import javax.jms.Session;

/**
 * MessagingServiceClient
 *
 * @author ptraca
 * @version 1.0
 */
public interface MessageClient {

    Session getSession();

}
