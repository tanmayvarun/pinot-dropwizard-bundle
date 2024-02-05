package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.IngestionException;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;
import io.dropwizard.pinot.ProducerService;
import io.dropwizard.pinot.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.KafkaFuture.Function;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@Slf4j
public class PinotKafkaTopicServiceImpl implements TopicService<Map<String, Object>> {

  private final AdminClient adminClient;
//  private final Properties adminConsumerProperties;
  private final ProducerService producerService;
//  private final MetricRegistry metricRegistry;
  private final Map<String, CompletableFuture<Topic>> topicCacheMap = new ConcurrentHashMap<>();
//  private final List<String> knownSchemaNameAndSpaces;

    public PinotKafkaTopicServiceImpl(AdminClient adminClient,
                                      ProducerService producerService) {
        this.adminClient = adminClient;
        this.producerService = producerService;
    }

//  @Inject
//  public TopicServiceImpl(AdminClient adminClient,
//      @Named("kafkaAdminConsumerProperties") Properties adminConsumerProperties,
//      ProducerServiceImpl producerServiceImpl,
//      MetricRegistry metricRegistry,
//      @Named("knownSchemaNameAndSpaces") List<String> knownSchemaNameAndSpaces) {
//    this.adminClient = adminClient;
//    this.adminConsumerProperties = adminConsumerProperties;
//    this.producerServiceImpl = producerServiceImpl;
//    this.metricRegistry = metricRegistry;
//    Objects.requireNonNull(knownSchemaNameAndSpaces);
//    if (knownSchemaNameAndSpaces.isEmpty()) {
//      throw new AssertionError("knownSchemaNameAndSpaces cannot be Empty");
//    }
//    this.knownSchemaNameAndSpaces = knownSchemaNameAndSpaces;
//  }

  @Override
  public CompletableFuture<ProducedMeta> postMessage(String topicName, String key, Map<String, Object> value) {
    //log.info("Posting message: {}", Record.getTstoreHiveRecord().dedupKey());
//    final Timer.Context timerContext =
//        metricRegistry.timer(MetricUtils.getTopicTimerName(topicName)).time();
//    final Timer.Context globalTimerContext =
//        metricRegistry.timer(MetricUtils.getTopicTimerName()).time();
    return getTopic(topicName)
        .thenCompose(topic -> {
//          timerContext.stop();
//          globalTimerContext.stop();
          return producerService.produce(topic, key, value);
        }).whenComplete((result, throwable) -> {
          if (throwable != null) {
//            timerContext.stop();
//            globalTimerContext.stop();
            log.error("Exception in getting topic:" + topicName, throwable);
          }
        });
  }

//  public Boolean isKnownSchema(SchemaNameAndSpace schemaNameAndSpace){
//     if(knownSchemaNameAndSpaces.contains(schemaNameAndSpace.fullName())){
//       return true;
//     }
//     log.info("Skipping entity for schemaNameAndSpace: {}", schemaNameAndSpace);
//     return false;
//
//  }

//  @Override
//  public CompletableFuture<Void> createTopic(Topic topic) {
//    NewTopic newTopic = new NewTopic(topic.getName(), topic.getPartitions(), (short) topic.getReplicationFactor());
//    CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singletonList(newTopic));
//    CompletableFuture<Void> resultFuture = new CompletableFuture<>();
//    createTopicsResult.all().whenComplete((aVoid, throwable) -> {
//      if (nonNull(throwable)) {
//        resultFuture.completeExceptionally(throwable);
//      } else {
//        resultFuture.complete(null);
//      }
//    });
//    return resultFuture;
//  }

  @Override
  public CompletableFuture<Topic> getTopic(String topicName) {
    return topicCacheMap.computeIfAbsent(topicName, s -> getTopicDetail(topicName));
  }

