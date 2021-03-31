package org.tm.archive.transport;

public class RetryLaterException extends Exception {
  public RetryLaterException() {}

  public RetryLaterException(Exception e) {
    super(e);
  }
}
