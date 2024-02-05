package io.dropwizard.pinot.healthcheck.configs.exception;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class PinotDaoException extends RuntimeException {

    private final ErrorCode errorCode;

    private final Map<String, Object> context;

    private boolean retryable;


    @Builder
    private PinotDaoException(ErrorCode errorCode,
                              String message,
                              Throwable cause,
                              Map<String, Object> context,
                              boolean retryable) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
        this.retryable = retryable;
    }

    public static PinotDaoException propagate(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof PinotDaoException) { return (PinotDaoException) cause; }

        String message = cause.getLocalizedMessage();
        ErrorCode errCode = errorCode;
        return new PinotDaoException(errCode, message, cause, Maps.newHashMap(), false);
    }

    @Builder
    public static PinotDaoException error(ErrorCode errorCode, Throwable cause, Map<String, Object> context) {
        return new PinotDaoException(errorCode, null, cause, context, false);
    }

    @Builder
    public static PinotDaoException error(ErrorCode errorCode, Map<String, Object> context) {
        return new PinotDaoException(errorCode, null, null, context, false);
    }

    @Builder
    public static PinotDaoException error(ErrorCode errorCode, String message, Map<String, Object> context, boolean retriable) {
        return PinotDaoException.builder()
                .errorCode(errorCode)
                .message(message)
                .context(context)
                .retryable(retriable)
                .build();
    }

}
