package org.mirza.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.entity.enums.PaymentStatusEnum;
import org.mirza.notification.dto.PaymentSuccessMessageDto;
import org.mirza.notification.enums.ExceptionEnum;
import org.mirza.notification.exception.GlobalException;
import org.mirza.notification.exception.NotFoundException;
import org.mirza.notification.exception.ValidationException;
import org.mirza.notification.repository.NotificationRepository;
import org.mirza.notification.repository.OrderRepository;
import org.mirza.notification.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public void sendNotification(PaymentSuccessMessageDto paymentSuccessMessageDto) {
        // validate payment success and order is success
        Order order = orderRepository.findById(paymentSuccessMessageDto.getOrderId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE);
        }

        Payment payment = paymentRepository.findById(paymentSuccessMessageDto.getPaymentId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.PAYMENT_NOT_FOUND));

        if (!Objects.equals(payment.getStatus(), PaymentStatusEnum.SUCCESS)) {
            throw new ValidationException(ExceptionEnum.PAYMENT_STATUS_NOT_ELIGIBLE);
        }


        // send notification with probability of success

        // save notification to db
    }
}
