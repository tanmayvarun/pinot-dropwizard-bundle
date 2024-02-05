package io.dropwizard.pinot.healthcheck.configs.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryProcessingException extends RuntimeException {
    
    private String query;
    
    private QueryErrorCode errorCode;
    
    private boolean retryable;

    
    @Builder
    private QueryProcessingException(QueryErrorCode errorCode,
                                     String query,
                                     String message,
                                     boolean retryable) {
        super(message);
        this.query = query;
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    @Builder
    public static QueryProcessingException error(QueryErrorCode errorCode, String query, String message, boolean retryable) {
        return new QueryProcessingException(errorCode, query, message, retryable);
    }

    @Builder
    public static QueryProcessingException error(QueryErrorCode errorCode, String message, boolean retryable) {
        return QueryProcessingException.builder()
                .errorCode(errorCode)
                .message(message)
                .retryable(retryable)
                .build();
    }
}
