A rich suite of functional and admin APIs for a Pinot datastore.

## Primary functions:
- Control plane APIs for creating and updating pinot table, associated schema etc.
- Data plane APIs for reading and writing to a pinot table without proactive error correction
- APIs for creating select, aggregation queries in fluent-style with inbuilt syntax validation and sql query creation

## Functional APIs
```
public interface PinotDao<T> {

    ProducedMeta ingestRowSync(String topicName, T entity);

    CompletableFuture<ProducedMeta> ingestRow(String topicName, T entity);

    Object query(RawQuery query);

    SelectionQueryResponse<T> select(SelectQuery query);

    SelectionQueryResponse<T> getRowByKey(String tableName, DomainParam partitionKeyParam,
                                       DomainParam rowKeyParam, Class entityclass);

}
```

## Admin APIs
```
public interface PinotAdminDao {

    Health ping();

    Set<String> getAllTopics();

    Topic getTopic(String topicName);

    void validateTopicExists(String topic);

    PinotSchemaConfig createSchema(PinotSchemaConfig schemaConfig);

    TableConfig createTable(TableConfig tableConfig);

    Object validateSchemaConfig(PinotSchemaConfig schemaConfig);

    Object validateTableConfig(TableConfig tableConfig);

    PinotSchemaConfig getSchema(String schemaName);

    Optional<PinotSchemaConfig> getSchemaSilently(String schemaName);

    TableConfig getTableConfig(String tableId, TableType tableType);

    Long getTableDataRetentionInDays(String tableId, TableType tableType);

    Map<String, List<String>> tagServers(RealtimeServerTaggingRequest realtimeServerTaggingRequest);
}
```

## Defining an Entity

### Annotations

#### PinotTableEntity
- tableName - name of actual pinot table
- type - type of pinot table REALTIME/OFFLINE
- ingestionKafkaTopicName - kafka topic mapped to pinot table for ingestion

#### PartitionKey
Partition key to choose kafka partition to ingest update.

#### UniqueKey
Unique identifier for the row being ingested to pinot table.

### Sample Entity Class
package com.example.pinot.entities;

````
import io.dropwizard.pinot.storage.pinot.entities.Column;
import io.dropwizard.pinot.storage.pinot.entities.PartitionKey;
import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import io.dropwizard.pinot.storage.pinot.entities.UniqueKey;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PinotTableEntity(
tableName = "offline_merchant_settlement_entity_table",
type = TableType.REALTIME,
ingestionKafkaTopicName = "mass_offline_payment_settlements_table"
)
public class OrderTransactionEntity {

    @PartitionKey
    private String partitionId;

    @UniqueKey
    private String orderId;

    @Column(name = "paymentType", nullable = false)
    private String orderType;

    @Column(name = "transactionId", nullable = false)
    private String transactionId;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "netAmount", nullable = false)
    private double netAmount;

    @Column(name = "sgst", nullable = false)
    private double sgst;

    @Column(name = "cgst", nullable = false)
    private double cgst;

    @Column(name = "transactionDate", nullable = false)
    private long transactionDate;

    @Column(name = "createdDate", nullable = false)
    private long createdDate;

    @Column(name = "settlementDate", nullable = false)
    private long settlementDate;

    @Column(name = "state", nullable = false)
    private String state;

}
````

## PinotDaoBundle Initialization

### PinotDaoBundle configuration
````
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class PinotDaoBundleConfig {

    @Builder.Default
    private Mode mode = Mode.EXECUTION;
    private KafkaProducerConfig kafkaProducerConfig;
    private String keytabPath;

    /**
     * For local testing, are not setting an LB, but in production an LB will front all the brokers.
     */
    private Endpoint brokerEndpoint;

    private Endpoint controllerEndpoint;

}
````

#### Sample config with SASL security for local testing
````
pinotDaoBundleConfig:
  mode: EXECUTION
  kafkaProducerConfig:
    bootstrapServers: localhost:19092
    acks: -1
    securityProtocol: SASL_PLAINTEXT
    saslKerberosServiceName: kafka
    compressionType: snappy
    batchSize: 16384
    lingerMs: 2
    maxRequestSize: 5242880
    receiveBufferBytes: 131072
    sendBufferBytes: 1048576
    metadataMaxAgeMs: 300000
  brokerEndpoint:
    host: localhost
    port: 8099
  controllerEndpoint:
    host: localhost
    port: 9000

````

#### Sample config without kafka security for local testing
````
pinotDaoBundleConfig:
  mode: EXECUTION
  kafkaProducerConfig:
    bootstrapServers: localhost:19092
    acks: -1
    compressionType: snappy
    batchSize: 16384
    lingerMs: 2
    maxRequestSize: 5242880
    receiveBufferBytes: 131072
    sendBufferBytes: 1048576
    metadataMaxAgeMs: 300000
  brokerEndpoint:
    host: localhost
    port: 8099
  controllerEndpoint:
    host: localhost
    port: 9000

````

````
public class AppConfig extends io.dropwizard.Configuration {

    private PinotDaoBundleConfig pinotDaoBundleConfig;
    .
    .
}
````

## Sample PinotDaoBundle Initialization
````
public class App extends Application<AppConfig> {

    
    @Override
    public void initialize(final Bootstrap<AppConfig> bootstrap){
        log.info("Initializing Core Engine");
        PinotDaoBundle pinotDaoBundle = pinotDaoBundle();
        bootstrap.addBundle(pinotDaoBundle);
        .
        .
    }

    private PinotDaoBundle<AppConfig> pinotDaoBundle() {
        return new PinotDaoBundle<AppConfig>() {
            @Override
            protected PinotDaoBundleConfig getConfig(AppConfig appConfig) {
                return appConfig.getPinotDaoBundleConfig();
            }

            @Override
            protected List<Class> registerEntities() {
                return Lists.newArrayList(
                        OrderTransactionEntity.class
                );
            }

            //Implement if SASL scheme chosen for Kafka access
            @Override
            protected void fetchKeytab(AppConfig appConfig) {
                //fetch keytab to appConfig.pinotDaoBundleConfig().getKeytabPath.
            }
        };
    }

}
````

## Usage
````
<dependency>
    <groupId>io.github.pinotdao</groupId>
    <artifactId>pinotdao-bundle</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
````

## Build from source
````
mvn clean package
````