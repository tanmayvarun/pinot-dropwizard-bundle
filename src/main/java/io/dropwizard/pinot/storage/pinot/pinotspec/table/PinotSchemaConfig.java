package io.dropwizard.pinot.storage.pinot.pinotspec.table;

import io.dropwizard.pinot.storage.pinot.pinotspec.schema.DateTimeField;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.DimensionField;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.Field;
import io.dropwizard.pinot.storage.pinot.pinotspec.schema.MetricField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinotSchemaConfig {

    private String schemaName;

    /**
     * this is an ordered list.
     * this is list since PK can be composite.
     */
    private List<String> primaryKeyColumns;

    private List<DimensionField> dimensionFieldSpecs;

    private List<MetricField> metricFieldSpecs;

    private List<DateTimeField> dateTimeFieldSpecs;

    public Optional<Field> getField(String name) {
        return getAllFields().stream().filter(field -> field.getName().equals(name)).findAny();
    }

    public List<Field> getAllFields() {
        List<Field> fields = new ArrayList<>();
        fields.addAll(dimensionFieldSpecs);
        fields.addAll(metricFieldSpecs);
        fields.addAll(dateTimeFieldSpecs);
        return fields;
    }

}
