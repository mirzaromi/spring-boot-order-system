package org.mirza.order.exception;

import org.mirza.order.enums.ExceptionEnum;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
