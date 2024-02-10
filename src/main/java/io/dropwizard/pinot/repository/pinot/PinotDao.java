package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;
import io.dropwizard.pinot.query.models.selection.SelectQuery;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.processed.response.SelectionQueryResponse;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.RawQuery;

import java.util.concurrent.CompletableFuture;

public interface PinotDao<T> {

    ProducedMeta ingestRowSync(String topicName, T entity);

    CompletableFuture<ProducedMeta> ingestRow(String topicName, T entity);

    Object query(RawQuery query);

    SelectionQueryResponse<T> lookup(String tableName, Class entityType,
                                     String partitionKey, String rowKey);

    SelectionQueryResponse<T> search(SelectQuery query);
}
