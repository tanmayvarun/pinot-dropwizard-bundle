package io.dropwizard.pinot.query.models.filters;

import io.dropwizard.pinot.models.domainparams.DomainParam;

public class LessThanFilter extends Filter {

    private String value;

    protected LessThanFilter(DomainParam domainParam, String value) {
        super(FilterType.LESS_THAN, domainParam);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s < %s", columnName(), value);
    }
}