  private CompletableFuture<Topic> getTopicDetail(String topicName) {
    DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(topicName));
    KafkaFuture<CompletableFuture<Topic>> future = describeTopicsResult.all()
        .thenApply(topicDescriptionMap -> {
          CompletableFuture<Topic> topicFuture = new CompletableFuture<>();
          TopicDescription topicDescription = topicDescriptionMap.get(topicName);
          List<TopicPartitionInfo> topicPartitionInfos = topicDescription.partitions();
          if (topicPartitionInfos.isEmpty()) {
            throw IngestionException.error(ErrorCode.UNKNOWN_INGESTION_ERROR, Map.of("message",
                    "TopicPartitions cannot be 0 for a given topic"));
          }
          Topic topic = new Topic(topicDescription.name(), topicPartitionInfos.size(),
              topicPartitionInfos.get(0).replicas().size());
          topicFuture.complete(topic);
          return topicFuture;
        });
    CompletableFuture<Topic> resultFuture = new CompletableFuture<>();
    future.whenComplete((topicCompletableFuture, throwable) -> {
          if (nonNull(throwable)) {
            resultFuture.completeExceptionally(throwable);
          } else {
            try {
              resultFuture.complete(topicCompletableFuture.get(10, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              resultFuture.completeExceptionally(e);
            } catch (ExecutionException | TimeoutException e) {
              resultFuture.completeExceptionally(e);
            }
          }
        }
    );
    return resultFuture;
  }

  @Override
  public CompletableFuture<Set<String>> getTopicNames() {
    ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(false));
    CompletableFuture<Set<String>> resultFuture = new CompletableFuture<>();
    listTopicsResult.names().whenComplete((topicNames, throwable) -> {
      if (nonNull(throwable)) {
        resultFuture.completeExceptionally(throwable);
      } else {
        resultFuture.complete(topicNames);
      }
    });
    return resultFuture;
  }

