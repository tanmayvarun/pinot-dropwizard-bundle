package io.dropwizard.pinot.storage.pinot.pinotspec.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import lombok.Setter;

@JsonSubTypes({
        @JsonSubTypes.Type(value = DimensionField.class, name = "DIMENSION_FIELD"),
        @JsonSubTypes.Type(value = MetricField.class, name = "METRIC_FIELD"),
        @JsonSubTypes.Type(value = DateTimeField.class, name = "DATETIME_FIELD")
})
@Getter
@Setter
public abstract class Field {

    private FieldType fieldType;

    private String name;

    private PinotSupportedColumnTypeV1 dataType;

    protected Field(FieldType fieldType, String name, PinotSupportedColumnTypeV1 dataType) {
        validType(dataType);
        this.fieldType = fieldType;
        this.name = name;
        this.dataType = dataType;
    }

    protected Field(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    abstract boolean validType(PinotSupportedColumnTypeV1 type);
}
