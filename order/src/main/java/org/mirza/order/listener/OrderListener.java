package org.mirza.order.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.order.dto.InventoryFailedMessageDto;
import org.mirza.order.service.OrderService;
import org.mirza.order.util.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {
    private final OrderService orderService;
    private final JsonUtil jsonUtil;

    @KafkaListener(id = "consumeInventoryFailed", topics = "${kafka.consumer.topic.inventory-failed}",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void consumeInventoryFailed(String message) {
        InventoryFailedMessageDto failedMessage = jsonUtil.toObject(message, InventoryFailedMessageDto.class);
        orderService.cancelOrder(failedMessage);
        log.info("Received data: {}", message);
    }
}
