package io.dropwizard.pinot.query.models.filters;

import com.google.common.base.Joiner;
import io.dropwizard.pinot.constants.GeneralConstants;

public class NotInFilter extends Filter {

    private final InFilter inFilter;

    protected NotInFilter(String columnName, InFilter inFilter) {
        super(FilterType.NOT_IN, columnName);
        this.inFilter = inFilter;
    }

    @Override
    public String toString() {
        return String.format("%s not in ('%s')", columnName, Joiner.on(GeneralConstants.COMMA_DELIMITER).join(inFilter.getOperands()));
    }
}
