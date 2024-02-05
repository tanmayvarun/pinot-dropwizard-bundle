package io.dropwizard.pinot.query.validator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private String error;
}
