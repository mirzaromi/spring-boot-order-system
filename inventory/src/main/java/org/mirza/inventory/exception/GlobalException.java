package org.mirza.inventory.exception;

import org.mirza.inventory.enums.ExceptionEnum;

public class GlobalException extends RuntimeException {
    public GlobalException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
