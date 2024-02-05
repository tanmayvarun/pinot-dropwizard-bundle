package io.dropwizard.pinot.healthcheck.configs.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericError {
    private String code;
    private String message;
    private Map<String, Object> context;
    private boolean retryable;
}
