package io.dropwizard.pinot.storage.pinot.pinotspec.schema;

import lombok.Setter;

/**
 * For spec refer
 * https://docs.pinot.apache.org/configuration-reference/schema#datetimefieldspec
 * @param <V>
 */
@Setter
public class DateTimeField<V> extends Field {

    private String format;

    private String granularity;

    private V defaultNullValue;

    public DateTimeField(String name, PinotSupportedColumnTypeV1 datatype) {
        super(FieldType.DIMENSION_FIELD, name, datatype);
    }

    public DateTimeField() {
        super(FieldType.DATETIME_FIELD);
    }

    @Override
    boolean validType(PinotSupportedColumnTypeV1 type) {
        return true;
    }
}
