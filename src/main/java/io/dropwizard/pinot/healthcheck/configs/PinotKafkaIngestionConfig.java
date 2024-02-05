package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinotKafkaIngestionConfig {
    private KafkaProducerConfig kafkaProducerProperties;
}
