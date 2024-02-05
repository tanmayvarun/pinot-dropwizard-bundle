package io.dropwizard.pinot.storage.pinot.entities;

import io.dropwizard.pinot.repository.pinot.PinotDao;
import io.dropwizard.pinot.storage.pinot.pinotspec.table.TableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@Builder
@AllArgsConstructor
public class EntityContext<T> {

    String partitionKeyColumn;

    String uniqueKeyColumn;

    private TableType type;

    private String tableName;

    private String ingestionKafkatopicName;

    private Field partitionKey;

    private Field uniqueKey;

    private PinotDao<T> pinotDao;

}
