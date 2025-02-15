package org.mirza.notification.dto;

import lombok.*;

@Getter
@Setter
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailedMessageDto extends BaseNotificationDto{
    private long userId;
    private long orderId;
    private String eventId;
}
