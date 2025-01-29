package org.mirza.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mirza.order.dto.ItemDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedMessageDto {
    private long userId;
    private String eventId;
    private List<ItemDto> items;
}
