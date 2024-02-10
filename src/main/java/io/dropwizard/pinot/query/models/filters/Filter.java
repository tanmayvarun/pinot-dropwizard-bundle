package io.dropwizard.pinot.query.models.filters;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public abstract class Filter {

    @NotNull
    protected final FilterType type;

    @NotBlank
    protected final String columnName;

    protected Filter(@NotNull FilterType type, @NotBlank String columnName) {
        this.type = type;
        this.columnName = columnName;
    }

    public abstract String toString();

}
