package org.tm.archive.sms;


public class IncomingIdentityDefaultMessage extends IncomingTextMessage {

  public IncomingIdentityDefaultMessage(IncomingTextMessage base) {
    super(base, "");
  }

  @Override
  public boolean isIdentityDefault() {
    return true;
  }

}
