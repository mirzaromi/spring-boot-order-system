package org.mirza.payment.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PaymentSuccessMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
}
