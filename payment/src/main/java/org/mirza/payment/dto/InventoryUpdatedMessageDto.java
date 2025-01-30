package org.mirza.payment.dto;

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
