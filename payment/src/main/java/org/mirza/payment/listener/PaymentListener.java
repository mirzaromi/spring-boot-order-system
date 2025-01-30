package org.mirza.payment.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.payment.dto.InventoryUpdatedMessageDto;
import org.mirza.payment.service.PaymentService;
import org.mirza.payment.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentListener {
    private final PaymentService paymentService;
    private final JsonUtil jsonUtil;

    @Value("${kafka.consumer.topic.inventory-updated}")
    private String inventoryUpdatedTopic;

    @KafkaListener(id = "consumeOrderCreated", topics = "${kafka.consumer.topic.inventory-updated}",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void consumeOrderCreated(String message) {
        log.info("Received data: {} from topic: {}", message, inventoryUpdatedTopic);

        InventoryUpdatedMessageDto inventoryUpdatedMessageDto = jsonUtil.toObject(message, InventoryUpdatedMessageDto.class);
        paymentService.processPayment(inventoryUpdatedMessageDto);
    }
}
