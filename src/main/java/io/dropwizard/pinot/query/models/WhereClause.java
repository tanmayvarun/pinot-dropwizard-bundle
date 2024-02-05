package io.dropwizard.pinot.query.models;

import io.dropwizard.pinot.constants.GeneralConstants;
import io.dropwizard.pinot.query.models.filters.Filter;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Getter
public class WhereClause implements SqlQueryParam {

    private final List<Filter> filters;

    @Builder
    public WhereClause(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public SqlQueryParamType getType() {
        return SqlQueryParamType.WHERE_CLAUSE;
    }

    @Override
    public String stringify() {

        if (CollectionUtils.isEmpty(filters)) {
            return GeneralConstants.EMPTY_STRING;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(filters.get(0).toString());

        for (int i = 1; i < filters.size(); i++) {
            builder.append(" and ").append(filters.get(i).toString());
        }

        return builder.toString();
    }
}
