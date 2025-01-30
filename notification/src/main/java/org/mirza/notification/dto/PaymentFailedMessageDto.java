package org.mirza.notification.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PaymentFailedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
}
