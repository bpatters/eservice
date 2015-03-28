package com.myl.eservice.common.errorhandling.impl;

/**
 * Created by bpatterson on 2/6/15.
 */
public class SearchServiceException extends RuntimeException {
    public SearchServiceException(String message) {
    }
    public SearchServiceException(String message, Throwable ex) {
        super(message, ex);
    }
}
