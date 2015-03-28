package com.myl.eservice.common.errorhandling.impl;

/**
 * Created by bpatterson on 2/7/15.
 */
public class ValidationException extends RuntimeException {
    String messageKey;
    String message;

    public ValidationException(String messageKey, String message) {
       super(message);

       this.messageKey = messageKey;
       this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
