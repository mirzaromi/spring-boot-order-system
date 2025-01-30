package org.mirza.payment.exception;

import org.mirza.payment.enums.ExceptionEnum;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
