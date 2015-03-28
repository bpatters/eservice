package com.myl.eservice.common.errorhandling.impl;

import com.myl.eservice.common.errorhandling.IExceptionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;


/**
 * Created by bpatterson on 2/7/15.
 */
@Component
public class ExceptionFactory implements IExceptionFactory {
    @Autowired
    @Qualifier("messages")
    private MessageSource messageSource;

    @Override
    public ValidationException throwValidation(String messageKey, Object... placeholders) throws ValidationException {
        throw new ValidationException(messageKey, this.getMessage(messageKey, placeholders));
    }

    @Override
    public ValidationException getValidation(String messageKey, Object... placeholders) throws ValidationException {
        return new ValidationException(messageKey, this.getMessage(messageKey, placeholders));
    }

    public String getMessage(String messageKey, Object... placeholders) {
        return this.getMessageSource().getMessage(messageKey, placeholders,getLocale());
    }
    public ErrorMessage getErrorMessage(int errorCode,String messageKey, Object... placeholders) {
        return new ErrorMessage(errorCode, this.getMessageSource().getMessage(messageKey, placeholders,getLocale()));
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
