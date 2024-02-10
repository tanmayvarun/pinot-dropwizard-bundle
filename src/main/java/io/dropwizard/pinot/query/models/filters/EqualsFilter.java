package io.dropwizard.pinot.query.models.filters;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EqualsFilter extends Filter {

    private final String value;

    @Builder
    public EqualsFilter(String databaseColumnName, String value) {
        super(FilterType.EQUALS, databaseColumnName);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s='%s'", columnName, value);
    }
}
