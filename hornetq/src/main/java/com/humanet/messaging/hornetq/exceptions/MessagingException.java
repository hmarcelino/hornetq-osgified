package com.humanet.messaging.hornetq.exceptions;


/**
 * If an error occurred while performing some action
 */
public class MessagingException extends Exception {

    private String errorCode;

    public MessagingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MessagingException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
