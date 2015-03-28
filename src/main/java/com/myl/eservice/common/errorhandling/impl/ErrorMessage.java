package com.myl.eservice.common.errorhandling.impl;

/**
 * Created by bpatterson on 2/21/15.
 */
public class ErrorMessage {
    private int errorCode;
    private String message;

    public ErrorMessage(int errorCode, String message) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
