package io.dropwizard.pinot.healthcheck.configs.exception;

import com.google.common.collect.Maps;
import lombok.Builder;

import java.util.Map;

public class IngestionException extends RuntimeException {

    private final ErrorCode errorCode;

    private final Map<String, Object> context;

    private boolean retryable;


    @Builder
    private IngestionException(ErrorCode errorCode,
                               String message,
                               Throwable cause,
                               Map<String, Object> context,
                               boolean retryable) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
        this.retryable = retryable;
    }

    public static IngestionException propagate(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof IngestionException) { return (IngestionException) cause; }

        String message = cause.getLocalizedMessage();
        ErrorCode errCode = errorCode;
        return new IngestionException(errCode, message, cause, Maps.newHashMap(), false);
    }

    @Builder
    public static IngestionException error(ErrorCode errorCode, Map<String, Object> context) {
        return new IngestionException(errorCode, null, null, context, false);
    }

    @Builder
    public static IngestionException error(ErrorCode errorCode, String message, Map<String, Object> context, boolean retriable) {
        return IngestionException.builder()
                .errorCode(errorCode)
                .message(message)
                .context(context)
                .retryable(retriable)
                .build();
    }

}
