package org.mirza.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedMessageDto {
    private long userId;
    private long orderId;
    private String eventId;
    private List<ItemDto> items;
}
