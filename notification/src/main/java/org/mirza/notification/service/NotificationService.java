package org.mirza.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Notification;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.User;
import org.mirza.entity.enums.NotificationStatusEnum;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final Random random;

    @Value("${kafka.consumer.topic.payment-success}")
    private String paymentSuccessTopic;

    public void sendNotification(PaymentSuccessMessageDto paymentSuccessMessageDto) {
        Notification notification = new Notification();

        try {
            // initiate notification
            Order order = orderRepository.findOrderById(paymentSuccessMessageDto.getOrderId())
                    .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

            Payment payment = paymentRepository.findById(paymentSuccessMessageDto.getPaymentId())
                    .orElseThrow(() -> new NotFoundException(ExceptionEnum.PAYMENT_NOT_FOUND));

            User user = Optional.ofNullable(order.getUser())
                    .orElseThrow(() -> new NotFoundException(ExceptionEnum.USER_NOT_FOUND));

            notification.setMessage(paymentSuccessMessageDto.getMessage());
            notification.setEventType(paymentSuccessTopic);
            notification.setOrder(order);
            notification.setUser(user);

            log.info("start send notification");

            // validate payment success and order is success

            if (!Objects.equals(order.getStatus(), OrderStatusEnum.SUCCESS)) {
                throw new ValidationException(ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE);
            }

            log.info("order status is {}", order.getStatus());

            if (!Objects.equals(payment.getStatus(), PaymentStatusEnum.SUCCESS)) {
                throw new ValidationException(ExceptionEnum.PAYMENT_STATUS_NOT_ELIGIBLE);
            }

            // send notification with probability of success
            double successProbability = 0.92;
            boolean isSuccessful = random.nextDouble() <= successProbability;
            log.info("sending notification to user {} with success status is {}", user, isSuccessful);

            // save notification to db

            notification.setStatus(isSuccessful ? NotificationStatusEnum.SUCCESS : NotificationStatusEnum.FAILED);
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("handling exception", e);
            log.error(e.getMessage(), e);
            notification.setStatus(NotificationStatusEnum.FAILED);
            notification.setRemark(e.getMessage());
            notificationRepository.save(notification);
        }

    }
}
