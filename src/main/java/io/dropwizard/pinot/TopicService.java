package io.dropwizard.pinot;

import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TopicService<T> {

  CompletableFuture<ProducedMeta> postMessage(String topicName, String key, T entity);

//  CompletableFuture<Void> createTopic(Topic topic);

  CompletableFuture<Topic> getTopic(String topicName);

  CompletableFuture<Set<String>> getTopicNames();

  CompletableFuture<Set<Topic>> getTopics();

  Set<String> getTopicsSync();

//  CompletableFuture<PartitionOffsets> getTopicOffsets(String topicName);

//  CompletableFuture<PartitionOffsets> getConsumerGroupOffsets(String topicName, String consumerGroup);

//  CompletionStage<PartitionOffsets> getConsumerGroupLags(String topicName, String consumerGroupId);
}
