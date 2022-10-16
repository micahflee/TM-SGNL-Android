package org.tm.archive.database;

import com.google.protobuf.ByteString;

import org.junit.Test;
import org.tm.archive.payments.proto.PaymentMetaData;
import org.tm.archive.util.Util;

import static org.junit.Assert.assertArrayEquals;

public final class PaymentMetaDataUtilTest {

  @Test
  public void extract_single_public_key() {
    byte[] random = Util.getSecretBytes(32);
    byte[] bytes  = PaymentMetaDataUtil.receiptPublic(PaymentMetaData.newBuilder()
                                                                     .setMobileCoinTxoIdentification(PaymentMetaData.MobileCoinTxoIdentification.newBuilder()
                                                                                                                                                .addPublicKey(ByteString.copyFrom(random))).build());
    assertArrayEquals(random, bytes);
  }
}
