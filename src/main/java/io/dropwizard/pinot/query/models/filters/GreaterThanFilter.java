package io.dropwizard.pinot.query.models.filters;

import io.dropwizard.pinot.models.domainparams.DomainParam;

public class GreaterThanFilter extends Filter {

    private String value;

    protected GreaterThanFilter(DomainParam domainParam, String value) {
        super(FilterType.GREATER_THAN, domainParam);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s > %s", columnName(), value);
    }
}
