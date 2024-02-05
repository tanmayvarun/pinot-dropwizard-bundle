package io.dropwizard.pinot.query.models.filters;

import com.google.common.base.Joiner;
import io.dropwizard.pinot.constants.GeneralConstants;
import io.dropwizard.pinot.models.domainparams.DomainParam;

public class NotInFilter extends Filter {

    private final InFilter inFilter;

    protected NotInFilter(DomainParam domainParam, InFilter inFilter) {
        super(FilterType.NOT_IN, domainParam);
        this.inFilter = inFilter;
    }

    @Override
    public String toString() {
        return String.format("%s not in ('%s')", columnName(), Joiner.on(GeneralConstants.COMMA_DELIMITER).join(inFilter.getOperands()));
    }
}
