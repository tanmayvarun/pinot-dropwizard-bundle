package io.dropwizard.pinot.query.models.filters;

import io.dropwizard.pinot.models.domainparams.DomainParam;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;

@Getter
public abstract class Filter {

    @NotNull
    protected final FilterType type;

    protected final DomainParam domainParam;

    protected Filter(FilterType type, DomainParam domainParam) {
        this.type = type;
        this.domainParam = domainParam;
    }

    public abstract String toString();

    @SneakyThrows
    public String columnName() {
        return this.domainParam.databaseColumnName();
    }
}
