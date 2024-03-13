/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.registration.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.google.firebase.FirebaseApp
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.model.resource.ResourceStatus
import com.tm.androidcopysdk.network.appSettings.UpdateEvent
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.isMDM
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.startMDMAuthenticator
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.utils.ApplicationInterface
import org.archiver.FCMConnector.Companion.initOfficialSignalFirebaseAccount
import org.archiver.FCMConnector.Companion.initTeleMessageSignalFirebaseAccount
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.intune.IntuneAuthManager
import org.intune.IntuneAuthManager.continueIntuneAuthentication
import org.intune.IntuneAuthManager.showDialog
import org.intune.MDMDialogListener
import org.selfAuthentication.SelfAuthenticationDialogBuilder
import org.selfAuthentication.SelfAuthenticatorManager.initAuthenticator
import org.selfAuthentication.SelfAuthenticatorManager.startAuthentication
import org.signal.core.util.concurrent.SimpleTask
import org.signal.core.util.logging.Log.d
import org.signal.core.util.logging.Log.i
import org.signal.core.util.logging.Log.w
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.registration.viewmodel.RegistrationViewModel
import org.tm.archive.util.FeatureFlags
import org.tm.archive.util.navigation.safeNavigate
import java.io.IOException

const val TAG = "TMEnterSmsCodeFragment"

class TMEnterSmsCodeFragment : EnterSmsCodeFragment(), IAuthenticationStatus, IMDMAuthenticator
{
  private var viewModel: RegistrationViewModel? = null
  private lateinit var mobileNumber: String
  private lateinit var startAuthButton: Button


  override fun handleSuccessfulVerify() {
    val authStatus = PrefManager.getIntPref(requireContext(),
      IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
    if (CommonUtils.isActivatedUser(requireContext())) {
      continueSignalFlow()
    } else {
      if (isMDM(requireContext()) && authStatus == IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal) { // mdm auth skip this fragment and work on EnterSmsCodeFragment
        startMdm()
      } else {
        startAutoAuthentication(mobileNumber) //start self auth
      }
    }
  }

  private fun continueSignalFlow() {
    SimpleTask.run<Any?>({
      val startTime = System.currentTimeMillis()
      try {
        FeatureFlags.refreshSync()
        i(TAG, "Took " + (System.currentTimeMillis() - startTime) + " ms to get feature flags.")
      } catch (e: IOException) {
        w(TAG, "Failed to refresh flags after " + (System.currentTimeMillis() - startTime) + " ms.", e)
      }
      null
    }) { displaySuccess { findNavController(requireView()).safeNavigate(TMEnterSmsCodeFragmentDirections.actionSuccessfulRegistration()) } }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    startAuthButton = view.findViewById(R.id.buttonTelemessageAuth)
    viewModel = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]
    mobileNumber = viewModel!!.number.e164Number
    startAuthButton.setOnClickListener {
      startAutoAuthentication(mobileNumber) //start self auth
    }
  }


  private fun startMdm() {
    d(TAG, "startMdm")
    initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
    startMDMAuthenticator(requireActivity(), mobileNumber, BuildConfig.signal_teleMessage_version, this)
  }

  override fun failureMDMAuth(reason: String) {
    val onCancel = "onCancel"
    val server = "server"
    d(TAG, "failureMDMAuth, reason: $reason")
    if (reason == onCancel) {
      showDialog(requireActivity(), object : MDMDialogListener {
        override fun startIntuneAgain() {
          startMdm()
        }
      })
    } //update app that intune signed failed: two cases. 1. try intune auth again  2. move to self auth
    else if (reason.contains(server) || reason.contains("Authentication failed") /*|| reason.contains("managerID")*/) { //try intune auth again
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      requireActivity().runOnUiThread { continueSignalFlow() }
    } else { //this case should pass to self-auth
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      requireActivity().runOnUiThread { continueSignalFlow() }
    }
  }

  override fun successMDMAuth() {
    d(TAG, "successMDMAuth")
    startIntuneAutoAuthentication(mobileNumber)
  }

  private fun startIntuneAutoAuthentication(e164number: String) {
    d(TAG, "startAutoAuthentication")
    initAuthenticator(e164number)
    continueIntuneAuthentication((requireContext().applicationContext as ApplicationInterface), this)
  }

  private fun startAutoAuthentication(e164number: String) {
    i(TAG, "startAutoAuthentication")
    initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
    i(TAG, "current FCM: " + FirebaseApp.getInstance().options.projectId)
    initAuthenticator(e164number)
    startAuthentication((requireContext().applicationContext as ApplicationInterface), this)
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: UpdateEvent?) {
    if (event == null) {
      return
    }
    d(TAG, "UpdateEvent -> onEvent: " + event.type)
    if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      CommonUtils.setActivatedUser(requireContext(), true)
      continueSignalFlow()
    } else if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      CommonUtils.setActivatedUser(requireContext(), false)
      val dialog = SelfAuthenticationDialogBuilder()
      dialog.logCallback.observe(this) { result ->
        when (result) {
          ResourceStatus.Success -> displaySuccess()
          ResourceStatus.Error -> displayFailure()
          ResourceStatus.Loading -> displayProgress()
        }
      }
      dialog.doSendLogsClicked(requireActivity())
      startAuthButton.visibility = View.VISIBLE
      displayFailure()
    }
    i(TAG, "onMessageEvent -> 1 current FCM: " + FirebaseApp.getInstance().options.projectId)
    d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ")
    initOfficialSignalFirebaseAccount(requireContext())
    i(TAG, "onMessageEvent -> 2 current FCM: " + FirebaseApp.getInstance().options.projectId)
  }

  override fun authenticationProcessMessage(message: String) {
    d(TAG, "authenticationProcessMessage = $message")
    if (message.isNotEmpty()) {
//      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
      EventBus.getDefault().post(UpdateEvent(UpdateEvent.EVENTS_TYPE.suspension))
    }
  }
}