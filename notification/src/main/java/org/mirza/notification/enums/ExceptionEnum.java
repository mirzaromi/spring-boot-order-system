package org.mirza.notification.enums;

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
    PAYMENT_FAILED("Payment Failed"),
    PAYMENT_NOT_FOUND("Payment Not Found"),
    PAYMENT_STATUS_NOT_ELIGIBLE("Payment Status Not Eligible"),
    NOTIFICATION_FAILED("Notification Failed"),

    ;

    private final String message;

}
