package io.dropwizard.pinot.query.models.selection;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.dropwizard.pinot.constants.GeneralConstants;
import io.dropwizard.pinot.query.models.Limit;
import io.dropwizard.pinot.query.models.TableNameClause;
import io.dropwizard.pinot.query.models.WhereClause;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.Query;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.QueryTimeRange;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.QueryType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SelectQuery extends Query {

    private final ColumnSelection columnSelection;

    @Builder
    public SelectQuery(final TableNameClause tableNameClause, ColumnSelection columnSelection,
                       WhereClause whereClause, QueryTimeRange queryTimeRange, Limit limit, String timeRangeColumn,
                       Class entityClass) {
        super(QueryType.SELECTION, tableNameClause, whereClause, queryTimeRange, limit, timeRangeColumn, entityClass);
        this.columnSelection = columnSelection;
    }

    @Override
    public String sql() {
        List<String> query = Lists.newArrayList(SELECT_PHRASE, columnSelection.stringify(), FROM_PHRASE,
                tableName.stringify(), WHERE_PHRASE, whereClause.stringify(), limit.stringify());

        return Joiner.on(GeneralConstants.SPACE_SEPARATOR).join(query);
    }
}
