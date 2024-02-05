package io.dropwizard.pinot.query.models;

public enum SqlQueryParamType {

    COLUMN_LIST(true),
    TABLE_NAME(true),
    WHERE_CLAUSE(true),
    LIMIT(false)
    ;

    private boolean mandatory;

    SqlQueryParamType(boolean mandatory) {
        this.mandatory = mandatory;
    }

}
