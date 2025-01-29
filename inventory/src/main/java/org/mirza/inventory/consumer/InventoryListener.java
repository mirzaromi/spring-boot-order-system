package org.mirza.inventory.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.inventory.dto.OrderCreatedMessageDto;
import org.mirza.inventory.service.InventoryService;
import org.mirza.inventory.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryListener {
    private final JsonUtil jsonUtil;
    private final InventoryService inventoryService;

    @Value("${kafka.consumer.topic.order-created}")
    private String orderCreatedTopic;

    @KafkaListener(id = "consumeOrderCreated", topics = "${kafka.consumer.topic.order-created}",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void consumeOrderCreated(String message) {
        log.info("Received data: {} from topic: {}", message, orderCreatedTopic);

        OrderCreatedMessageDto orderCreatedMessageDto = jsonUtil.toObject(message, OrderCreatedMessageDto.class);
        inventoryService.reserveInventory(orderCreatedMessageDto);
    }
}
