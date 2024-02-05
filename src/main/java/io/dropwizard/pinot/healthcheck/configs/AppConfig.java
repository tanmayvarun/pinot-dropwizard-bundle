package io.dropwizard.pinot.healthcheck.configs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.Configuration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfig extends Configuration {

    /**
     * recordConsumerConfig:
     *   topicName: com_phonepe_accounting_plutus_database_entities_reports_merchant__MerchantSettlementTransaction___unit
     *   commitBatchSize: 500000
     *   pollTimeMs: 50000
     *   triggerIntervalSeconds: 600
     *   kafkaConsumerConfig:
     *     groupId: "pinot_settlement_report_zfs"
     *     bootstrapServers: SASL_PLAINTEXT:///prd-hdplucykfk101.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk107.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk110.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk115.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk120.phonepe.nm5:6667
     *     securityProtocol: SASL_PLAINTEXT
     *     saslKerberosServiceName: kafka
     *     enableAutoCommit: false
     *     autoCommitIntervalMs: 30000
     *     autoOffsetReset: earliest
     *     pollTimeMs: 1000
     *     maxPollRecords: 3000
     *
     * # correct kafka topic -- to which to publish
     *
     * recordProducerConfig:
     *   topicName: test_test_zfs
     *   kafkaProducerProperties:
     *     bootstrapServers: SASL_PLAINTEXT:///prd-hdplucykfk101.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk110.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk115.phonepe.nm5:6667,SASL_PLAINTEXT:///prd-hdplucykfk120.phonepe.nm5:6667
     *     acks: -1
     *     securityProtocol: SASL_PLAINTEXT
     *     saslKerberosServiceName: kafka
     *     compressionType: snappy
     *     batchSize: 100000
     *     lingerMs: 20
     *     maxRequestSize: 12582912
     *     receiveBufferBytes: 5242880
     *     sendBufferBytes: 12582912
     *     metadataMaxAgeMs: 300000
     *     bufferMemory: 100000000
     */

    @Valid
    private RecordConsumerConfig recordConsumerConfig;

    @NotNull
    private PinotKafkaIngestionConfig pinotKafkaIngestionConfig;

    @NotNull
    private GoLucyConfig goLucyConfig;

    private ReportDatastoreConfig reportDatastoreConfig;

    @NotNull
    private StreamProcessingConfig streamProcessingConfig;


//    private List<TopicConfig> topicConfigs;

  //  private ServiceDiscoveryConfiguration discovery;
}
