package io.dropwizard.pinot.query.models.filters;

public enum FilterType {
    RANGE,
    EQUALS,
    NOT_EQUALS,
    NULL,
    NOT_NULL,
    IN,
    NOT_IN,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL
    ;
}
