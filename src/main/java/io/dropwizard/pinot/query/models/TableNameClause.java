package io.dropwizard.pinot.query.models;

import lombok.Builder;

public class TableNameClause implements SqlQueryParam {

    private final String tableName;

    @Builder
    public TableNameClause(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public SqlQueryParamType getType() {
        return SqlQueryParamType.TABLE_NAME;
    }

    @Override
    public String stringify() {
        return tableName;
    }
}
