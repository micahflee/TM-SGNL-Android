/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.selfAuthentication

import android.content.Context
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.signal.core.util.logging.Log
import org.signal.core.util.logging.Log.tag
import org.tm.archive.BaseActivity
import org.tm.archive.database.loaders.DeviceListLoader
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.devicelist.Device

val TAG = tag(DevicesDisconnector::class.java)
class DevicesDisconnector(val activity : BaseActivity) : LoaderManager.LoaderCallbacks<MutableList<Device>> {
  val accountManager = ApplicationDependencies.getSignalServiceAccountManager()

  init {
    val loaderManager = LoaderManager.getInstance(activity)
    loaderManager.initLoader(0, null, this)
  }
  override fun onCreateLoader(id: Int, args: Bundle?): Loader<MutableList<Device>> {
    Log.d(TAG, "onCreateLoader")
    return DeviceListLoader(activity, accountManager)
  }

  override fun onLoaderReset(loader: Loader<MutableList<Device>>) {
//    TODO("Not yet implemented")
  }

  override fun onLoadFinished(loader: Loader<MutableList<Device>>, data: MutableList<Device>?) {
    if (data == null) {
      Log.d(TAG, "no devices to remove")
      return
    }
    CoroutineScope(Dispatchers.IO).launch {
      data.forEach { device ->
        accountManager.removeDevice(device.id)
        Log.d(TAG, "device.id: ${device.id} removed")
      }
    }
  }

}