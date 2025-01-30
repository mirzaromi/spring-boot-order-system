package org.mirza.notification.exception;

import org.mirza.notification.enums.ExceptionEnum;

public class GlobalException extends RuntimeException {
    public GlobalException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
