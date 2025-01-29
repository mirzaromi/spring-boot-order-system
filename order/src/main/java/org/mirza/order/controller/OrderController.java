package org.mirza.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mirza.entity.dto.BaseResponse;
import org.mirza.order.dto.request.CreateOrderRequestDto;
import org.mirza.order.dto.response.CreateOrderResponseDto;
import org.mirza.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @PostMapping("/second-topic")
    public ResponseEntity<BaseResponse<CreateOrderResponseDto>> createOrder(CreateOrderRequestDto requestDto) {
        try {
            kafkaTemplate.send("my-second-topic", "the message from second topic");
            return ResponseEntity.ok(new BaseResponse<>());
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            return ResponseEntity.internalServerError().body(
                    new BaseResponse<>(500, "Kafka communication failed", null)
            );
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CreateOrderResponseDto>> createOrderFirstTopic(CreateOrderRequestDto requestDto) {
        try {
            kafkaTemplate.send("my-second-topic", "the message from first topic");
            return ResponseEntity.ok(new BaseResponse<>());
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            return ResponseEntity.internalServerError().body(
                    new BaseResponse<>(500, "Kafka communication failed", null)
            );
        }
    }

//    @GetMapping("/{orderId}")
//    public ResponseEntity<BaseResponse<CreateOrderResponseDto>> getOrder(@PathVariable String orderId) {
//
//    }
}
