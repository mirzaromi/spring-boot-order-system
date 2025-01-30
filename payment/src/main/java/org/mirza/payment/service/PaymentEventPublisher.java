package org.mirza.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.payment.dto.InventoryUpdatedMessageDto;
import org.mirza.payment.dto.PaymentFailedMessageDto;
import org.mirza.payment.dto.PaymentSuccessMessageDto;
import org.mirza.payment.util.JsonUtil;
import org.mirza.payment.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final KafkaUtil kafkaUtil;
    private final JsonUtil jsonUtil;

    @Value("${kafka.producer.topic.payment-success}")
    private String paymentSuccessTopic;

    @Value("${kafka.producer.topic.payment-failed}")
    private String paymentFailedTopic;

    public void publishPaymentSuccess(Order order, Payment payment) {
        PaymentSuccessMessageDto message = PaymentSuccessMessageDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .paymentId(payment.getId())
                .eventId(generateEventId(paymentSuccessTopic, order.getId()))
                .build();

        log.info("Publishing payment success event for order {}", order.getId());
        kafkaUtil.send(paymentSuccessTopic, jsonUtil.toJsonString(message));
    }

    public void publishPaymentFailed(InventoryUpdatedMessageDto inventoryMessage) {
        PaymentFailedMessageDto message = PaymentFailedMessageDto.builder()
                .orderId(inventoryMessage.getOrderId())
                .userId(inventoryMessage.getUserId())
                .eventId(generateEventId(paymentFailedTopic, inventoryMessage.getOrderId()))
                .build();

        log.info("Publishing payment failed event for order {}", inventoryMessage.getOrderId());
        kafkaUtil.send(paymentFailedTopic, jsonUtil.toJsonString(message));
    }

    private String generateEventId(String topic, Long orderId) {
        return String.format("%s-%d-%d", topic, orderId, System.currentTimeMillis());
    }
}
