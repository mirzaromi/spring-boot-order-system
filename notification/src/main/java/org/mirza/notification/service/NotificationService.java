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
import org.mirza.notification.dto.*;
import org.mirza.notification.enums.ExceptionEnum;
import org.mirza.notification.enums.NotificationType;
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
    private final NotificationValidator notificationValidator;

    @Value("${kafka.consumer.topic.payment-success}")
    private String paymentSuccessTopic;

    @Value("${notification.success.probability:0.92}")
    private double successProbability;

    public void sendNotification(PaymentSuccessMessageDto messageDto) {
        processNotification(messageDto, NotificationType.PAYMENT_SUCCESS);
    }

    public void sendNotification(PaymentFailedMessageDto messageDto) {
        processNotification(messageDto, NotificationType.PAYMENT_FAILED);
    }

    public void sendNotification(InventoryFailedMessageDto messageDto) {
        processNotification(messageDto, NotificationType.INVENTORY_FAILED);
    }

    private <T extends BaseNotificationDto> void processNotification(T messageDto, NotificationType notificationType) {
        Notification notification = initializeNotification(messageDto, notificationType);

        try {
            NotificationContextDto context = buildNotificationContext(messageDto);
            validateNotificationContext(context, notificationType);
            processNotificationDelivery(notification, context);
        } catch (Exception e) {
            handleNotificationFailure(notification, e);
        }
    }

    private <T extends BaseNotificationDto> Notification initializeNotification(T messageDto, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setMessage(messageDto.getMessage());
        notification.setNotificationType(notificationType.name());
        return notification;
    }

    private <T extends BaseNotificationDto> NotificationContextDto buildNotificationContext(T messageDto) {
        Order order = orderRepository.findOrderById(messageDto.getOrderId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        User user = Optional.ofNullable(order.getUser())
                .filter(u -> Objects.equals(u.getId(), messageDto.getUserId()))
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.USER_NOT_FOUND));

        // Only fetch payment for payment-related notifications
        Payment payment = null;
        if (messageDto instanceof PaymentSuccessMessageDto) {
            Long paymentId = ((PaymentSuccessMessageDto) messageDto).getPaymentId();
            payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new NotFoundException(ExceptionEnum.PAYMENT_NOT_FOUND));
        }

        return new NotificationContextDto(order, payment, user);
    }

    private void validateNotificationContext(NotificationContextDto context, NotificationType type) {
        switch (type) {
            case PAYMENT_SUCCESS:
                notificationValidator.validatePaymentSuccess(context.getOrder(), context.getPayment());
                break;
            case PAYMENT_FAILED:
                notificationValidator.validatePaymentFailed(context.getOrder(), context.getPayment());
                break;
            case INVENTORY_FAILED:
                notificationValidator.validateInventoryFailed(context.getOrder());
                break;
        }
    }


    private void processNotificationDelivery(Notification notification, NotificationContextDto context) {
        log.info("Processing notification for user: {} of type: {}",
                context.getUser().getId(), notification.getNotificationType());

        boolean isSuccessful = simulateNotificationDelivery(context.getUser());
        updateAndSaveNotification(notification, context, isSuccessful);
    }


    private boolean simulateNotificationDelivery(User user) {
        boolean isSuccessful = random.nextDouble() <= successProbability;
        log.info("Sending notification to user {} with success status: {}", user.getId(), isSuccessful);
        return isSuccessful;
    }


    private void updateAndSaveNotification(Notification notification, NotificationContextDto context, boolean isSuccessful) {
        notification.setStatus(isSuccessful ? NotificationStatusEnum.SUCCESS : NotificationStatusEnum.FAILED);
        notification.setOrder(context.getOrder());
        notification.setUser(context.getUser());
        notificationRepository.save(notification);
    }


    private void handleNotificationFailure(Notification notification, Exception e) {
        log.error("Failed to process notification: {}", e.getMessage(), e);
        notification.setStatus(NotificationStatusEnum.FAILED);
        notification.setRemark(e.getMessage());
        notificationRepository.save(notification);
    }
}
