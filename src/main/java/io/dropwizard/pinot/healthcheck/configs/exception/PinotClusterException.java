package io.dropwizard.pinot.healthcheck.configs.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PinotClusterException extends RuntimeException {

    private PinotClusterErrorCode errorCode;

    private boolean retryable;


    @Builder
    private PinotClusterException(PinotClusterErrorCode errorCode,
                                  String message,
                                  boolean retryable) {
        super(message);
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    @Builder
    public static PinotClusterException error(PinotClusterErrorCode errorCode, String message, boolean retryable) {
        return PinotClusterException.builder()
                .errorCode(errorCode)
                .message(message)
                .retryable(retryable)
                .build();
    }
}
