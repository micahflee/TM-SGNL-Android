/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tm.androidcopysdk.network.appSettings.UpdateEvent
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.selfAuthentication.SelfAuthenticatorManager
import org.signal.core.util.logging.Log
import org.signal.core.util.logging.Log.tag
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.util.views.CircularProgressMaterialButton

open class BaseActivity : SignalBaseActivity() {
  private var suspendDialog: Dialog? = null
  private val TAG = "TM" + tag(BaseActivity::class.java)

  override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume - ${System.identityHashCode(this)}")
    notifyMessageIfNeeded()

    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this)
      Log.d(TAG, "registerBus")
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
      Log.d(TAG, "unregisterBus")
    }
    endSuspendDialog()
  }

  private fun notifyMessageIfNeeded() {
    val isAlreadyRestarted = PrefManager.getBooleanPref(this, ArchivePreferenceConstants.PREF_KEY_MAIN_ACTIVITY_RESTART, false)
    if (!isAlreadyRestarted) {
      PrefManager.setBooleanPref(this, ArchivePreferenceConstants.PREF_KEY_MAIN_ACTIVITY_RESTART, true)
      val handler = Handler(Looper.getMainLooper())
      handler.postDelayed({ notifyMessage() }, 4000)
    }
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  @Synchronized
  fun notifyMessage() {
    synchronized(ApplicationDependencies.getIncomingMessageObserver()) {
      (ApplicationDependencies.getIncomingMessageObserver() as Object).notifyAll()
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: UpdateEvent?) {
    if (event == null) {
      return
    }
    Log.d(TAG, "UpdateEvent -> onEvent: " + event.type)
    if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      showSuspendDialog()
      SelfAuthenticatorManager.removeLinkedDevices(this)
      SelfAuthenticatorManager.hideDialogAndShowSuspendDialog(SelfAuthenticatorManager.SuspendUIAction.SHOULD_SHOW_SUSPEND_DIALOG)
    } else if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      endSuspendDialog()
    }
  }

  private fun endSuspendDialog() {
    suspendDialog?.dismiss()
    suspendDialog = null
  }

  private fun showSuspendDialog() {
    if (suspendDialog == null) {
      suspendDialog = Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen) // Fullscreen theme
      suspendDialog!!.apply {
        setContentView(R.layout.fragment_registration_enter_phone_number)
        val layout = findViewById<ConstraintLayout>(R.id.constraint_layout)
        layout.setBackgroundColor(resources.getColor(R.color.white))
        val button = findViewById<CircularProgressMaterialButton>(R.id.registerButton)
        button.visibility = View.GONE
        setCancelable(false)
        show()
      }
    }
  }

}