  @Override
  public CompletableFuture<Set<Topic>> getTopics() {
    ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(false));
    KafkaFuture<CompletableFuture<Set<Topic>>> topFuture = listTopicsResult.namesToListings()
        .thenApply(topicListingsMap -> {
          Set<CompletableFuture<Topic>> topicFutures = topicListingsMap.keySet().stream()
              .map(this::getTopic)
              .collect(Collectors.toSet());
          return CompletableFuture
              .allOf(topicFutures.toArray(new CompletableFuture[0]))
              .thenApply(aVoid -> topicFutures.stream().map(CompletableFuture::join)
                  .collect(Collectors.toSet()));
        });
    CompletableFuture<Set<Topic>> allTopicsFuture = new CompletableFuture<>();
    topFuture.whenComplete((setCompletableFuture, throwable) -> {
      if (nonNull(throwable)) {
        allTopicsFuture.completeExceptionally(throwable);
      } else {
        try {
          allTopicsFuture.complete(setCompletableFuture.get(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          allTopicsFuture.completeExceptionally(e);
        } catch (ExecutionException | TimeoutException e) {
          allTopicsFuture.completeExceptionally(e);
        }
      }
    });
    return allTopicsFuture;
  }

    @Override
    public Set<String> getTopicsSync() {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(false));
        try {
            return listTopicsResult.names().get();
        } catch (InterruptedException e) {
            throw PinotDaoException.error(ErrorCode.PINOT_KAFKA_DAO_ERROR,
                    Collections.singletonMap("topics lookup interrupted", ""));
        } catch (ExecutionException e) {
            throw PinotDaoException.error(ErrorCode.PINOT_KAFKA_DAO_ERROR,
                    Collections.singletonMap("execution exception", e.getLocalizedMessage()));
        }

    }

//  @Override
//  public CompletableFuture<PartitionOffsets> getTopicOffsets(String topicName) {
//    CompletableFuture<PartitionOffsets> future = new CompletableFuture<>();
//    final KafkaFuture<List<TopicPartition>> topicPartitionsFuture = getTopicPartitions(topicName);
//    topicPartitionsFuture.whenComplete((topicPartitions, throwable) -> {
//      if (nonNull(throwable)) {
//        future.completeExceptionally(throwable);
//      } else {
//        try (final KafkaConsumer<Object, Object> kafkaConsumer = new KafkaConsumer<>(adminConsumerProperties)) {
//          final Map<TopicPartition, Long> topicPartitionLongMap = kafkaConsumer.endOffsets(topicPartitions);
//
//          PartitionOffsets partitionOffsets = new PartitionOffsets(topicPartitions.size());
//          topicPartitionLongMap.forEach(((topicPartition, offset) -> partitionOffsets
//              .add(new PartitionOffset(topicPartition.partition(), offset))));
//          partitionOffsets.sort();
//          future.complete(partitionOffsets);
//        } catch (Exception ex) {
//          log.error("Exception in getting topicOffsets for topic: " + topicName, ex);
//          future.completeExceptionally(ex);
//        }
//      }
//    });
//    return future;
//  }
//
//  @Override
//  public CompletableFuture<PartitionOffsets> getConsumerGroupOffsets(String topicName, String groupId) {
//    CompletableFuture<PartitionOffsets> future = new CompletableFuture<>();
//
//    final KafkaFuture<List<TopicPartition>> topicPartitionsFuture = getTopicPartitions(topicName);
//    topicPartitionsFuture.whenComplete((topicPartitions, throwable) -> {
//      if (nonNull(throwable)) {
//        future.completeExceptionally(throwable);
//      } else {
//        final ListConsumerGroupOffsetsOptions listConsumerGroupOffsetsOptions = new ListConsumerGroupOffsetsOptions();
//        listConsumerGroupOffsetsOptions.topicPartitions(topicPartitions);
//        final ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient
//            .listConsumerGroupOffsets(groupId, listConsumerGroupOffsetsOptions);
//        listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata()
//            .whenComplete((topicPartitionOffsetAndMetadataMap, throwable2) -> {
//              if (nonNull(throwable2)) {
//                future.completeExceptionally(throwable2);
//              } else {
//                try {
//                  PartitionOffsets partitionOffsets = new PartitionOffsets(topicPartitionOffsetAndMetadataMap.size());
//                  topicPartitionOffsetAndMetadataMap.forEach((topicPartition, offsetAndMetadata) -> {
//                    final int partition = topicPartition.partition();
//                    final PartitionOffset partitionOffset = new PartitionOffset(partition,
//                        offsetAndMetadata.offset());
//                    partitionOffsets.add(partitionOffset);
//                  });
//                  partitionOffsets.sort();
//                  future.complete(partitionOffsets);
//                } catch (Exception ex) {
//                  log.error("Exception in getting consumer group offsets", ex);
//                  future.completeExceptionally(ex);
//                }
//              }
//            });
//      }
//    });
//    return future;
//  }

//  @Override
//  public CompletionStage<PartitionOffsets> getConsumerGroupLags(String topicName, String consumerGroupId) {
//    final CompletableFuture<PartitionOffsets> topicOffsetsFuture = getTopicOffsets(topicName);
//    final CompletableFuture<PartitionOffsets> consumerGroupOffsetsFuture = getConsumerGroupOffsets(topicName,
//        consumerGroupId);
//
//    return CompletableFuture.allOf(topicOffsetsFuture, consumerGroupOffsetsFuture).thenApply(aVoid -> {
//      final PartitionOffsets partitionOffsets = topicOffsetsFuture.join();
//      final PartitionOffsets consumerGroupOffsets = consumerGroupOffsetsFuture.join();
//
//      final PartitionOffsets lag = new PartitionOffsets(partitionOffsets.count());
//      for (PartitionOffset partitionOffset : partitionOffsets) {
//        final PartitionOffset consumerGroupOffset = consumerGroupOffsets.get(partitionOffset.getPartition());
//        lag.add(new PartitionOffset(partitionOffset.getPartition(),
//            Math.max(0, partitionOffset.getOffset() - consumerGroupOffset.getOffset())));
//      }
//      return lag;
//    }).exceptionally(throwable -> {
//      log.error("Exception in computing lag for topic/consumerGroupId: {}/{}", topicName, consumerGroupId, throwable);
//      return null;
//    });
//  }

  private KafkaFuture<List<TopicPartition>> getTopicPartitions(String topicName) {
    final DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(topicName));
    return describeTopicsResult.all()
        .thenApply(new Function<Map<String, TopicDescription>, List<TopicPartition>>() {
          @Override
          public List<TopicPartition> apply(Map<String, TopicDescription> topicDescriptionMap) {
            final TopicDescription topicDescription = topicDescriptionMap.get(topicName);
            return IntStream.range(0, topicDescription.partitions().size())
                .mapToObj(partition -> new TopicPartition(topicName, partition))
                .collect(Collectors.toList());
          }
        });
  }
}
