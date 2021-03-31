package org.tm.archive.sms;


import org.tm.archive.recipients.Recipient;

public class OutgoingIdentityVerifiedMessage extends OutgoingTextMessage {

  public OutgoingIdentityVerifiedMessage(Recipient recipient) {
    this(recipient, "");
  }

  private OutgoingIdentityVerifiedMessage(Recipient recipient, String body) {
    super(recipient, body, -1);
  }

  @Override
  public boolean isIdentityVerified() {
    return true;
  }

  @Override
  public OutgoingTextMessage withBody(String body) {
    return new OutgoingIdentityVerifiedMessage(getRecipient(), body);
  }
}
