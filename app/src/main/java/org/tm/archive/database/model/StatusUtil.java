package org.tm.archive.database.model;

import org.tm.archive.database.MmsSmsColumns;
import org.tm.archive.database.SmsDatabase;

final class StatusUtil {
  private StatusUtil() {}

  static boolean isDelivered(long deliveryStatus, int deliveryReceiptCount) {
    return (deliveryStatus >= SmsDatabase.Status.STATUS_COMPLETE &&
            deliveryStatus < SmsDatabase.Status.STATUS_PENDING)  || deliveryReceiptCount > 0;
  }

  static boolean isPending(long type) {
    return MmsSmsColumns.Types.isPendingMessageType(type) &&
           !MmsSmsColumns.Types.isIdentityVerified(type)  &&
           !MmsSmsColumns.Types.isIdentityDefault(type);
  }

  static boolean isFailed(long type, long deliveryStatus) {
    return MmsSmsColumns.Types.isFailedMessageType(type)            ||
           MmsSmsColumns.Types.isPendingSecureSmsFallbackType(type) ||
           deliveryStatus >= SmsDatabase.Status.STATUS_FAILED;
  }

  static boolean isVerificationStatusChange(long type) {
    return SmsDatabase.Types.isIdentityDefault(type) || SmsDatabase.Types.isIdentityVerified(type);
  }
}
