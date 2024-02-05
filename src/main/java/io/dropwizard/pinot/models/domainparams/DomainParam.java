package io.dropwizard.pinot.models.domainparams;

import io.dropwizard.pinot.storage.pinot.pinotspec.schema.PinotSupportedColumnTypeV1;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//todo: add start time validation that column name / key / domain name cannot be null/empty for any subclass.
@Setter
@Getter
@Slf4j
public abstract class DomainParam<T> {

    protected T value;

    protected DomainParam(T value) {
        this.value = value;
    }

    protected DomainParam() {

    }

    @NotBlank
    public abstract String getKey();

    @NotBlank
    public abstract String getDomainName();

    //todo: validation at service startup that column name is present in associated schema
    //very imp
    @NotBlank
    public abstract String databaseColumnName();

    @NotNull
    public abstract PinotSupportedColumnTypeV1 type();

    public T getValue() {
        return this.value;
    }

}
