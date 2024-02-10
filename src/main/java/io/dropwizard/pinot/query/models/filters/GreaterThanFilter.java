package io.dropwizard.pinot.query.models.filters;

public class GreaterThanFilter extends Filter {

    private String value;

    protected GreaterThanFilter(String columnName, String value) {
        super(FilterType.GREATER_THAN, columnName);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s > %s", columnName, value);
    }
}
