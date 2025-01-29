package org.mirza.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mirza.order.dto.ItemDto;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {
    @NotNull
    private long userId;
    private List<ItemDto> items;
}


