package org.mirza.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.entity.enums.PaymentStatusEnum;
import org.mirza.payment.dto.InventoryUpdatedMessageDto;
import org.mirza.payment.enums.ExceptionEnum;
import org.mirza.payment.exception.GlobalException;
import org.mirza.payment.repository.OrderRepository;
import org.mirza.payment.repository.PaymentRepository;
import org.mirza.payment.util.JsonUtil;
import org.mirza.payment.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final PaymentProcessor paymentProcessor;
    private final PaymentEventPublisher paymentEventPublisher;

    @Value("${payment.success.probability:0.92}")
    private double paymentSuccessProbability;

    public void processPayment(InventoryUpdatedMessageDto inventoryUpdatedMessageDto) {

        try {
            log.info("start processing payment");
            // make sure status is reserved
            Order order = orderValidator.validateOrderForPayment(inventoryUpdatedMessageDto.getOrderId());

            // do payment
            // generate random number with certain probability
            Payment payment = doProcessPayment(order);

            // create message for notif service
            paymentEventPublisher.publishPaymentSuccess(order, payment);

        } catch (RuntimeException e) {
            // for compensation
            log.info("Error processing payment {}", inventoryUpdatedMessageDto);
            // create message for notif service
            paymentEventPublisher.publishPaymentFailed(inventoryUpdatedMessageDto);
        }


    }

    private Payment doProcessPayment(Order order) {
        Payment payment = paymentProcessor.createPayment(order, paymentSuccessProbability);

        if (payment.getStatus() == PaymentStatusEnum.FAILED) {
            handleFailedPayment(order, payment);
        }

        return payment;
    }

    private void handleFailedPayment(Order order, Payment payment) {
        log.info("Handling failed payment for order {}", order.getId());

        order.setStatus(OrderStatusEnum.FAILED);
        order.setRemark("Payment failed");
        orderRepository.save(order);

        throw new GlobalException(ExceptionEnum.PAYMENT_FAILED); // for triggering the catch

    }
}
