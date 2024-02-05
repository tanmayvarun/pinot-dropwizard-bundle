package io.dropwizard.pinot.models.kafka.kafkaproducer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProducedMeta {
    private String topicName;
    private int partitionId;
    private long offset;
}