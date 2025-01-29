package org.mirza.inventory.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InventoryConsumer {
    @KafkaListener(id = "myListener", topics = "my-second-topic",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void listen(String data) {
        log.info("Received data: {}", data);
    }

    @KafkaListener(id = "anotherListener", topics = "my-topic",
            autoStartup = "${listen.auto.start:true}", concurrency = "${listen.concurrency:1}")
    public void listenTwo(String data) {
        log.info("Received data: {}", data);
    }
}
