package org.mirza.notification.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class PaymentSuccessMessageDto extends BaseNotificationDto implements Serializable {
    private long userId;
    private long orderId;
    private long paymentId;
    private String eventId;
    private String message;
}
