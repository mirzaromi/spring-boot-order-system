package org.mirza.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionEnum {

    USER_NOT_FOUND("User Not Found"),
    PRODUCT_NOT_FOUND("Product Not Found"),
    ORDER_NOT_FOUND("Order Not Found"),
    INSUFFICIENT_PRODUCT_STOCK("Product Not Available"),
    ;

    private final String message;

}
