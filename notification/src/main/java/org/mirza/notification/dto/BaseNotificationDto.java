package org.mirza.notification.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseNotificationDto {
    private long userId;
    private long orderId;
    private String eventId;
    private String message;
}
