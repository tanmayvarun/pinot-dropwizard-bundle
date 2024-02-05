package io.dropwizard.pinot.utils;

import io.dropwizard.pinot.healthcheck.configs.exception.DoNotRetryException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RetryCommand<T> {
  private int retryCounter;
  private final int maxRetries;
  private final int waitTimeInMs;
  private final String commandName;

  public RetryCommand(int maxRetries, int waitTimeInMs, String commandName) {
    this.maxRetries = maxRetries;
    this.waitTimeInMs = waitTimeInMs;
    this.commandName = commandName;
  }

  // Takes a function and executes it, if fails, passes the function to the retry command
  public T run(Supplier<T> function) {
    try {
      return function.get();
    } catch (DoNotRetryException dnre){
      log.error("DoNotRetryException: {}", dnre);
      throw dnre;
    } catch (Exception e) {
      return retry(function);
    }
  }

  public int getRetryCounter() {
    return retryCounter;
  }

  private T retry(Supplier<T> function) throws RuntimeException {
    log.error("FAILED - Command: "+commandName+" failed, will be retried " + maxRetries + " times after waiting for " +waitTimeInMs + "ms");
    retryCounter = 0;
    while (retryCounter < maxRetries) {

      try {
        Thread.sleep(waitTimeInMs);
        return function.get();
      } catch (InterruptedException ie){
        Thread.currentThread().interrupt();
        throw new RuntimeException("Command: "+commandName+" failed", ie);
      } catch (Exception ex) {
        retryCounter++;
        log.error("FAILED - Command: "+commandName+" failed on retry " + retryCounter + " of " + maxRetries + " error: " + ex );
        if (retryCounter >= maxRetries) {
          log.error("Max retries exceeded.");
          break;
        }
      }
    }
    throw new RuntimeException("Command: "+commandName+" failed on all of " + maxRetries + " retries");
  }

}