package org.mirza.notification.service;

import lombok.RequiredArgsConstructor;
import org.mirza.entity.*;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.entity.enums.PaymentStatusEnum;
import org.mirza.notification.enums.ExceptionEnum;
import org.mirza.notification.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotificationValidator {
    public void validatePaymentSuccess(Order order, Payment payment) {
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE);
        }
        if (!Objects.equals(payment.getStatus(), PaymentStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.PAYMENT_STATUS_NOT_ELIGIBLE);
        }
    }

    public void validatePaymentFailed(Order order, Payment payment) {
        if (Objects.equals(order.getStatus(), OrderStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE);
        }
        if (!Objects.equals(payment.getStatus(), PaymentStatusEnum.FAILED)) {
            throw new ValidationException(ExceptionEnum.PAYMENT_STATUS_NOT_ELIGIBLE);
        }
    }

    public void validateInventoryFailed(Order order) {
        if (Objects.equals(order.getStatus(), OrderStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE);
        }
    }
}
