package io.dropwizard.pinot.query.models.filters;

public class NotEqualsFilter extends Filter {

    private final EqualsFilter equalsFilter;

    protected NotEqualsFilter(String columnName, EqualsFilter equalsFilter) {
        super(FilterType.NOT_EQUALS, columnName);
        this.equalsFilter = equalsFilter;
    }

    @Override
    public String toString() {
        return String.format("%s != '%s'", columnName, equalsFilter.getValue());
    }
}
