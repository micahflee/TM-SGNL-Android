package org.tm.archive.registration.secondary

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.tm.archive.crypto.IdentityKeyUtil
import org.tm.archive.database.loaders.DeviceListLoader
import org.tm.archive.devicelist.DeviceNameProtos
import java.nio.charset.Charset

class DeviceNameCipherTest {

  @Test
  fun encryptDeviceName() {
    val deviceName = "xXxCoolDeviceNamexXx"
    val identityKeyPair = IdentityKeyUtil.generateIdentityKeyPair()

    val encryptedDeviceName = DeviceNameCipher.encryptDeviceName(deviceName.toByteArray(Charset.forName("UTF-8")), identityKeyPair)

    val plaintext = DeviceListLoader.decryptName(DeviceNameProtos.DeviceName.parseFrom(encryptedDeviceName), identityKeyPair)

    assertThat(String(plaintext, Charset.forName("UTF-8")), `is`(deviceName))
  }
}
