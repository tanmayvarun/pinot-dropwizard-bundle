package io.dropwizard.pinot.repository.pinot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.dropwizard.pinot.cache.PinotTableCache;
import io.dropwizard.pinot.healthcheck.configs.exception.*;
import io.dropwizard.pinot.models.domainparams.DomainParam;
import io.dropwizard.pinot.models.kafka.kafkaproducer.ProducedMeta;
import io.dropwizard.pinot.TopicService;
import io.dropwizard.pinot.query.models.Limit;
import io.dropwizard.pinot.query.models.TableNameClause;
import io.dropwizard.pinot.query.models.WhereClause;
import io.dropwizard.pinot.query.models.filters.EqualsFilter;
import io.dropwizard.pinot.query.models.selection.SelectQuery;
import io.dropwizard.pinot.repository.pinot.clients.PinotClient;
import io.dropwizard.pinot.repository.pinot.helper.EntityRegistry;
import io.dropwizard.pinot.storage.pinot.entities.EntityContext;
import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.processed.response.SelectionQueryResponse;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.RawQuery;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.Field;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.PinotSupportedColumnTypeV1;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotSchemaConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableDetails;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableKey;
import io.dropwizard.pinot.utils.SerDe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.ResultSetGroup;
import org.apache.pinot.client.ResultTableResultSet;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class PinotDaoImpl<T> implements PinotDao<T> {

    private final TopicService<Map<String, Object>> topicService;

    private final PinotClient pinotClient;

    private final EntityRegistry entityRegistry;

    private final EntityMapper entityMapper;

    private final Class entityClass;

    public PinotDaoImpl(TopicService<Map<String, Object>> topicService, PinotClient pinotClient, EntityRegistry entityRegistry,
                        EntityMapper entityMapper, Class entityClass) {
        entityRegistry.checkEntityInRegistry(entityClass);
        this.topicService = topicService;
        this.pinotClient = pinotClient;
        this.entityRegistry = entityRegistry;
        this.entityMapper = entityMapper;
        this.entityClass = entityClass;
    }

    @Override
    public ProducedMeta ingestRowSync(String topicName, T entity) {
        try {
            CompletableFuture<ProducedMeta> future = ingestInternal(topicName, entity);
            log.info("Row ingested successfully: {}", entity);
            return future.get();
        } catch (Exception ex) {
            //swallow
            log.error("Exception whiling posting message to kafka {}", ExceptionUtils.getStackTrace(ex));
            throw IngestionException.propagate(ErrorCode.INVALID_TABLE_ENTITY, ex);
        }
    }

    @Override
    public CompletableFuture<ProducedMeta> ingestRow(String topicName, T entity) {
        try {
            return ingestInternal(topicName, entity);
        } catch (Exception ex) {
            //swallow
            throw IngestionException.propagate(ErrorCode.INVALID_TABLE_ENTITY, ex);
        }
    }

    private CompletableFuture<ProducedMeta> ingestInternal(String topic, T entity) {
        try {
            EntityContext context = entityRegistry.checkEntityInRegistry(entity.getClass());
            validateObjectAgainstEntity(entity);
            String partitionKey = (String) context.getPartitionKey().get(entity);
            Map<String, Object> map = SerDe.convertValue(entity, new TypeReference<Map<String, Object>>(){});
            return topicService.postMessage(topic, partitionKey, map);
        } catch (Exception ex) {
            //swallow
            log.error("Exception whiling posting message to kafka {}", ExceptionUtils.getStackTrace(ex));
            throw IngestionException.error(ErrorCode.INVALID_TABLE_ENTITY, Map.of("invalid entity", entity));
        }
    }

    @Override
    public Object query(RawQuery query) {
        return pinotClient.query(query.getSql());
    }

    /**
     * Holds logic of converting raw response into a selection response
     * @param query
     * @return
     */
    @Override
    public SelectionQueryResponse<T> select(SelectQuery query) {
        try {
            ResultSetGroup resultSetGroup = pinotClient.query(query.sql());

            if (resultSetGroup.getResultSetCount() == 0) {
                return SelectionQueryResponse.<T>builder()
                        .executionStats(resultSetGroup.getExecutionStats())
                        .results(new ArrayList<>())
                        .build();
            }

            //single resultset for SELECT query type
            Preconditions.checkArgument(resultSetGroup.getResultSetCount() == 1);

            ResultTableResultSet resultTableResultSet = (ResultTableResultSet) resultSetGroup.getResultSet(0);

            return SelectionQueryResponse.<T>builder()
                    .executionStats(resultSetGroup.getExecutionStats())
                    .results(entityMapper.map(resultTableResultSet, query.getEntityClass()))
                    .build();
        } catch (PinotClientException ex) {
            log.error("Error occured executing pinot query {}, error {}", query.sql(), ex.getLocalizedMessage());
            throw QueryProcessingException.error(QueryErrorCode.UNKNOWN_ERROR, query.sql(), ex.getLocalizedMessage(), true);
        } catch (Exception ex) {
            if (ex instanceof ConnectException) {
                //todo: raise alert that db connectivity degraded or lost.
                throw QueryProcessingException.error(QueryErrorCode.DAO_CONNECTION_ERROR, query.sql(),
                        null, false);
            }
            throw QueryProcessingException.error(QueryErrorCode.UNKNOWN_ERROR, query.sql(), ex.getLocalizedMessage(), true);
        }
    }

    public SelectionQueryResponse<T> getRowByKey(String tableName, DomainParam partitionKeyParam,
                                                      DomainParam rowKeyParam, Class klass) {
        return select(SelectQuery.builder()
                .tableNameClause(TableNameClause.builder().tableName(tableName).build())
                .entityClass(klass)
                .limit(Limit.builder().limit(1).build())
                .whereClause(WhereClause.builder()
                        .filters(Lists.newArrayList(
                                EqualsFilter.builder()
                                        .domainParam(partitionKeyParam)
                                        .build(),
                                EqualsFilter.builder()
                                        .domainParam(rowKeyParam)
                                        .build()
                        ))
                        .build())
                .build());
    }

    /**
     * <ol>
     *     Validate that the object contains the essential data for publishing.
     *     <li>
     *         Validates partition key column value is not null
     *     </li>
     *     <li>
     *         Validates unique key column value is not null
     *     </li>
     *     <li>
     *         Validates value of non-nullable field is not null
     *     </li>
     * </ol>
     * @param entity
     * @param <T>
     */
    private <T> void validateObjectAgainstEntity(T entity) {
        entityRegistry.checkEntityInRegistry(entity.getClass());
    }
}
