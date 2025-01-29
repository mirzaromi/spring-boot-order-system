package org.mirza.inventory.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Inventory;
import org.mirza.inventory.dto.InventoryFailedMessageDto;
import org.mirza.inventory.dto.InventoryUpdatedMessageDto;
import org.mirza.inventory.dto.OrderCreatedMessageDto;
import org.mirza.inventory.enums.ExceptionEnum;
import org.mirza.inventory.exception.GlobalException;
import org.mirza.inventory.exception.NotFoundException;
import org.mirza.inventory.repository.InventoryRepository;
import org.mirza.inventory.util.JsonUtil;
import org.mirza.inventory.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final KafkaUtil kafkaUtil;
    private final JsonUtil jsonUtil;

    @Value("${kafka.producer.topic.inventory-failed}")
    private String inventoryFailedTopic;

    @Value("${kafka.producer.topic.inventory-updated}")
    private String inventoryUpdatedTopic;

    public void reserveInventory(OrderCreatedMessageDto orderCreatedMessageDto) {
        try {
            log.info("Reserve inventory");
            List<Inventory> inventoryList = updateInventoryStock(orderCreatedMessageDto);

            inventoryRepository.saveAll(inventoryList);
            publishInventoryUpdatedMessage(orderCreatedMessageDto);

        } catch (RuntimeException e) {
            log.error("Error reserving inventory: {}", e.getMessage(), e);

            publishInventoryFailedMessage(orderCreatedMessageDto, e);
            throw e; // for triggering the @Transaction to roll back the process above
        }
    }

    private List<Inventory> updateInventoryStock(OrderCreatedMessageDto orderCreatedMessageDto) {
        return orderCreatedMessageDto.getItems()
                .stream()
                .map(item -> {
                    Inventory inventory = findInventory(item.getProductId());
                    log.info("Updating inventory with id: {}", inventory.getId());
                    validateStockAvailability(inventory, item.getQuantity());
                    inventory.setStock(inventory.getStock() - item.getQuantity());
                    log.info("quantity of inventory: {}, is: {}", inventory.getName(), inventory.getStock());
                    return inventory;
                })
                .toList();
    }

    private Inventory findInventory(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.PRODUCT_NOT_FOUND));
    }

    private void publishInventoryUpdatedMessage(OrderCreatedMessageDto orderCreatedMessageDto) {
        log.info("Publish inventory updated message");
        InventoryUpdatedMessageDto messageDto = new InventoryUpdatedMessageDto();
        messageDto.setUserId(orderCreatedMessageDto.getUserId());
        messageDto.setOrderId(orderCreatedMessageDto.getOrderId());
        messageDto.setEventId(buildEventId(inventoryUpdatedTopic, orderCreatedMessageDto.getOrderId()));

        kafkaUtil.send(inventoryUpdatedTopic, jsonUtil.toJsonString(messageDto));
    }

    private <T extends RuntimeException> void publishInventoryFailedMessage(OrderCreatedMessageDto orderCreatedMessageDto, T e) {
        log.info("Publish inventory failed message");
        InventoryFailedMessageDto messageDto = new InventoryFailedMessageDto();
        messageDto.setUserId(orderCreatedMessageDto.getUserId());
        messageDto.setOrderId(orderCreatedMessageDto.getOrderId());
        messageDto.setEventId(buildEventId(inventoryFailedTopic, orderCreatedMessageDto.getOrderId()));
        messageDto.setMessage(e.getMessage());

        kafkaUtil.send(inventoryFailedTopic, jsonUtil.toJsonString(messageDto));
    }

    private String buildEventId(String topic, Long orderId) {
        return topic + "-" + orderId;
    }
    private void validateStockAvailability(Inventory inventory, int requestedQuantity) {
        if(inventory.getStock() < requestedQuantity) {
            log.error("Insufficient stock for product: {}", inventory.getId());
            throw new GlobalException(ExceptionEnum.INSUFFICIENT_PRODUCT_STOCK);
        }
    }

}
