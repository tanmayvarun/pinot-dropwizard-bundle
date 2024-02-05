package io.dropwizard.pinot.query.services.impl;

import io.dropwizard.pinot.query.models.Limit;
import io.dropwizard.pinot.query.models.selection.ColumnSelection;
import io.dropwizard.pinot.query.services.QueryBuilder;

import java.util.List;
import java.util.function.Predicate;

public class QueryBuilderImpl implements QueryBuilder {

    @Override
    public String selectQueryBuilder(String tableName, ColumnSelection columnSelection,
                                     List<Predicate> predicates, Limit limit) {
        StringBuilder builder = new StringBuilder();
        return null;


    }
}
