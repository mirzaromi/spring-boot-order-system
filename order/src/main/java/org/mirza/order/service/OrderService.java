package org.mirza.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Inventory;
import org.mirza.entity.Order;
import org.mirza.entity.OrderDetail;
import org.mirza.entity.User;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.order.dto.InventoryFailedMessageDto;
import org.mirza.order.dto.ItemDto;
import org.mirza.order.dto.request.CreateOrderRequestDto;
import org.mirza.order.dto.OrderCreatedMessageDto;
import org.mirza.order.enums.ExceptionEnum;
import org.mirza.order.exception.GlobalException;
import org.mirza.order.exception.NotFoundException;
import org.mirza.order.repository.InventoryRepository;
import org.mirza.order.repository.OrderDetailRepository;
import org.mirza.order.repository.OrderRepository;
import org.mirza.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Value("${kafka.producer.topic.order-created}")
    private String orderCreatedTopic;

    @Transactional
    public OrderCreatedMessageDto createOrder(CreateOrderRequestDto requestDto) {
        log.info("Creating order for user ID: {}", requestDto.getUserId());

        User user = validateAndGetUser(requestDto.getUserId());
        Order order = createInitialOrder(user);

        processOrderItems(requestDto.getItems(), order);

        orderRepository.save(order);

        return createOrderCreatedMessage(requestDto, user, order);
    }

    private OrderCreatedMessageDto createOrderCreatedMessage(CreateOrderRequestDto requestDto, User user, Order order) {
        OrderCreatedMessageDto orderCreatedMessageDto = new OrderCreatedMessageDto();
        orderCreatedMessageDto.setUserId(user.getId());
        orderCreatedMessageDto.setEventId(orderCreatedTopic+ "-" + order.getId());
        orderCreatedMessageDto.setItems(requestDto.getItems());
        orderCreatedMessageDto.setOrderId(order.getId());
        return orderCreatedMessageDto;
    }

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new NotFoundException(ExceptionEnum.USER_NOT_FOUND);
                });
    }

    private Order createInitialOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatusEnum.PENDING);
        return orderRepository.save(order);  // Persist early for ID generation
    }

    private void processOrderItems(List<ItemDto> items, Order order) {
        List<OrderDetail> orderDetails = items.stream()
                .map(item -> createOrderDetail(item, order))
                .toList();

        orderDetailRepository.saveAll(orderDetails);

        calculateAndSetOrderTotals(order, orderDetails);
    }

    private OrderDetail createOrderDetail(ItemDto item, Order order) {
        Inventory inventory = inventoryRepository.findById(item.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", item.getProductId());
                    return new NotFoundException(ExceptionEnum.PRODUCT_NOT_FOUND);
                });

        validateStockAvailability(inventory, item.getQuantity());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setInventory(inventory);
        orderDetail.setQuantity(item.getQuantity());
        orderDetail.setOrder(order);
        orderDetail.setPrice(inventory.getPrice() * item.getQuantity());
        return orderDetail;
    }

    private void calculateAndSetOrderTotals(Order order, List<OrderDetail> orderDetails) {
        int totalItems = orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        double totalPrice = orderDetails.stream()
                .mapToDouble(OrderDetail::getPrice)
                .sum();

        order.setTotalItems(totalItems);
        order.setTotalPrice(totalPrice);
    }

    private void validateStockAvailability(Inventory inventory, int requestedQuantity) {
        if(inventory.getStock() < requestedQuantity) {
            log.error("Insufficient stock for product: {}", inventory.getId());
            throw new GlobalException(ExceptionEnum.INSUFFICIENT_PRODUCT_STOCK);
        }
    }

    // compensation from inventory failed
    public void cancelOrder(InventoryFailedMessageDto messageDto) {
        log.info("Cancelling order for user ID: {}", messageDto.getUserId());

        Order order = orderRepository.findById(messageDto.getOrderId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        order.setStatus(OrderStatusEnum.FAILED);
        order.setRemark(messageDto.getMessage());

        log.info("Cancelling order for order ID: {} with cause: {}", messageDto.getOrderId(), messageDto.getMessage());
        orderRepository.save(order);
    }
}
