package org.mirza.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.entity.enums.PaymentStatusEnum;
import org.mirza.payment.dto.InventoryUpdatedMessageDto;
import org.mirza.payment.enums.ExceptionEnum;
import org.mirza.payment.exception.GlobalException;
import org.mirza.payment.exception.NotFoundException;
import org.mirza.payment.exception.ValidationException;
import org.mirza.payment.repository.OrderRepository;
import org.mirza.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

import static org.mirza.payment.enums.ExceptionEnum.ORDER_STATUS_NOT_ELIGIBLE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Random random;

    public void processPayment(InventoryUpdatedMessageDto inventoryUpdatedMessageDto) {
        // make sure status is reserved
        Order order = validateOrder(inventoryUpdatedMessageDto);

        // do payment
        // generate random number with certain probability
        Payment payment = doProcessPayment(order);

        // create message for notif service

    }

    private Payment doProcessPayment(Order order) {
        double successProbability = 0.92; // 92% chance of success
        boolean isSuccessPayment = random.nextDouble() < successProbability;

        Payment payment = new Payment();
        if (isSuccessPayment) {
            // payment success
            payment.setAmount(order.getTotalPrice());
            payment.setOrder(order);
            payment.setStatus(PaymentStatusEnum.SUCCESS);
        } else {
            // payment failed
            payment.setAmount(order.getTotalPrice());
            payment.setOrder(order);
            payment.setStatus(PaymentStatusEnum.FAILED);
            payment.setRemark("unknown reason");

            // make order failed
            order.setStatus(OrderStatusEnum.FAILED);
            order.setRemark("payment failed");
            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    private Order validateOrder(InventoryUpdatedMessageDto inventoryUpdatedMessageDto) {
        Order order = orderRepository.findById(inventoryUpdatedMessageDto.getOrderId())
                .orElseThrow(() -> new NotFoundException(ExceptionEnum.ORDER_NOT_FOUND));

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.RESERVED)) {
            throw new ValidationException(ORDER_STATUS_NOT_ELIGIBLE);
        }
        return order;
    }
}
