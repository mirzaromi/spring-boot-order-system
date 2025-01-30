package org.mirza.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.enums.PaymentStatusEnum;
import org.mirza.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessor {
    private final PaymentRepository paymentRepository;
    private final Random random;

    public Payment createPayment(Order order, double successProbability) {
        log.info("Create payment with order id {}", order.getId());

        boolean isSuccessful = random.nextDouble() <= successProbability;

        Payment payment = new Payment();
        payment.setId(order.getId());
        payment.setOrder(order);
        payment.setStatus(isSuccessful ? PaymentStatusEnum.SUCCESS : PaymentStatusEnum.FAILED);
        payment.setRemark(isSuccessful ? null : "Failed");

        return paymentRepository.save(payment);
    }
}
