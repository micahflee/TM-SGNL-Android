package org.tm.archive.transport;

public class UndeliverableMessageException extends Exception {
  public UndeliverableMessageException() {
  }

  public UndeliverableMessageException(String detailMessage) {
    super(detailMessage);
  }

  public UndeliverableMessageException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public UndeliverableMessageException(Throwable throwable) {
    super(throwable);
  }
}
