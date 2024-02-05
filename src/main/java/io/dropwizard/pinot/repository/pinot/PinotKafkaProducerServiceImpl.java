package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;
import io.dropwizard.pinot.ProducerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;

public class PinotKafkaProducerServiceImpl implements ProducerService<Map<String, Object>> {

  private final KafkaProducer<String, Map<String, Object>> kafkaProducer;
//  private final MetricRegistry metricRegistry;

  public PinotKafkaProducerServiceImpl(KafkaProducer<String, Map<String, Object>> kafkaProducer) {
    this.kafkaProducer = kafkaProducer;

    System.out.println("Producer service is created");
//    this.metricRegistry = metricRegistry;
  }

  @Override
  public CompletableFuture<ProducedMeta> produce(Topic topic, String key, Map<String, Object> serializableEntity) {
//    final Timer.Context globalTimerContext =
//        metricRegistry.timer(MetricUtils.publishToKafkaTopicMetricName()).time();
//    final Timer.Context timerContext =
//        metricRegistry.timer(MetricUtils.publishToKafkaTopicMetricName(topic.getName())).time();

    CompletableFuture<ProducedMeta> future = new CompletableFuture<>();

    ProducerRecord<String, Map<String, Object>> producerRecord = new ProducerRecord<>(topic.getName(),
            key, serializableEntity);

    kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
//      timerContext.stop();
//      globalTimerContext.stop();
      if (isNull(e)) {
        future.complete(ProducedMeta.builder()
            .topicName(recordMetadata.topic())
            .offset(recordMetadata.offset())
            .partitionId(recordMetadata.partition())
            .build());
      } else {
        future.completeExceptionally(e);
      }
    });

    return future;
  }
}
