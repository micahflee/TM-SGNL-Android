/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.registration.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.BackupService
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.model.resource.ResourceStatus
import com.tm.androidcopysdk.network.appSettings.UpdateEvent
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.isMDM
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.startMDMAuthenticator
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.utils.ApplicationInterface
import org.archiver.ArchiveConstants
import org.archiver.ArchiveLogger.Companion.sendArchiveLog
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
import org.selfAuthentication.SelfAuthenticationDialogBuilder
import org.selfAuthentication.SelfAuthenticatorManager.initAuthenticator
import org.selfAuthentication.SelfAuthenticatorManager.startAuthentication
import org.signal.core.util.logging.Log
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.util.Dialogs
import org.tm.archive.util.PlayServicesUtil
import org.tm.archive.util.PlayServicesUtil.PlayServicesStatus


const val TAG = "TMEnterPhoneNumberFragment"
class TMEnterPhoneNumberFragment : SignalEnterPhoneNumberFragment(), IAuthenticationStatus, IMDMAuthenticator {
  private var progressBarCustomView: View? = null
  private var progressBarShown = false
  private var mIsLoginAuthenticationInProgress = false
  private var constraintLayout: ConstraintLayout? = null
  private var mobileNumber: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    constraintLayout = view.findViewById(R.id.constraint_layout)
    initProgressBar()
    constraintLayout?.addView(progressBarCustomView)

  }

  override fun onDestroy() {
    super.onDestroy()
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  private fun initProgressBar() {
    progressBarShown = false
    progressBarCustomView = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout_with_background, null, false)
    val backgroundLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    progressBarCustomView?.layoutParams = backgroundLayoutParams
    progressBarCustomView?.visibility = View.GONE
  }


  private fun hideProgressBar() {
    requireActivity().runOnUiThread {
      progressBarCustomView!!.visibility = View.GONE
      progressBarShown = false
      Log.d(TAG, "Registration progress hidden")
    }
  }

  private fun showProgressBar() {
    requireActivity().runOnUiThread {
      progressBarCustomView!!.visibility = View.VISIBLE
      progressBarShown = true
      Log.d(TAG, "Registration progress shown")
    }
  }


  override fun handleRegister(context: Context) {
    if (viewModel.number.countryCode == 0) {
      showErrorDialog(context, getString(R.string.RegistrationActivity_you_must_specify_your_country_code))
      return
    }
    if (TextUtils.isEmpty(viewModel.number.nationalNumber)) {
      showErrorDialog(context, getString(R.string.RegistrationActivity_please_enter_a_valid_phone_number_to_register))
      return
    }
    val number = viewModel.number
    val e164number = number.e164Number
    if (!number.isValid) {
      Dialogs.showAlertDialog(context,
        getString(R.string.RegistrationActivity_invalid_number), String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid), e164number))
      return
    }
    val fcmStatus = PlayServicesUtil.getPlayServicesStatus(context)
    if (fcmStatus == PlayServicesStatus.SUCCESS) {
      val activity: Activity = requireActivity()
      if (BuildConfig.DEBUG) {
        if (CommonUtils.isMyServiceRunning(activity, BackupService::class.java)) {
          CommonUtils.stopBackupService(activity, false)
        }
        PrefManager.setStringPref(getContext(), ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY, AuthenticatorConstants.Companion.BASE_URL.first)
        PrefManager.setStringPref(getContext(), ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY, AuthenticatorConstants.Companion.BASE_URL.second)
        CommonUtils.setUrl(activity.applicationContext, AuthenticatorConstants.Companion.BASE_URL.first, AuthenticatorConstants.Companion.BASE_URL.second)
      }
      sendArchiveLog("Register success with $e164number Phone number")
      val lastNumber = PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, "")
      if (lastNumber != e164number) {
        CommonUtils.setActivatedUser(requireContext(), false)
        PrefManager.setStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, e164number)
      }
      AndroidCopySDK.getInstance(context).savePhoneNumber(getPhoneNumberInTestMode(context))
      mIsLoginAuthenticationInProgress = true
      //      startAutoAuthentication(requireContext(), e164number);
      mobileNumber = e164number
      val authStatus = PrefManager.getIntPref(requireContext(),
        IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      if (CommonUtils.isActivatedUser(context)) {
        confirmNumberPrompt(context, e164number) { handleRequestVerification(context, true) }
      } else {
        if (isMDM(context) && authStatus == IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal) { // mdm auth skip this fragment and work on EnterSmsCodeFragment
          startMdm()
        } else {
          startAutoAuthentication(e164number) //start self auth
        }
      }
    } else if (fcmStatus == PlayServicesStatus.MISSING) {
      confirmNumberPrompt(context, e164number) { handlePromptForNoPlayServices(context) }
    } else if (fcmStatus == PlayServicesStatus.NEEDS_UPDATE) {
      GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 0)!!.show()
    } else {
      Dialogs.showAlertDialog(context,
        getString(R.string.RegistrationActivity_play_services_error),
        getString(R.string.RegistrationActivity_google_play_services_is_updating_or_unavailable))
    }
  }

  private fun startMdm() {
    Log.d(TAG, "startMdm")
    initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
    startMDMAuthenticator(requireActivity(), mobileNumber!!, BuildConfig.signal_teleMessage_version, this)
  }


  override fun failureMDMAuth(reason: String) {
    val onCancel = "onCancel"
    val server = "server"
    Log.d(TAG, "failureMDMAuth, reason: $reason")
    if (reason == onCancel) {
      showDialog(requireActivity()  ,object :MDMDialogListener {
        override fun startIntuneAgain() {
          startMdm()
        }
      })
    } //update app that intune signed failed: two cases. 1. try intune auth again  2. move to self auth
    else if (reason.contains(server) || reason.contains("Authentication failed") /*|| reason.contains("managerID")*/) { //try intune auth again
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal)
      requireActivity().runOnUiThread { confirmNumberPrompt(requireContext(), mobileNumber!!) { handleRequestVerification(requireContext(), true) } }
    } else { //this case should pass to self-auth
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal)
      requireActivity().runOnUiThread { confirmNumberPrompt(requireContext(), mobileNumber!!) { handleRequestVerification(requireContext(), true) } }
    }
  }

  override fun successMDMAuth() {
    Log.d(TAG, "successMDMAuth")
    startIntuneAutoAuthentication(mobileNumber!!)
  }

  /**
   * intune
   *
   * @param e164number
   */
  private fun startIntuneAutoAuthentication(e164number: String) {
    Log.d(TAG, "startAutoAuthentication")
    initAuthenticator(e164number)
    continueIntuneAuthentication((requireContext().applicationContext as ApplicationInterface), this)
  }

  private fun startAutoAuthentication(e164number: String) {
    Log.i(TAG, "startAutoAuthentication")
    initTeleMessageSignalFirebaseAccount(requireContext(), null, true)
    Log.i(TAG, "current FCM: " + FirebaseApp.getInstance().getOptions().projectId)
    initAuthenticator(e164number)
    startAuthentication((requireContext().applicationContext as ApplicationInterface), this)
    if (!progressBarShown) {
      showProgressBar()
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: UpdateEvent?) {
    if (event == null) {
      return
    }
    Log.d("EnterPhoneNumberFragment", "UpdateEvent -> onEvent: " + event.type)
    if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      CommonUtils.setActivatedUser(requireContext(), true)
      val number = viewModel.number
      val e164number = number.e164Number
      confirmNumberPrompt(requireContext(), e164number, Runnable { handleRequestVerification(requireContext(), true) })
    } else if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      CommonUtils.setActivatedUser(requireContext(), false)
      val dialog = SelfAuthenticationDialogBuilder()
      dialog.doSendLogsClicked(requireActivity())
      dialog.logCallback.observe(this) { result: ResourceStatus? ->
        if (result != null) {
          when (result) {
            ResourceStatus.Success, ResourceStatus.Error -> progressBarCustomView?.visibility = View.GONE
            ResourceStatus.Loading -> progressBarCustomView?.visibility = View.VISIBLE
          }
        }
      }
    }
    Log.i(TAG, "onMessageEvent -> 1 current FCM: " + FirebaseApp.getInstance().getOptions().projectId)
    Log.d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ")
    initOfficialSignalFirebaseAccount(requireContext())
    Log.i(TAG, "onMessageEvent -> 2 current FCM: " + FirebaseApp.getInstance().getOptions().projectId)
    if (progressBarShown) {
      hideProgressBar()
    }
  }


  override fun authenticationProcessMessage(message: String) {
    Log.d(TAG, "authenticationProcessMessage = $message")
    if (!message.isEmpty()) {
      mIsLoginAuthenticationInProgress = false
      //      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
      EventBus.getDefault().post(UpdateEvent(UpdateEvent.EVENTS_TYPE.suspension))
    }
  }
}