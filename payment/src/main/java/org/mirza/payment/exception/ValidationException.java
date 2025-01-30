package org.mirza.payment.exception;

import org.mirza.payment.enums.ExceptionEnum;

public class ValidationException extends RuntimeException {
    public ValidationException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
