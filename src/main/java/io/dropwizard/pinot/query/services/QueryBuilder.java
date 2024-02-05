package io.dropwizard.pinot.query.services;

import io.dropwizard.pinot.query.models.Limit;
import io.dropwizard.pinot.query.models.selection.ColumnSelection;

import java.util.List;
import java.util.function.Predicate;

public interface QueryBuilder {

    String selectQueryBuilder(String tableName,
                              ColumnSelection columnSelection,
                              List<Predicate> predicates,
                              Limit limit);

}
