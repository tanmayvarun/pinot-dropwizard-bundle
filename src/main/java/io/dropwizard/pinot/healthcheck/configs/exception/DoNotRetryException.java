package io.dropwizard.pinot.healthcheck.configs.exception;

public class DoNotRetryException extends RuntimeException {

  public DoNotRetryException() {
    super();
  }

  public DoNotRetryException(Throwable cause) {
    super(cause);
  }

  public DoNotRetryException(String message) {
    super(message);
  }

  public DoNotRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
