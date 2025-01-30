package org.mirza.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.payment.enums.ExceptionEnum;
import org.mirza.payment.exception.NotFoundException;
import org.mirza.payment.exception.ValidationException;
import org.mirza.payment.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.mirza.payment.enums.ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderRepository orderRepository;

    public Order validateOrderForPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.RESERVED)) {
            throw new ValidationException(ORDER_STATUS_NOT_ELIGIBLE);
        }
        return order;
    }
}
