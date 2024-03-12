package org.tm.archive.registration.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.FirebaseApp;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.network.appSettings.UpdateEvent;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator;
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator;
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus;
import com.tm.utils.ApplicationInterface;

import org.archiver.FCMConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.intune.IntuneAuthManager;
import org.jetbrains.annotations.NotNull;
import org.selfAuthentication.SelfAuthenticationDialogBuilder;
import org.selfAuthentication.SelfAuthenticatorManager;
import org.signal.core.util.logging.Log;
import org.tm.archive.BuildConfig;
import org.tm.archive.R;
import org.tm.archive.registration.viewmodel.NumberViewState;
import org.tm.archive.registration.viewmodel.RegistrationViewModel;
import org.tm.archive.util.FeatureFlags;
import org.signal.core.util.concurrent.SimpleTask;
import org.tm.archive.util.navigation.SafeNavigation;

import java.io.IOException;

public final class EnterSmsCodeFragment extends BaseEnterSmsCodeFragment<RegistrationViewModel> implements SignalStrengthPhoneStateListener.Callback
    , IAuthenticationStatus, IMDMAuthenticator //*TM_SA*//
{

  //**TM_SA**// START
  private boolean progressBarShown;

//  public static boolean          mIsLoginAuthenticationInProgress = false;
  private       ConstraintLayout constraintLayout;
  private       View             progressBarCustomView;
  private RegistrationViewModel          viewModel;
  String mobileNumber;

  Button startAuthButton;

  //**TM_SA**// END
  private static final String TAG = Log.tag(EnterSmsCodeFragment.class);

  public EnterSmsCodeFragment() {
    super(R.layout.fragment_registration_enter_code);
  }

  //**TM_SA**// START
  /*@Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if(EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }*/

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    startAuthButton = view.findViewById(R.id.buttonTelemessageAuth);
    viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
    mobileNumber = viewModel.getNumber().getE164Number();
    constraintLayout = view.findViewById(R.id.constraint_layout);
    initProgressBar();
    constraintLayout.addView(progressBarCustomView);

    startAuthButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startAutoAuthentication(mobileNumber); //start self auth
      }
    });
  }

  private void initProgressBar(){
    progressBarShown = false;

    progressBarCustomView = LayoutInflater.from(getContext()).inflate(R.layout.progress_bar_layout_with_background, null, false);
    LinearLayout.LayoutParams backgroundLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    progressBarCustomView.setLayoutParams(backgroundLayoutParams);
    progressBarCustomView.setVisibility(View.GONE);
  }
  private void hideProgressBar(){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarCustomView.setVisibility(View.GONE);
        progressBarShown = false;
        Log.d(TAG, "Registration progress hidden");
      }
    });
  }

  private void showProgressBar(){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarCustomView.setVisibility(View.VISIBLE);
        progressBarShown = true;
        Log.d(TAG, "Registration progress shown");
      }
    });
  }
  //**TM_SA**// END


  @Override
  protected @NonNull RegistrationViewModel getViewModel() {
    return new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
  }

  @Override
  protected void handleSuccessfulVerify() {
    //**TM_SA**// START
//    mIsLoginAuthenticationInProgress = true;
    int authStatus = PrefManager.getIntPref(requireContext(),
                                            IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
    if (CommonUtils.isActivatedUser(requireContext())) {
      continueSignalFlow();
    } else {
      if (MDMAuthenticator.INSTANCE.isMDM(requireContext()) && authStatus == IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal()) {// mdm auth skip this fragment and work on EnterSmsCodeFragment
        startMdm();
      } else {
        startAutoAuthentication(mobileNumber); //start self auth
      }
    }
    //**TM_SA**//End


  }
  private void continueSignalFlow() {
    SimpleTask.run(() -> {
      long startTime = System.currentTimeMillis();
      try {
        FeatureFlags.refreshSync();
        Log.i(TAG, "Took " + (System.currentTimeMillis() - startTime) + " ms to get feature flags.");
      } catch (IOException e) {
        Log.w(TAG, "Failed to refresh flags after " + (System.currentTimeMillis() - startTime) + " ms.", e);
      }
      return null;
    }, none -> displaySuccess(() -> SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), EnterSmsCodeFragmentDirections.actionSuccessfulRegistration())));
  }

  @Override
  protected void navigateToRegistrationLock(long timeRemaining) {
    SafeNavigation.safeNavigate(Navigation.findNavController(requireView()),
                                EnterSmsCodeFragmentDirections.actionRequireKbsLockPin(timeRemaining));
  }

  @Override
  protected void navigateToCaptcha() {
    SafeNavigation.safeNavigate(NavHostFragment.findNavController(this), EnterSmsCodeFragmentDirections.actionRequestCaptcha());
  }

  @Override
  protected void navigateToKbsAccountLocked() {
    SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), RegistrationLockFragmentDirections.actionAccountLocked());
  }



  //**TM_SA**//START
  private void startMdm() {
    Log.d(TAG, "startMdm");
    FCMConnector.initTeleMessageSignalFirebaseAccount(requireContext(), null, true);
    MDMAuthenticator.INSTANCE.startMDMAuthenticator(requireActivity(), mobileNumber, BuildConfig.signal_teleMessage_version, this);
  }


  @Override
  public void failureMDMAuth(String reason) {
    final String onCancel = "onCancel", server = "server";
    Log.d(TAG, "failureMDMAuth, reason: " + reason);
    if(reason.equals(onCancel)) {
      IntuneAuthManager.INSTANCE.showDialog(requireActivity(), this::startMdm);
    } //update app that intune signed failed: two cases. 1. try intune auth again  2. move to self auth
    else if(reason.contains(server) || reason.contains("Authentication failed")
      /*|| reason.contains("managerID")*/) { //try intune auth again
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
      Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
      requireActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          continueSignalFlow();
        }
      });
    }else  { //this case should pass to self-auth
      PrefManager.setIntPref(requireContext(),IntuneAuthManager.MDM_Auth_Status_String,IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal());
      Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal());
      requireActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          continueSignalFlow();
        }
      });
    }

  }

  @Override
  public void successMDMAuth() {
    Log.d(TAG, "successMDMAuth");
    startIntuneAutoAuthentication(mobileNumber);
  }

  private void startIntuneAutoAuthentication(String e164number) {
    Log.d(TAG, "startAutoAuthentication");
    SelfAuthenticatorManager.INSTANCE.initAuthenticator(e164number);
    IntuneAuthManager.INSTANCE.continueIntuneAuthentication((ApplicationInterface)requireContext().getApplicationContext(), this);
  }

  private void startAutoAuthentication(String e164number) {
    Log.i(TAG , "startAutoAuthentication");
    FCMConnector.initTeleMessageSignalFirebaseAccount(requireContext(), null, true);
    Log.i(TAG, "current FCM: " + FirebaseApp.getInstance().getOptions().getProjectId());
    SelfAuthenticatorManager.INSTANCE.initAuthenticator(e164number);
    SelfAuthenticatorManager.INSTANCE.startAuthentication((ApplicationInterface) getContext().getApplicationContext(), this);
    if (!progressBarShown) {
      showProgressBar();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(UpdateEvent event) {
    if (event == null) {
      return;
    }
    Log.d("EnterPhoneNumberFragment", "UpdateEvent -> onEvent: " + event.type);

    if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      CommonUtils.setActivatedUser(requireContext(), true);
      continueSignalFlow();

    } else if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      CommonUtils.setActivatedUser(requireContext(), false);
      SelfAuthenticationDialogBuilder dialog = new SelfAuthenticationDialogBuilder();
      dialog.doSendLogsClicked(requireActivity(), progressBarCustomView);
      startAuthButton.setVisibility(View.VISIBLE);
    }

    Log.i(TAG, "onMessageEvent -> 1 current FCM: " + FirebaseApp.getInstance().getOptions().getProjectId());
    Log.d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ");
    FCMConnector.initOfficialSignalFirebaseAccount(requireContext());
    Log.i(TAG, "onMessageEvent -> 2 current FCM: " + FirebaseApp.getInstance().getOptions().getProjectId());

    if (progressBarShown) {
      hideProgressBar();
    }
  }


  @Override
  public void authenticationProcessMessage(@NotNull String message) {
    Log.d(TAG, "authenticationProcessMessage = " + message);
    if (!message.isEmpty()) {
//      mIsLoginAuthenticationInProgress = false;
//      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
      EventBus.getDefault().post(new UpdateEvent(UpdateEvent.EVENTS_TYPE.suspension));
    }
  }
  //**TM_SA**//End

}

