package org.tm.archive.database.model;

import org.tm.archive.database.MessageTable;
import org.tm.archive.database.MessageTypes;

final class StatusUtil {
  private StatusUtil() {}

  static boolean isDelivered(long deliveryStatus, boolean hasDeliveryReceipt) {
    return (deliveryStatus >= MessageTable.Status.STATUS_COMPLETE &&
            deliveryStatus < MessageTable.Status.STATUS_PENDING) || hasDeliveryReceipt;
  }

  static boolean isPending(long type) {
    return MessageTypes.isPendingMessageType(type) &&
           !MessageTypes.isIdentityVerified(type) &&
           !MessageTypes.isIdentityDefault(type);
  }

  static boolean isFailed(long type, long deliveryStatus) {
    return MessageTypes.isFailedMessageType(type) ||
           MessageTypes.isPendingSecureSmsFallbackType(type) ||
           deliveryStatus >= MessageTable.Status.STATUS_FAILED;
  }

  static boolean isVerificationStatusChange(long type) {
    return MessageTypes.isIdentityDefault(type) || MessageTypes.isIdentityVerified(type);
  }
}
