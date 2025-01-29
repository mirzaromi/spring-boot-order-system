package org.mirza.order.exception;

import org.mirza.order.enums.ExceptionEnum;

public class GlobalException extends RuntimeException {
    public GlobalException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
