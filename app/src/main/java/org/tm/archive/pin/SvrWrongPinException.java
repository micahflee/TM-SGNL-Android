package org.tm.archive.pin;

public final class SvrWrongPinException extends Exception {

  private final int triesRemaining;

  public SvrWrongPinException(int triesRemaining){
    this.triesRemaining = triesRemaining;
  }

  public int getTriesRemaining() {
    return triesRemaining;
  }
}
