package com.humanet.messaging.hornetq;

/**
 * @author jone
 */
public interface LocationAware {
    void setCurrentLocation(String location);

    boolean isFromLocation(String location);
}
