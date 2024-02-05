package io.dropwizard.pinot.query.models.filters;

import io.dropwizard.pinot.models.domainparams.DomainParam;

public class NotEqualsFilter extends Filter {

    private final EqualsFilter equalsFilter;

    protected NotEqualsFilter(DomainParam domainParam, EqualsFilter equalsFilter) {
        super(FilterType.NOT_EQUALS, domainParam);
        this.equalsFilter = equalsFilter;
    }

    @Override
    public String toString() {
        return String.format("%s != '%s'", columnName(), equalsFilter.getValue());
    }
}
