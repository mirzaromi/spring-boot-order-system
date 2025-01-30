package org.mirza.payment.exception;

import org.mirza.payment.enums.ExceptionEnum;

public class GlobalException extends RuntimeException {
    public GlobalException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
