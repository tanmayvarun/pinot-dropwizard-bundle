package io.dropwizard.pinot.healthcheck.configs.exception.responsemapper;

import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;
import io.dropwizard.pinot.healthcheck.configs.exception.GenericError;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotClusterException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Throwable exception = null;

        if (e instanceof PinotClusterException) {
            return Response.status(((PinotClusterException) exception).getErrorCode().getHttpResponseCode())
                    .entity(GenericError.builder()
                            .code(((PinotClusterException) exception).getErrorCode().getCode())
                            .retryable(((PinotClusterException) exception).isRetryable())
                            .build())
                    .build();
        }

        if (e instanceof PinotDaoException) {
            return Response.status(((PinotDaoException) exception).getErrorCode().getHttpstatuscode())
                    .entity(GenericError.builder()
                            .code(((PinotClusterException) exception).getErrorCode().getCode())
                            .retryable(((PinotClusterException) exception).isRetryable())
                            .build())
                    .build();
        }

        return Response.serverError().build();
    }
}
