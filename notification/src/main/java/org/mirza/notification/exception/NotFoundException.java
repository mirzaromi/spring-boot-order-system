package org.mirza.notification.exception;

import org.mirza.notification.enums.ExceptionEnum;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
