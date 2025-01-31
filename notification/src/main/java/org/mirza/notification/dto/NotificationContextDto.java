package org.mirza.notification.dto;

import lombok.Value;
import org.mirza.entity.Order;
import org.mirza.entity.Payment;
import org.mirza.entity.User;

@Value
public class NotificationContextDto {
    Order order;
    Payment payment;  // Payment might be null for inventory notifications
    User user;
}
