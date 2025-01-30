package org.mirza.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionEnum {

    USER_NOT_FOUND("User Not Found"),
    PRODUCT_NOT_FOUND("Product Not Found"),
    INSUFFICIENT_PRODUCT_STOCK("Product Not Available"),
    ORDER_NOT_FOUND("Order Not Found"),

    ;

    private final String message;

}
