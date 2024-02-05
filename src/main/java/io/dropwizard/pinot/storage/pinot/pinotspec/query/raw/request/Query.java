package io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request;

import io.dropwizard.pinot.query.models.Limit;
import io.dropwizard.pinot.query.models.TableNameClause;
import io.dropwizard.pinot.query.models.WhereClause;
import io.dropwizard.pinot.storage.pinot.entities.PinotTableEntity;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public abstract class Query {

    protected final QueryType type;

    public static final String SELECT_PHRASE = "select";
    public static final String FROM_PHRASE = "from";

    public static final String WHERE_PHRASE = "where";

    protected final TableNameClause tableName;
    protected final WhereClause whereClause;
    protected final Limit limit;

    protected final String timeRangeColumn;

    protected final QueryTimeRange timeRange;

    @NotNull
    protected final Class entityClass;

    protected Query(final QueryType type, final TableNameClause tableNameClause, WhereClause whereClause,
                       QueryTimeRange timeRange, Limit limit, String timeRangeColumn,
                    Class entityClass) {
        this.type = type;
        this.timeRangeColumn = timeRangeColumn;
        this.tableName = tableNameClause;
        this.whereClause = whereClause;
        this.timeRange = timeRange;
        this.limit = limit;
        this.entityClass = entityClass;
    }

    protected abstract String sql();

}
