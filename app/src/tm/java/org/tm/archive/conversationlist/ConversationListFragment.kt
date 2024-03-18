/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.conversationlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.network.appSettings.UpdateEvent
import com.tm.androidcopysdk.network.appSettings.WorkerIntentService
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.isMDM
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.startMDMAuthenticator
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.utils.ApplicationInterface
import org.archiver.ArchiveLogger
import org.archiver.ArchivePreferenceConstants
import org.archiver.ArchiveUtil.Companion.getPhoneNumberInTestMode
import org.archiver.FCMConnector.Companion.initOfficialSignalFirebaseAccount
import org.archiver.FCMConnector.Companion.initTeleMessageSignalFirebaseAccount
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.intune.IntuneAuthManager
import org.intune.IntuneAuthManager.continueIntuneAuthentication
import org.intune.IntuneAuthManager.showDialog
import org.intune.MDMDialogListener
import org.selfAuthentication.SelfAuthenticatorManager.initAuthenticator
import org.selfAuthentication.SelfAuthenticatorManager.startAuthenticationProcess
import org.signal.core.util.logging.Log
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.util.TextSecurePreferences

const val TAG = "TM ConversationListFragment"
open class ConversationListFragment : SignalConversationListFragment(), IAuthenticationStatus, IMDMAuthenticator /*TM_SA*/ {
  private var mAuthenticationProgressAlertDialogBuilder: AlertDialog.Builder? = null
  private var mAuthenticationProgressAlertDialog: AlertDialog? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(TAG, "onViewCreated")


    //**TM_SA**//Start
    if (CommonUtils.isActivatedUser(requireContext())) {
      WorkerIntentService.startJobIntentService(requireContext(), true)
    } else {
      Log.d(TAG, "BuildConfig.APPLICATION_ID: " + BuildConfig.APPLICATION_ID)
      val authStatus = PrefManager.getIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String,
        IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      Log.d(TAG,
        "onCreate -> authStatus = $authStatus. (0-signed, 1 -should intune auth, 2-self auth)")
      if (isMDM(requireContext()) && authStatus == IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal) { //if intune managed device, start MDM auth
        startIntuneAuth()
      } else { // else self auth
        startSelfAuth()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume")
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this)
    }
  }
  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy")
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  private fun startIntuneAuth() {
    Log.d(TAG, "startIntuneAuth")
    startMdm()
  }

  fun startSelfAuth() {
    if (!CommonUtils.isActivatedUser(requireContext())) {
      initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
      createAndShowAuthProgressDialog(requireContext(), true)
      startAuthenticationProcess(requireContext(),
        getPhoneNumberInTestMode(requireContext()), context?.applicationContext as ApplicationInterface,
        this)
    }
  }


  fun startMdm() {
    initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
    startMDMAuthenticator(requireActivity(),
      getPhoneNumberInTestMode(requireContext()), BuildConfig.signal_teleMessage_version, this)
  }

  override fun failureMDMAuth(reason: String) {
    val onCancel = "onCancel"
    Log.d("ConversationListFragment", "failureMDMAuth, reason: $reason")
    //MDMAuthenticator.INSTANCE.signOutUser(requireActivity());
    if (reason == onCancel) {
      showDialog(requireActivity(), object : MDMDialogListener {
        override fun startIntuneAgain() {
          startMdm()
        }
      })
      //update app that intune signed failed: two cases. 1. try intune auth again  2. move to self auth
    } else if (reason.contains("server") || reason.contains("Authentication failed") /*|| reason.contains("managerID")*/) { //try intune auth again
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      Log.d("ConversationListFragment", "status auth is 1")
    } else { //this case should pass to self-auth
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      Log.d("ConversationListFragment", "status auth is 2")
      startSelfAuth()
    }
  }

  override fun successMDMAuth() {
    Log.d("ConversationListFragment", "successMDMAuth")
    val e164number = PrefManager.getStringPref(requireContext(), ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER)
    startIntuneAutoAuthentication(e164number)
  }

  /**
   * intune
   * @param e164number
   */
  private fun startIntuneAutoAuthentication(e164number: String) {
    Log.d(TAG, "startAutoAuthentication")
    initAuthenticator(e164number)
    continueIntuneAuthentication(context?.applicationContext as ApplicationInterface,this)
  }


  //**TM_SA**//Start
  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: UpdateEvent?) {
    if (event == null) {
      return
    }
    Log.d("ConversationListFragment", "UpdateEvent -> onEvent: " + event.type)
    if (event.type == UpdateEvent.EVENTS_TYPE.authProcess) {
      CommonUtils.setActivatedUser(requireContext(), false)
      startSelfAuth()
    }
    if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      CommonUtils.setActivatedUser(requireContext(), false)
    } else if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      CommonUtils.setActivatedUser(requireContext(), true)
      CommonUtils.startBackupService(context)
      ArchiveLogger.sendArchiveLog("Backup service started")
//      endAuthDialog()
    }
    if (event.type != UpdateEvent.EVENTS_TYPE.authProcess) {
      endAuthDialog()
//      SelfAuthenticatorManager.setProgressDialogVisibility(false)
      WorkerIntentService.startJobIntentService(requireContext())
      Log.d("ConversationListFragment", "SelfAuthenticator -> initOfficialSignalFirebaseAccount!!! ")
      initOfficialSignalFirebaseAccount(requireContext())
    }
  }

  override fun authenticationProcessMessage(message: String) {
    Log.d("ConversationListFragment", "SelfAuthenticatorProcess -> authenticationProcessMessage = $message")
    if (!message.isEmpty()) {
//      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
      EventBus.getDefault().post(UpdateEvent(UpdateEvent.EVENTS_TYPE.suspension))
    }
  }


  private fun createAndShowAuthProgressDialog(context: Context, isCanCancel: Boolean) {
    Log.d("SelfAuthenticatorManager","createAndShowAuthProgressDialog")
    if (mAuthenticationProgressAlertDialogBuilder == null) {
      mAuthenticationProgressAlertDialogBuilder = AlertDialog.Builder(context, R.style.AuthTmProgressDialog)
      val view = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout_with_background, null)
      mAuthenticationProgressAlertDialogBuilder!!.setView(view)
      mAuthenticationProgressAlertDialogBuilder!!.setCancelable(isCanCancel)
      mAuthenticationProgressAlertDialog = mAuthenticationProgressAlertDialogBuilder!!.create()
    }

    if (mAuthenticationProgressAlertDialog != null && !mAuthenticationProgressAlertDialog!!.isShowing) {
      mAuthenticationProgressAlertDialog!!.show()
    }
  }

  private fun endAuthDialog() {
    Log.d("SelfAuthenticatorManager","endAuthDialog")
    if (mAuthenticationProgressAlertDialog != null) {
      mAuthenticationProgressAlertDialog!!.dismiss()
      mAuthenticationProgressAlertDialog = null
    }
  }
}