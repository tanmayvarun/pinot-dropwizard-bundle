package io.dropwizard.pinot.repository.pinot;

import io.dropwizard.pinot.healthcheck.configs.exception.QueryErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.QueryProcessingException;
import io.dropwizard.pinot.storage.pinot.entities.Column;
import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.PinotSupportedColumnTypeV1;
import org.apache.commons.lang3.EnumUtils;
import org.apache.pinot.client.ResultTableResultSet;
import io.dropwizard.pinot.utils.SerDe;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EntityMapperImpl implements EntityMapper {

    @Override
    public List<Object> map(ResultTableResultSet resultTableResultSet,
                                      Class entityClass) {

        int rowCount = resultTableResultSet.getRowCount();
        int columnCount = resultTableResultSet.getColumnCount();

        List<Object> entities = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int j = 0; j < columnCount; j++) {
                addValue(rowMap, i, j, resultTableResultSet);
            }
            entities.add(SerDe.convertValue(rowMap, entityClass));
        }

        return entities;
    }

    @Override
    public <T> Map<String, Object> serialize(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for (Field field: fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                try {
                    Column column = field.getAnnotation(Column.class);
                    map.put(Objects.requireNonNull(column.name()), field.get(entity));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return map;
    }

    private void addValue(Map<String, Object> result, int row, int column, ResultTableResultSet resultSet) {

        if (! EnumUtils.isValidEnum(PinotSupportedColumnTypeV1.class, resultSet.getColumnDataType(column))) {
            throw QueryProcessingException.error(QueryErrorCode.COLUMN_DATATYPE_NOT_COMPATIBLE,
                    resultSet.getColumnDataType(column), false);
        }

        PinotSupportedColumnTypeV1 dataType = PinotSupportedColumnTypeV1.valueOf(resultSet.getColumnDataType(column));

        Object value = dataType.accept(new PinotSupportedColumnTypeV1.PinotSupportedColumnTypeV1Visitor<Void, Object>() {

            @Override
            public Object visitInt() {
                return resultSet.getInt(row, column);
            }

            @Override
            public Object visitLong() {
                return resultSet.getLong(row, column);
            }

            @Override
            public Object visitFloat() {
                return resultSet.getFloat(row, column);
            }

            @Override
            public Object visitDouble() {
                return resultSet.getDouble(row, column);
            }

            @Override
            public Object visitBoolean() {
                return resultSet.getInt(row, column) == 1;
            }

            @Override
            public Object visitTimestamp() {
                String timestampString = resultSet.getString(row, column);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date date = null;
                try {
                    date = df.parse(timestampString);
                    return date.getTime();
                } catch (ParseException e) {
                    //swallow parsing exception.
                    return timestampString;
                }
            }

            @Override
            public Object visitString() {
                return resultSet.getString(row, column);
            }

            @Override
            public Object visitBytes() {
                throw QueryProcessingException.error(QueryErrorCode.COLUMN_DATATYPE_NOT_COMPATIBLE, "bytes", false);
            }

            @Override
            public Object visitJson() {
                throw QueryProcessingException.error(QueryErrorCode.COLUMN_DATATYPE_NOT_COMPATIBLE, "json", false);
            }
        });

        result.put(resultSet.getColumnName(column), value);
    }
}
