package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import org.apache.pinot.client.ResultTableResultSet;

import java.util.List;
import java.util.Map;

public interface EntityMapper {

    <T> List<T> map(ResultTableResultSet resultTableResultSet,
                               Class entityClass);

    <T> Map<String, Object> serialize(T entity);

}
