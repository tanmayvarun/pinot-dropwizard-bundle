package io.dropwizard.pinot.query.models.filters;

public class LessThanFilter extends Filter {

    private String value;

    protected LessThanFilter(String columnName, String value) {
        super(FilterType.LESS_THAN, columnName);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s < %s", columnName, value);
    }
}
