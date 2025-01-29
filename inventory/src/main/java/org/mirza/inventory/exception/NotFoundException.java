package org.mirza.inventory.exception;

import org.mirza.inventory.enums.ExceptionEnum;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
