package org.mirza.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mirza.entity.enums.OrderStatusEnum;
import org.mirza.order.dto.ItemDto;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderResponseDto {
    private long id;
    private long userId;
    private OrderStatusEnum status;
    private List<ItemDto> items;
    private int totalItems;
    private double totalPrice;
    private Date createdAt;
}
