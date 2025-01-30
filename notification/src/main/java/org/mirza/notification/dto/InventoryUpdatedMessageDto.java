package org.mirza.notification.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InventoryUpdatedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
}
