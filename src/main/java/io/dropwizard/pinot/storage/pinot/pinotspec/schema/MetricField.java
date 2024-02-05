package io.dropwizard.pinot.storage.pinot.pinotspec.schema;

import lombok.Setter;

/**
 * For spec refer
 * https://docs.pinot.apache.org/configuration-reference/schema#metricfieldspec
 */
@Setter
public class MetricField<V> extends Field {

    private V defaultNullValue;

    private V value;

    public MetricField(String name, PinotSupportedColumnTypeV1 dataType) {
        super(FieldType.METRIC_FIELD, name, dataType);
    }

    public MetricField() {
        super(FieldType.METRIC_FIELD);
    }

    @Override
    boolean validType(PinotSupportedColumnTypeV1 type) {
        return true;
    }
}
