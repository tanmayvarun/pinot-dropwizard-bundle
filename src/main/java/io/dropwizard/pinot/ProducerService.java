package io.dropwizard.pinot;

import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;

import java.util.concurrent.CompletableFuture;

public interface ProducerService<T> {
  CompletableFuture<ProducedMeta> produce(Topic topic, String key, T record);
}
