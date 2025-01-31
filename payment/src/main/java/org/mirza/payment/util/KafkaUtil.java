package org.mirza.payment.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUtil {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        log.info("Sending to topic: {}, message: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
