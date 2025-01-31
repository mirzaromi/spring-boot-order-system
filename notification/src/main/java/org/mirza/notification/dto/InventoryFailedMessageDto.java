package org.mirza.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFailedMessageDto extends BaseNotificationDto implements Serializable {
    private long userId;
    private long orderId;
    private String eventId;
    private String message;
}
