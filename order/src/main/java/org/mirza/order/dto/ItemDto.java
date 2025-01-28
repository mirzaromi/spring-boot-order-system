package org.mirza.order.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemDto {
    private long productId;
    private int quantity;
    private double price;
}