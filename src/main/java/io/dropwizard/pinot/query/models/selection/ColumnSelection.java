package io.dropwizard.pinot.query.models.selection;

import io.dropwizard.pinot.query.models.SqlQueryParam;
import io.dropwizard.pinot.query.models.SqlQueryParamType;
import lombok.Getter;

@Getter
public abstract class ColumnSelection implements SqlQueryParam {

    protected final SelectionType selectionType;

    protected ColumnSelection(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public SqlQueryParamType getType() {
        return SqlQueryParamType.COLUMN_LIST;
    }
}
