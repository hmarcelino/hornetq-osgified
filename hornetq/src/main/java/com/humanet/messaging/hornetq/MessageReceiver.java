package com.humanet.messaging.hornetq;

import java.io.Serializable;

public interface MessageReceiver<T extends Serializable> {

    public void receive(T t);

}//MessageReceiver
