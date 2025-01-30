package org.mirza.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionEnum {

    USER_NOT_FOUND("User Not Found"),
    PRODUCT_NOT_FOUND("Product Not Found"),
    INSUFFICIENT_PRODUCT_STOCK("Product Not Available"),
    ORDER_NOT_FOUND("Order Not Found"),
    ORDER_STATUS_NOT_ELIGIBLE("Order Status Not Eligible"),
    ;

    private final String message;

}
