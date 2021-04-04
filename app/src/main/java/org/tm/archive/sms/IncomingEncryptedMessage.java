package org.tm.archive.sms;

public class IncomingEncryptedMessage extends IncomingTextMessage {

  public IncomingEncryptedMessage(IncomingTextMessage base, String newBody) {
    super(base, newBody);
  }

  @Override
  public boolean isSecureMessage() {
    return true;
  }
}
