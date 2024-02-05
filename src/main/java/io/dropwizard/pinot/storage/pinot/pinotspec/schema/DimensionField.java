package io.dropwizard.pinot.storage.pinot.pinotspec.schema;

import lombok.Builder;
import lombok.Setter;

/**
 * For spec refer
 * https://docs.pinot.apache.org/configuration-reference/schema#dimensionfieldspec
 * @param <V>
 */

@Setter
public class DimensionField<V> extends Field {

    private V defaultNullValue;

    private V value;

    @Builder.Default
    private Boolean singleValueField = true;

    @Builder
    public DimensionField(String name, PinotSupportedColumnTypeV1 dataType, boolean singleValueField) {
        super(FieldType.DIMENSION_FIELD, name, dataType);
    }

    public DimensionField() {
        super(FieldType.DIMENSION_FIELD);
    }

    @Override
    boolean validType(PinotSupportedColumnTypeV1 type) {
        return true;
    }
}
