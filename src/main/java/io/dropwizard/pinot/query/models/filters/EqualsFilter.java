package io.dropwizard.pinot.query.models.filters;

import io.dropwizard.pinot.models.domainparams.DomainParam;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EqualsFilter extends Filter {

    private final String value;

    @Builder
    public EqualsFilter(DomainParam domainParam, String value) {
        super(FilterType.EQUALS, domainParam);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s='%s'", columnName(), value);
    }
}
