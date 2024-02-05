package io.dropwizard.pinot.query.models;

import javax.validation.constraints.NotBlank;

public interface SqlQueryParam {

    SqlQueryParamType getType();

    @NotBlank
    abstract String stringify();

}
