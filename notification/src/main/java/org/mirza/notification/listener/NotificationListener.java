package org.mirza.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.notification.dto.PaymentSuccessMessageDto;
import org.mirza.notification.service.NotificationService;
import org.mirza.notification.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;
    private final JsonUtil jsonUtil;

    @Value("${kafka.consumer.topic.payment-success}")
    private String paymentSuccessTopic;

    @KafkaListener(id = "consumeOrderCreated", topics = "${kafka.consumer.topic.payment-success}",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void consumeOrderCreated(String message) {
        log.info("Received data: {} from topic: {}", message, paymentSuccessTopic);

        PaymentSuccessMessageDto paymentSuccessMessageDto = jsonUtil.toObject(message, PaymentSuccessMessageDto.class);
        notificationService.sendNotification(paymentSuccessMessageDto);
    }
}
