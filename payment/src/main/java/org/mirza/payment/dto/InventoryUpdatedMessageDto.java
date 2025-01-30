package org.mirza.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdatedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
}
