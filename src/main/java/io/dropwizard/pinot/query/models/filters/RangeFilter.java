package io.dropwizard.pinot.query.models.filters;


import lombok.Builder;
import lombok.Getter;

@Getter
public class RangeFilter<T> extends Filter {

    private T lowerLimit;
    private T upperLimit;
    private boolean includeLower = false;
    private boolean includeUpper = false;

    @Builder
    RangeFilter(final String columnName, T lowerLimit, T upperLimit, boolean includeLower, boolean includeUpper) {
        super(FilterType.RANGE, columnName);
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    //todo: fix to string to proper where clause
    @Override
    public String toString() {
        return String.format("%s_%s_%s", columnName, lowerLimit, upperLimit);
    }
}
