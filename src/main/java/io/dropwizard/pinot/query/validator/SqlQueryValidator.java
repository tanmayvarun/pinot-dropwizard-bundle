package io.dropwizard.pinot.query.validator;

public interface SqlQueryValidator {

    ValidationResult syntaxCheck(String sqlQuery);

    ValidationResult shadowRun(String sqlQuery);
}
