package io.dropwizard.pinot.config;


import com.google.common.base.Strings;
import lombok.*;

import java.io.Serializable;
import java.util.Properties;

@Builder
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class KafkaConsumerConfig implements Serializable {

  private long pollTimeMs;
  private String bootstrapServers;
  private String keyDeserializer;
  private String valueDeserializer;
  private String fetchMinBytes;
  private String groupId;
  private String heartbeatIntervalMs;
  private String maxPartitionFetchBytes;
  private String sessionTimeoutMs;
  private String autoOffsetReset;
  private String connectionsMaxIdleMs;
  private String enableAutoCommit;
  private String excludeInternalTopics;
  private String fetchMaxBytes;
  private String isolationLevel;
  private String maxPollIntervalMs;
  private String maxPollRecords;
  private String partitionAssignmentStrategy;
  private String receiveBufferBytes;
  private String requestTimeoutMs;
  private String saslJaasConfig;
  private String saslKerberosServiceName;
  private String saslMechanism;
  private String securityProtocol;
  private String sendBufferBytes;
  private String autoCommitIntervalMs;
  private String checkCrcs;
  private String clientId;
  private String fetchMaxWaitMs;
  private String interceptorClasses;
  private String metadataMaxAgeMs;
  private String metricReporters;
  private String metricDropwizardRegistry;
  private String metricsNumSamples;
  private String metricsRecordingLevel;
  private String metricsSampleWindowMs;
  private String reconnectBackoffMaxMs;
  private String reconnectBackoffMs;
  private String retryBackoffMs;
  private String saslKerberosKinitCmd;
  private String saslKerberosMinTimeBeforeRelogin;
  private String saslKerberosTicketRenewJitter;

  public Properties toProperties() {
    Properties properties = new Properties();
    addProperty(properties, "auto.commit.interval.ms", autoCommitIntervalMs);
    addProperty(properties, "auto.offset.reset", autoOffsetReset);
    addProperty(properties, "bootstrap.servers", bootstrapServers);
    addProperty(properties, "check.crcs", checkCrcs);
    addProperty(properties, "client.id", clientId);
    addProperty(properties, "connections.max.idle.ms", connectionsMaxIdleMs);
    addProperty(properties, "enable.auto.commit", enableAutoCommit);
    addProperty(properties, "exclude.internal.topics", excludeInternalTopics);
    addProperty(properties, "fetch.max.bytes", fetchMaxBytes);
    addProperty(properties, "fetch.max.wait.ms", fetchMaxWaitMs);
    addProperty(properties, "fetch.min.bytes", fetchMinBytes);
    addProperty(properties, "group.id", groupId);
    addProperty(properties, "heartbeat.interval.ms", heartbeatIntervalMs);
    addProperty(properties, "interceptor.classes", interceptorClasses);
    addProperty(properties, "isolation.level", isolationLevel);
    addProperty(properties, "key.deserializer", keyDeserializer);
    addProperty(properties, "max.partition.fetch.bytes", maxPartitionFetchBytes);
    addProperty(properties, "max.poll.interval.ms", maxPollIntervalMs);
    addProperty(properties, "max.poll.records", maxPollRecords);
    addProperty(properties, "metadata.max.age.ms", metadataMaxAgeMs);
    addProperty(properties, "metric.dropwizard.registry", metricDropwizardRegistry);
    addProperty(properties, "metric.reporters", metricReporters);
    addProperty(properties, "metric.reporters", metricReporters);
    addProperty(properties, "metrics.num.samples", metricsNumSamples);
    addProperty(properties, "metrics.recording.level", metricsRecordingLevel);
    addProperty(properties, "metrics.sample.window.ms", metricsSampleWindowMs);
    addProperty(properties, "partition.assignment.strategy", partitionAssignmentStrategy);
    addProperty(properties, "receive.buffer.bytes", receiveBufferBytes);
    addProperty(properties, "reconnect.backoff.max.ms", reconnectBackoffMaxMs);
    addProperty(properties, "reconnect.backoff.ms", reconnectBackoffMs);
    addProperty(properties, "request.timeout.ms", requestTimeoutMs);
    addProperty(properties, "retry.backoff.ms", retryBackoffMs);
    addProperty(properties, "sasl.jaas.config", saslJaasConfig);
    addProperty(properties, "sasl.kerberos.kinit.cmd", saslKerberosKinitCmd);
    addProperty(properties, "sasl.kerberos.min.time.before.relogin", saslKerberosMinTimeBeforeRelogin);
    addProperty(properties, "sasl.kerberos.service.name", saslKerberosServiceName);
    addProperty(properties, "sasl.kerberos.ticket.renew.jitter", saslKerberosTicketRenewJitter);
    addProperty(properties, "sasl.mechanism", saslMechanism);
    addProperty(properties, "security.protocol", securityProtocol);
    addProperty(properties, "send.buffer.bytes", sendBufferBytes);
    addProperty(properties, "session.timeout.ms", sessionTimeoutMs);
    addProperty(properties, "value.deserializer", valueDeserializer);

    return properties;
  }

  static void addProperty(Properties properties, String key, String value) {
    if (!Strings.isNullOrEmpty(value)) {
      properties.put(key, value);
    }
  }
}