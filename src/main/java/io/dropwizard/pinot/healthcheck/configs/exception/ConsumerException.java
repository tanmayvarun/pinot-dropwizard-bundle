package io.dropwizard.pinot.healthcheck.configs.exception;

public class ConsumerException extends RuntimeException {

  public ConsumerException() {
    super();
  }

  public ConsumerException(Throwable cause) {
    super(cause);
  }

  public ConsumerException(String message) {
    super(message);
  }

  public ConsumerException(String message, Throwable cause) {
    super(message, cause);
  }
}
