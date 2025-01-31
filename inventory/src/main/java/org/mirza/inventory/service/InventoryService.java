package org.mirza.inventory.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Inventory;
import org.mirza.entity.Order;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.inventory.dto.InventoryFailedMessageDto;
import org.mirza.inventory.dto.InventoryUpdatedMessageDto;
import org.mirza.inventory.dto.OrderCreatedMessageDto;
import org.mirza.inventory.dto.PaymentFailedMessageDto;
import org.mirza.inventory.enums.ExceptionEnum;
import org.mirza.inventory.exception.GlobalException;
import org.mirza.inventory.exception.NotFoundException;
import org.mirza.inventory.repository.InventoryRepository;
import org.mirza.inventory.repository.OrderRepository;
import org.mirza.inventory.util.JsonUtil;
import org.mirza.inventory.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = {RuntimeException.class, Exception.class})
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final KafkaUtil kafkaUtil;
    private final JsonUtil jsonUtil;
    private final OrderRepository orderRepository;

    @Value("${kafka.producer.topic.inventory-failed}")
    private String inventoryFailedTopic;

    @Value("${kafka.producer.topic.inventory-updated}")
    private String inventoryUpdatedTopic;

    public void reserveInventory(OrderCreatedMessageDto orderCreatedMessageDto) {
        try {
            log.info("Reserve inventory");
            List<Inventory> inventoryList = updateInventoryStock(orderCreatedMessageDto);

            inventoryRepository.saveAll(inventoryList);

            // set order status to reserved
            setOrderStatusToReserved(orderCreatedMessageDto);

            publishInventoryUpdatedMessage(orderCreatedMessageDto);

        } catch (RuntimeException e) {
            log.error("Error reserving inventory: {}", e.getMessage(), e);

            publishInventoryFailedMessage(orderCreatedMessageDto, e);
            throw e; // for triggering the @Transaction to roll back the process above
        }
    }

    private void setOrderStatusToReserved(OrderCreatedMessageDto orderCreatedMessageDto) {
        Order order = orderRepository.findById(orderCreatedMessageDto.getOrderId())
                        .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));
        order.setStatus(OrderStatusEnum.RESERVED);
        orderRepository.save(order);
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
        String timestamp = String.valueOf(System.currentTimeMillis());
        return topic + "-" + orderId + "-" + timestamp;
    }
    private void validateStockAvailability(Inventory inventory, int requestedQuantity) {
        if(inventory.getStock() < requestedQuantity) {
            log.error("Insufficient stock for product: {}", inventory.getId());
            throw new GlobalException(ExceptionEnum.INSUFFICIENT_PRODUCT_STOCK);
        }
    }

    public void compensatePaymentFailed(PaymentFailedMessageDto paymentFailedMessageDto) {
        log.info("Compensate payment failed");
        Order order = orderRepository.findOrderById(paymentFailedMessageDto.getOrderId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        order.setStatus(OrderStatusEnum.FAILED);
        orderRepository.save(order);

        List<Inventory> inventories = order.getOrderDetail().stream()
                .map(orderDetail -> {
                    Inventory inventory = orderDetail.getInventory();
                    log.info("Updating inventory with id: {}", inventory.getId());
                    inventory.setStock(inventory.getStock() + orderDetail.getQuantity());
                    log.info("quantity of inventory: {}, is: {}", inventory.getName(), inventory.getStock());
                    return inventory;
                })
                .toList();

        inventoryRepository.saveAll(inventories);
    }

}
