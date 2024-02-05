package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.healthcheck.Health;
import io.dropwizard.pinot.housekeeping.RealtimeServerTaggingRequest;
import io.dropwizard.pinot.models.kafka.Topic;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotSchemaConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
