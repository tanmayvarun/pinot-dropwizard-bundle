package io.dropwizard.pinot.repository.pinot.helper;

import com.google.common.collect.ImmutableMap;

import io.dropwizard.pinot.cache.PinotTableCache;
import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import io.dropwizard.pinot.storage.pinot.entities.*;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.Field;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.PinotSupportedColumnTypeV1;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotSchemaConfig;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableDetails;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.PinotTableKey;
import io.dropwizard.pinot.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EntityRegistry {
    private final Map<Class, EntityContext> entityContextMapping = new HashMap<>();

    private final PinotTableCache pinotTableCache;

    public EntityRegistry(PinotTableCache pinotTableCache) {
        this.pinotTableCache = pinotTableCache;
    }

    public boolean registerEntities(List<Class> entities) {
        entities.forEach(entity -> {
            validateEntity(entity);
            try {
                entityContextMapping.put(entity, buildContext(entity));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });

        return true;
    }

    public EntityContext checkEntityInRegistry(Class entity) {
        if (! entityContextMapping.containsKey(entity)) {
            throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                    ImmutableMap.of("entity not registered at startup", entity.getClass()));
        }

        return entityContextMapping.get(entity);
    }

    private EntityContext buildContext(Class entity) throws NoSuchFieldException {
        PinotTableEntity annotation = (PinotTableEntity) entity.getAnnotation(PinotTableEntity.class);
        java.lang.reflect.Field partitionKey = entity.getDeclaredField(ReflectionUtils.annotatedWith(entity, PartitionKey.class).get().getName());
        partitionKey.setAccessible(true);
        java.lang.reflect.Field uniqueKey = entity.getDeclaredField(ReflectionUtils.annotatedWith(entity, UniqueKey.class).get().getName());
        uniqueKey.setAccessible(true);
        return EntityContext.builder()
                .tableName(annotation.tableName())
                .type(annotation.type())
                .ingestionKafkatopicName(annotation.ingestionKafkaTopicName())
                .partitionKeyColumn(ReflectionUtils.annotatedWith(entity, PartitionKey.class).get().getName())
                .uniqueKeyColumn(ReflectionUtils.annotatedWith(entity, UniqueKey.class).get().getName())
                .partitionKey(partitionKey)
                .uniqueKey(uniqueKey)
                .build();
    }

    private void validateEntity(Class entity) {
        if (!entity.isAnnotationPresent(PinotTableEntity.class)) {
            throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY, ImmutableMap.of("entity", entity));
        }
        PinotTableEntity annotation = (PinotTableEntity) entity.getAnnotation(PinotTableEntity.class);
        PinotTableDetails tableDetails = pinotTableCache.get(PinotTableKey.builder()
                .tableName(annotation.tableName())
                .tableType(annotation.type())
                .build());
        validateAllKeysWithRespectToSchema(entity, tableDetails);
        validatePartitionKey(entity, tableDetails);
        validateTopicName(annotation.ingestionKafkaTopicName(), tableDetails);
    }


    /**
     * <ol>
     *     Following validations will be done.
     *     <li>
     *         validate every annotated field against schema.
     *     </li>
     *     <li>
     *     </li>
     * </ol>
     * @param entity
     * @param tableDetails
     */
    private void validateAllKeysWithRespectToSchema(Class entity, PinotTableDetails tableDetails) {
        PinotSchemaConfig schemaConfig = tableDetails.getSchemaConfig();

        //only fields annotated as column will be considered and compared, rest of the fields will never be serialized
        //or validated.

        Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class)).
                forEach(field -> {
                    Column column = field.getAnnotation(Column.class);

                    String columnName = StringUtils.isNotBlank(column.name()) ?
                            column.name() : field.getName();

            Optional<Field> databaseFieldOptional = keyFoundInSchema(columnName, schemaConfig);

            //check key
            if (databaseFieldOptional.isEmpty()) {
                throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                        "Column not found in schema: " + columnName, null, false);
            }

            //check value datatype
            Field databaseField = databaseFieldOptional.get();

            boolean validDataType = databaseField.getDataType().accept(new PinotSupportedColumnTypeV1.PinotSupportedColumnTypeV1Visitor<Object, Boolean>() {
                @Override
                public Boolean visitInt() {
                    return field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class);
                }

                @Override
                public Boolean visitLong() {
                    return field.getType().equals(Long.TYPE) || field.getType().equals(Long.class);
                }

                @Override
                public Boolean visitFloat() {
                    return field.getType().equals(Float.TYPE) || field.getType().equals(Float.class);
                }

                @Override
                public Boolean visitDouble() {
                    return field.getType().equals(Double.TYPE) || field.getType().equals(Double.class);
                }

                @Override
                public Boolean visitBoolean() {
                    return field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class);
                }

                @Override
                public Boolean visitTimestamp() {
                    return field.getType().equals(Long.TYPE) || field.getType().equals(Long.class);
                }

                @Override
                public Boolean visitString() {
                    return field.getType().equals(String.class);
                }

                @Override
                public Boolean visitBytes() {
                    return false;
                }

                @Override
                public Boolean visitJson() {
                    return false;
                }
            });

            if (! validDataType) {
                throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                        ImmutableMap.of(field.getName() + " datatype in entity is invalid", field.getType()));
            }
        });
    }

    private Optional<Field> keyFoundInSchema(String key, PinotSchemaConfig schemaConfig) {
        return schemaConfig.getField(key);
    }

    private void validatePartitionKey(Class entity, PinotTableDetails tableDetails) {
        Optional<java.lang.reflect.Field> partitionKeyField = ReflectionUtils.annotatedWith(entity, PartitionKey.class);
        if (partitionKeyField.isEmpty()) {
            throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                    "Partition key is not defined for entity: " + entity.getSimpleName(), null, false);
        }
        if (! partitionKeyField.get().getName().equals(getPartitionKey(tableDetails))) {
            throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                    String.format("Partition key in entity mismatch from pinot table for entity %s", entity),
                    ImmutableMap.of("table", tableDetails.getTableConfig().getTableName()), false);
        }
    }

    private void validateTopicName(String topic, PinotTableDetails tableDetails) {
        if (! tableDetails.getTableConfig().getTableIndexConfig().getStreamConfigs().get("stream.kafka.topic.name").toString().equals(topic)) {
            throw PinotDaoException.error(ErrorCode.INVALID_TABLE_ENTITY,
                    "Partition key is null or invalid",
                    ImmutableMap.of("table", tableDetails.getTableConfig().getTableName()), false);
        }
    }

    private String getPartitionKey(PinotTableDetails tableDetails) {
        return tableDetails.getTableConfig().getTableIndexConfig().getSegmentPartitionConfig().getColumnPartitionMap()
                .keySet().iterator().next();
    }
}
