package org.mirza.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mirza.entity.dto.BaseResponse;
import org.mirza.order.dto.request.CreateOrderRequestDto;
import org.mirza.order.dto.OrderCreatedMessageDto;
import org.mirza.order.dto.response.CreateOrderResponseDto;
import org.mirza.order.service.OrderService;
import org.mirza.order.util.JsonUtil;
import org.mirza.order.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtil jsonUtil;
    private final KafkaUtil kafkaUtil;

    @Value("${kafka.producer.topic.order-created}")
    private String orderCreatedTopic;


    @PostMapping
    public ResponseEntity<BaseResponse<CreateOrderResponseDto>> createOrder(@RequestBody @Validated CreateOrderRequestDto requestDto) {
        try {
            OrderCreatedMessageDto orderCreatedMessageDto = orderService.createOrder(requestDto);
            kafkaUtil.send(orderCreatedTopic, jsonUtil.toJsonString(orderCreatedMessageDto));
            return ResponseEntity.ok(new BaseResponse<>());
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            return ResponseEntity.internalServerError().body(
                    new BaseResponse<>(500, "Kafka communication failed", null)
            );
        }
    }
}
