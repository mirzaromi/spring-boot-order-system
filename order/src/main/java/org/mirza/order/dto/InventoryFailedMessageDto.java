package org.mirza.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFailedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
    private String message;
}
