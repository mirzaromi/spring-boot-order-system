package org.mirza.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mirza.entity.enums.OrderStatusEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponseDto {
    private long id;
    private OrderStatusEnum orderStatus;
    private int totalItem;
    private double totalPrice;
}
