package com.myl.eservice.common.errorhandling;

import com.myl.eservice.common.errorhandling.impl.ValidationException;
import com.myl.eservice.common.errorhandling.impl.ErrorMessage;

/**
 * Created by bpatterson on 2/7/15.
 */
public interface IExceptionFactory {

    ValidationException throwValidation(String messageKey, Object... placeholders) throws ValidationException;

    ValidationException getValidation(String messageKey, Object... placeholders) throws ValidationException;

    String getMessage(String messageKey, Object... placeholders);
    ErrorMessage getErrorMessage(int errorCode,String messageKey, Object... placeholders);

}
