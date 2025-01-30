package org.mirza.notification.exception;

import org.mirza.notification.enums.ExceptionEnum;

public class ValidationException extends RuntimeException {
    public ValidationException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
