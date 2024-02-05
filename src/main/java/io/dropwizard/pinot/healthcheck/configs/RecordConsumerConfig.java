package io.dropwizard.pinot.healthcheck.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordConsumerConfig {
  private String topicName;
  private int commitBatchSize;
  private long pollTimeMs;
  private KafkaConsumerConfig kafkaConsumerConfig;
  private long triggerIntervalSeconds = 5 * 60;
}
