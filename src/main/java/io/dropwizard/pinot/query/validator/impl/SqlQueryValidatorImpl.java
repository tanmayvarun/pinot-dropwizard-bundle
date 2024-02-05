package io.dropwizard.pinot.query.validator.impl;

import io.dropwizard.pinot.query.validator.SqlQueryValidator;
import io.dropwizard.pinot.query.validator.ValidationResult;
import io.dropwizard.pinot.repository.pinot.PinotDao;
import io.dropwizard.pinot.storage.pinot.pinotspec.query.raw.request.RawQuery;

public class SqlQueryValidatorImpl implements SqlQueryValidator {

    private PinotDao pinotDao;

    @Override
    public ValidationResult syntaxCheck(String sqlQuery) {
        return null;
    }

    @Override
    public ValidationResult shadowRun(String sqlQuery) {
        try {
            pinotDao.query(RawQuery.builder().sql(sqlQuery).build());
        } catch (Exception ex) {
            return ValidationResult.builder()
                    .valid(false)
                    .build();
        }

        return ValidationResult.builder()
                .valid(true)
                .build();
    }
}
