package org.mirza.notification.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PaymentSuccessMessageDto {
    private long userId;
    private long orderId;
    private long paymentId;
    private String eventId;
    private String message;
}
