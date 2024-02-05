package io.dropwizard.pinot.config;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KafkaProducerConfig {

    private String acks;
    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String bufferMemory;
    private String compressionType;
    private String sslKeyPassword;
    private String sslKeystoreLocation;
    private String sslKeystorePassword;
    private String sslTruststoreLocation;
    private String sslTruststorePassword;
    private String batchSize;
    private String clientId;
    private String connectionsMaxIdleMs;
    private String lingerMs;
    private String maxBlockMs;
    private String maxRequestSize;
    private String partitionerClass;
    private String receiveBufferBytes;
    private String requestTimeoutMs;
    private String saslJaasConfig;
    private String saslKerberosServiceName;
    private String saslMechanism;
    private String securityProtocol;
    private String sendBufferBytes;
    private String sslEnabledProtocols;
    private String sslKeystoreType;
    private String sslProtocol;
    private String sslProvider;
    private String sslTruststoreType;
    private String enableIdempotence;
    private String interceptorClasses;
    private String maxInFlightRequestsPerConnection;
    private String metadataMaxAgeMs;
    private String metricReporters;
    private String metricsNumSamples;
    private String metricsRecordingLevel;
    private String metricsSampleWindowMs;
    private String reconnectBackoffMaxMs;
    private String reconnectBackoffMs;
    private String retries;
    private String retryBackoffMs;
    private String saslKerberosKinitCmd;
    private String saslKerberosMinTimeBeforeRelogin;
    private String saslKerberosTicketRenewJitter;
    private String saslKerberosTicketRenewWindowFactor;
    private String sslCipherSuites;
    private String sslEndpointIdentificationAlgorithm;
    private String sslKeymanagerAlgorithm;
    private String sslSecureRandomImplementation;
    private String sslTrustmanagerAlgorithm;
    private String transactionTimeoutMs;
    private String transactionalId;

    public Properties toProperties() {
        Properties properties = new Properties();
        addProperty(properties, "acks", acks);
        addProperty(properties, "batch.size", batchSize);
        addProperty(properties, "bootstrap.servers", bootstrapServers);
        addProperty(properties, "buffer.memory", bufferMemory);
        addProperty(properties, "client.id", clientId);
        addProperty(properties, "compression.type", compressionType);
        addProperty(properties, "connections.max.idle.ms", connectionsMaxIdleMs);
        addProperty(properties, "enable.idempotence", enableIdempotence);
        addProperty(properties, "interceptor.classes", interceptorClasses);
        addProperty(properties, "key.serializer", keySerializer);
        addProperty(properties, "linger.ms", lingerMs);
        addProperty(properties, "max.block.ms", maxBlockMs);
        addProperty(properties, "max.in.flight.requests.per.connection", maxInFlightRequestsPerConnection);
        addProperty(properties, "max.request.size", maxRequestSize);
        addProperty(properties, "metadata.max.age.ms", metadataMaxAgeMs);
        addProperty(properties, "metric.reporters", metricReporters);
        addProperty(properties, "metrics.num.samples", metricsNumSamples);
        addProperty(properties, "metrics.recording.level", metricsRecordingLevel);
        addProperty(properties, "metrics.sample.window.ms", metricsSampleWindowMs);
        addProperty(properties, "partitioner.class", partitionerClass);
        addProperty(properties, "receive.buffer.bytes", receiveBufferBytes);
        addProperty(properties, "reconnect.backoff.max.ms", reconnectBackoffMaxMs);
        addProperty(properties, "reconnect.backoff.ms", reconnectBackoffMs);
        addProperty(properties, "request.timeout.ms", requestTimeoutMs);
        addProperty(properties, "retries", retries);
        addProperty(properties, "retry.backoff.ms", retryBackoffMs);
        addProperty(properties, "sasl.jaas.config", saslJaasConfig);
        addProperty(properties, "sasl.kerberos.kinit.cmd", saslKerberosKinitCmd);
        addProperty(properties, "sasl.kerberos.min.time.before.relogin", saslKerberosMinTimeBeforeRelogin);
        addProperty(properties, "sasl.kerberos.service.name", saslKerberosServiceName);
        addProperty(properties, "sasl.kerberos.ticket.renew.jitter", saslKerberosTicketRenewJitter);
        addProperty(properties, "sasl.mechanism", saslMechanism);
        addProperty(properties, "security.protocol", securityProtocol);
        addProperty(properties, "send.buffer.bytes", sendBufferBytes);
        addProperty(properties, "ssl.cipher.suites", sslCipherSuites);
        addProperty(properties, "ssl.enabled.protocols", sslEnabledProtocols);
        addProperty(properties, "ssl.endpoint.identification.algorithm", sslEndpointIdentificationAlgorithm);
        addProperty(properties, "ssl.key.password", sslKeyPassword);
        addProperty(properties, "ssl.keymanager.algorithm", sslKeymanagerAlgorithm);
        addProperty(properties, "ssl.keystore.location", sslKeystoreLocation);
        addProperty(properties, "ssl.keystore.password", sslKeystorePassword);
        addProperty(properties, "ssl.keystore.type", sslKeystoreType);
        addProperty(properties, "ssl.protocol", sslProtocol);
        addProperty(properties, "ssl.provider", sslProvider);
        addProperty(properties, "ssl.secure.random.implementation", sslSecureRandomImplementation);
        addProperty(properties, "ssl.trustmanager.algorithm", sslTrustmanagerAlgorithm);
        addProperty(properties, "ssl.truststore.location", sslTruststoreLocation);
        addProperty(properties, "ssl.truststore.password", sslTruststorePassword);
        addProperty(properties, "ssl.truststore.type", sslTruststoreType);
        addProperty(properties, "transaction.timeout.ms", transactionTimeoutMs);
        addProperty(properties, "transactional.id", transactionalId);
        addProperty(properties, "value.serializer", valueSerializer);

        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return properties;
    }

    static void addProperty(Properties properties, String key, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            properties.put(key, value);
        }
    }
}
