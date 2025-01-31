package org.mirza.payment.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PaymentFailedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
    private String message;
}
