package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.models.domainparams.DomainParam;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;
import io.dropwizard.pinot.query.models.selection.SelectQuery;
import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.processed.response.SelectionQueryResponse;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.RawQuery;

import java.util.concurrent.CompletableFuture;

public interface PinotDao<T> {

    ProducedMeta ingestRowSync(String topicName, T entity);

    CompletableFuture<ProducedMeta> ingestRow(String topicName, T entity);

    Object query(RawQuery query);

    SelectionQueryResponse<T> select(SelectQuery query);

    SelectionQueryResponse<T> getRowByKey(String tableName, DomainParam partitionKeyParam,
                                       DomainParam rowKeyParam, Class entityclass);

}
