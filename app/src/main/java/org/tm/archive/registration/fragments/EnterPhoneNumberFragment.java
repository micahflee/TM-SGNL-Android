package org.tm.archive.registration.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.MessageEvent;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator;
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator;
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus;

import org.archive.selfAuthentication.SelfAuthenticatorConstants;
import org.archiver.ArchiveLogger;
import org.archiver.ArchivePreferenceConstants;
import org.archiver.ArchiveUtil;
import org.archiver.FCMConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.intune.IntuneAuthManager;
import org.jetbrains.annotations.NotNull;
import org.selfAuthentication.SelfAuthenticatorManager;
import org.signal.core.util.ThreadUtil;
import org.signal.core.util.concurrent.LifecycleDisposable;
import org.signal.core.util.logging.Log;
import org.tm.archive.ApplicationContext;
import org.tm.archive.BuildConfig;
import org.tm.archive.LoggingFragment;
import org.tm.archive.R;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.registration.RegistrationSessionProcessor;
import org.tm.archive.registration.VerifyAccountRepository.Mode;
import org.tm.archive.registration.util.RegistrationNumberInputController;
import org.tm.archive.registration.viewmodel.NumberViewState;
import org.tm.archive.registration.viewmodel.RegistrationViewModel;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.Debouncer;
import org.tm.archive.util.Dialogs;
import org.tm.archive.util.PlayServicesUtil;
import org.tm.archive.util.SupportEmailUtil;
import org.tm.archive.util.ViewUtil;
import org.tm.archive.util.dualsim.MccMncProducer;
import org.tm.archive.util.navigation.SafeNavigation;
import org.tm.archive.util.views.CircularProgressMaterialButton;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import static org.tm.archive.registration.fragments.RegistrationViewDelegate.setDebugLogSubmitMultiTapView;
import static org.tm.archive.registration.fragments.RegistrationViewDelegate.showConfirmNumberDialogIfTranslated;

public final class EnterPhoneNumberFragment extends LoggingFragment
        implements RegistrationNumberInputController.Callbacks, IAuthenticationStatus, IMDMAuthenticator { //*TM_SA*//

  private static final String TAG = Log.tag(EnterPhoneNumberFragment.class);

  private TextInputLayout                countryCode;
  private TextInputLayout                number;
  private CircularProgressMaterialButton register;
  private View                           cancel;
  private ScrollView                     scrollView;
  private RegistrationViewModel          viewModel;

  //**TM_SA**// START
  private Context mContext;
  private boolean     progressBarShown;

  public static boolean mIsLoginAuthenticationInProgress = false;
  private ConstraintLayout constraintLayout;
  private View progressBarCustomView;
  String mobileNumber;
  //**TM_SA**// END

  private final LifecycleDisposable disposables = new LifecycleDisposable();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // <!--//**TM_SA**//--> START
    mContext = getActivity();
    if(!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    // <!--//**TM_SA**//--> END
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_registration_enter_phone_number, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setDebugLogSubmitMultiTapView(view.findViewById(R.id.verify_header));

    countryCode = view.findViewById(R.id.country_code);
    number      = view.findViewById(R.id.number);
    cancel      = view.findViewById(R.id.cancel_button);
    scrollView  = view.findViewById(R.id.scroll_view);
    register    = view.findViewById(R.id.registerButton);
    constraintLayout = view.findViewById(R.id.constraint_layout);  //**TM_SA**//

    initProgressBar();  //**TM_SA**//

    RegistrationNumberInputController controller = new RegistrationNumberInputController(requireContext(),
                                                                                         this,
                                                                                         Objects.requireNonNull(number.getEditText()),
                                                                                         countryCode);
    register.setOnClickListener(v -> handleRegister(requireContext()));

    disposables.bindTo(getViewLifecycleOwner().getLifecycle());
    viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

    if (viewModel.isReregister()) {
      cancel.setVisibility(View.VISIBLE);
      cancel.setOnClickListener(v -> requireActivity().finish());
    } else {
      cancel.setVisibility(View.GONE);
    }

    viewModel.getLiveNumber().observe(getViewLifecycleOwner(), controller::updateNumberFormatter);

    if (viewModel.hasCaptchaToken()) {
      ThreadUtil.runOnMainDelayed(() -> handleRegister(requireContext()), 250);
    }

    Toolbar toolbar = view.findViewById(R.id.toolbar);
    ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    final ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setTitle(null);
    }

    final NumberViewState viewModelNumber = viewModel.getNumber();
    if (viewModelNumber.getCountryCode() == 0) {
      controller.prepopulateCountryCode();
    }
    controller.setNumberAndCountryCode(viewModelNumber);

    showKeyboard(number.getEditText());

    if (viewModel.hasUserSkippedReRegisterFlow() && viewModel.shouldAutoShowSmsConfirmDialog()) {
      viewModel.setAutoShowSmsConfirmDialog(false);
      ThreadUtil.runOnMainDelayed(() -> handleRegister(requireContext()), 250);
    }

    constraintLayout.addView(progressBarCustomView);//**TM_SA**//
    FCMConnector.initTeleMessageSignalFirebaseAccount(requireContext(), null, true);//**TM_SA**//
  }

  //**TM_SA**// START
  @Override public void onDestroy() {
    super.onDestroy();
    if(EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }
  //**TM_SA**// END


  //**TM_SA**// START
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
        com.tm.logger.Log.d(TAG, "Registration progress hidden");
      }
    });
  }

  private void showProgressBar(){
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBarCustomView.setVisibility(View.VISIBLE);
        progressBarShown = true;
        com.tm.logger.Log.d(TAG, "Registration progress shown");
      }
    });
  }
  //**TM_SA**// END

  private void showKeyboard(View viewToFocus) {
    viewToFocus.requestFocus();
    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(viewToFocus, InputMethodManager.SHOW_IMPLICIT);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.enter_phone_number, menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.phone_menu_use_proxy) {
      SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), EnterPhoneNumberFragmentDirections.actionEditProxy());
      return true;
    } else {
      return false;
    }
  }

  private void handleRegister(@NonNull Context context) {
    if (viewModel.getNumber().getCountryCode() == 0) {
      showErrorDialog(context, getString(R.string.RegistrationActivity_you_must_specify_your_country_code));
      return;
    }

    if (TextUtils.isEmpty(viewModel.getNumber().getNationalNumber())) {
      showErrorDialog(context, getString(R.string.RegistrationActivity_please_enter_a_valid_phone_number_to_register));
      return;
    }

    final NumberViewState number     = viewModel.getNumber();
    final String          e164number = number.getE164Number();

    if (!number.isValid()) {
      Dialogs.showAlertDialog(context,
                              getString(R.string.RegistrationActivity_invalid_number),
                              String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid), e164number));
      return;
    }

    PlayServicesUtil.PlayServicesStatus fcmStatus = PlayServicesUtil.getPlayServicesStatus(context);

    if (fcmStatus == PlayServicesUtil.PlayServicesStatus.SUCCESS) {
      //**TM_SA**//Start
      ArchiveLogger.Companion.sendArchiveLog("Register success with " + e164number + " Phone number" );
      PrefManager.setStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, e164number);

      AndroidCopySDK.getInstance(context).savePhoneNumber(ArchiveUtil.Companion.getPhoneNumberInTestMode(context));
      mIsLoginAuthenticationInProgress = true;
//      startAutoAuthentication(requireContext(), e164number);
      mobileNumber = e164number;
      int authStatus = PrefManager.getIntPref(requireContext(),
              IntuneAuthManager.MDM_Auth_Status_String, IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
      if (MDMAuthenticator.INSTANCE.isMDM(context) && authStatus == IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal()) {// mdm auth skip this fragment and work on EnterSmsCodeFragment
        startMdm();
        //confirmNumberPrompt(context, e164number, () -> handleRequestVerification(context, true));
      } else {
        startAutoAuthentication(requireContext(), e164number); //start self auth
      }


      //confirmNumberPrompt(context, e164number, () -> onE164EnteredSuccessfully(context, true));
      //**TM_SA**//End
    } else if (fcmStatus == PlayServicesUtil.PlayServicesStatus.MISSING) {
      confirmNumberPrompt(context, e164number, () -> handlePromptForNoPlayServices(context));
    } else if (fcmStatus == PlayServicesUtil.PlayServicesStatus.NEEDS_UPDATE) {
      GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 0).show();
    } else {
      Dialogs.showAlertDialog(context,
                              getString(R.string.RegistrationActivity_play_services_error),
                              getString(R.string.RegistrationActivity_google_play_services_is_updating_or_unavailable));
    }
  }

  //**TM_SA**//START
  protected void startMdm() {
    Log.d(TAG, "startMdm");
    MDMAuthenticator.INSTANCE.startMDMAuthenticator(requireActivity(), mobileNumber, BuildConfig.signal_teleMessage_version, this);
  }


  @Override
  public void failureMDMAuth(String reason) {
    final String onCancel = "onCancel", server = "server";
    com.tm.logger.Log.d(TAG, "failureMDMAuth, reason: " + reason);
    if(reason.equals(onCancel)) {
      IntuneAuthManager.INSTANCE.showDialog(requireActivity(), this::startMdm);
    } //update app that intune signed failed: two cases. 1. try intune auth again  2. move to self auth
    else if(reason.contains(server) || reason.contains("Authentication failed")
            || reason.contains("managerID")) { //try intune auth again
      PrefManager.setIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String,IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
      com.tm.logger.Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_INTUNE_AUTH.ordinal());
      requireActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          confirmNumberPrompt(requireContext(), mobileNumber, () -> handleRequestVerification(requireContext(), true));
        }
      });
    }else  { //this case should pass to self-auth
      PrefManager.setIntPref(requireContext(),IntuneAuthManager.MDM_Auth_Status_String,IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal());
      com.tm.logger.Log.d(TAG, "status auth is " + IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal());
      requireActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          confirmNumberPrompt(requireContext(), mobileNumber, () -> handleRequestVerification(requireContext(), true));
        }
      });
    }

  }

  @Override
  public void successMDMAuth() {
    com.tm.logger.Log.d(TAG, "successMDMAuth");
    startIntuneAutoAuthentication(mobileNumber);
  }

  /**
   * intune
   *
   * @param e164number
   */
  private void startIntuneAutoAuthentication(String e164number) {
    Log.d(TAG, "startAutoAuthentication");
    com.tm.logger.Log.d(TAG, "startAutoAuthentication");
    SelfAuthenticatorManager.INSTANCE.initAuthenticator(e164number);
    IntuneAuthManager.INSTANCE.continueIntuneAuthentication(this);
  }

  private void startAutoAuthentication(Context context, String e164number) {
    SelfAuthenticatorManager.INSTANCE.initAuthenticator(e164number);
    SelfAuthenticatorManager.INSTANCE.startAuthentication(context, this);
    if (!progressBarShown) {
      showProgressBar();
    }
  }
  //**TM_SA**//END
  private void onE164EnteredSuccessfully(@NonNull Context context, boolean fcmSupported) {
    enterInProgressUiState();

    Disposable disposable = viewModel.canEnterSkipSmsFlow()
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .onErrorReturnItem(false)
                                     .subscribe(canEnter -> {
                                       if (canEnter) {
                                         Log.i(TAG, "Enter skip flow");
                                         SafeNavigation.safeNavigate(NavHostFragment.findNavController(this), EnterPhoneNumberFragmentDirections.actionReRegisterWithPinFragment());
                                       } else {
                                         Log.i(TAG, "Unable to collect necessary data to enter skip flow, returning to normal");
                                         handleRequestVerification(context, fcmSupported);
                                       }
                                     });
    disposables.add(disposable);
  }

  private void handleRequestVerification(@NonNull Context context, boolean fcmSupported) {
    if (fcmSupported) {
      SmsRetrieverClient client  = SmsRetriever.getClient(context);
      Task<Void>         task    = client.startSmsRetriever();
      AtomicBoolean      handled = new AtomicBoolean(false);

      Debouncer debouncer = new Debouncer(TimeUnit.SECONDS.toMillis(5));
      debouncer.publish(() -> {
        if (!handled.getAndSet(true)) {
          Log.w(TAG, "Timed out waiting for SMS listener!");
          requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
        }
      });

      task.addOnSuccessListener(none -> {
        if (!handled.getAndSet(true)) {
          Log.i(TAG, "Successfully registered SMS listener.");
          requestVerificationCode(Mode.SMS_WITH_LISTENER);
        } else {
          Log.w(TAG, "Successfully registered listener after timeout.");
        }
        debouncer.clear();
      });

      task.addOnFailureListener(e -> {
        if (!handled.getAndSet(true)) {
          Log.w(TAG, "Failed to register SMS listener.", e);
          requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
        } else {
          Log.w(TAG, "Failed to register listener after timeout.");
        }
        debouncer.clear();
      });

      task.addOnCanceledListener(() -> {
        if (!handled.getAndSet(true)) {
          Log.w(TAG, "SMS listener registration canceled.");
          requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
        } else {
          Log.w(TAG, "SMS listener registration canceled after timeout.");
        }
        debouncer.clear();
      });

    } else {
      Log.i(TAG, "FCM is not supported, using no SMS listener");
      requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
    }
  }

  private void enterInProgressUiState() {
    register.setSpinning();
    countryCode.setEnabled(false);
    number.setEnabled(false);
    cancel.setVisibility(View.GONE);
  }

  private void exitInProgressUiState() {
    register.cancelSpinning();
    countryCode.setEnabled(true);
    number.setEnabled(true);
    if (viewModel.isReregister()) {
      cancel.setVisibility(View.VISIBLE);
    }
  }

  private void requestVerificationCode(@NonNull Mode mode) {
    NavController  navController  = NavHostFragment.findNavController(this);
    MccMncProducer mccMncProducer = new MccMncProducer(requireContext());
    Disposable request = viewModel.requestVerificationCode(mode, mccMncProducer.getMcc(), mccMncProducer.getMnc())
                                  .doOnSubscribe(unused -> SignalStore.account().setRegistered(false))
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe((RegistrationSessionProcessor processor) -> {
                                    if (processor.verificationCodeRequestSuccess()) {
                                      disposables.add(updateFcmTokenValue());
                                      SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionEnterVerificationCode());
                                    } else if (processor.captchaRequired(viewModel.getExcludedChallenges())) {
                                      Log.i(TAG, "Unable to request sms code due to captcha required");
                                      SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionRequestCaptcha());
                                    } else if (processor.exhaustedVerificationCodeAttempts()) {
                                      Log.i(TAG, "Unable to request sms code due to exhausting attempts");
                                      showErrorDialog(register.getContext(), getString(R.string.RegistrationActivity_rate_limited_to_service));
                                    } else if (processor.rateLimit()) {
                                      Log.i(TAG, "Unable to request sms code due to rate limit");
                                      showErrorDialog(register.getContext(), getString(R.string.RegistrationActivity_rate_limited_to_try_again, formatMillisecondsToString(processor.getRateLimit())));
                                    } else if (processor.isImpossibleNumber()) {
                                      Log.w(TAG, "Impossible number", processor.getError());
                                      Dialogs.showAlertDialog(requireContext(),
                                                              getString(R.string.RegistrationActivity_invalid_number),
                                                              String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid), viewModel.getNumber().getFullFormattedNumber()));
                                    } else if (processor.isNonNormalizedNumber()) {
                                      handleNonNormalizedNumberError(processor.getOriginalNumber(), processor.getNormalizedNumber(), mode);
                                    } else if (processor.isTokenRejected()) {
                                      Log.i(TAG, "The server did not accept the information.", processor.getError());
                                      showErrorDialog(register.getContext(), getString(R.string.RegistrationActivity_we_need_to_verify_that_youre_human));
                                    } else if (processor instanceof RegistrationSessionProcessor.RegistrationSessionProcessorForVerification
                                               && ((RegistrationSessionProcessor.RegistrationSessionProcessorForVerification) processor).externalServiceFailure())
                                    {
                                      Log.w(TAG, "The server reported a failure with an external service.", processor.getError());
                                      showErrorDialog(register.getContext(), getString(R.string.RegistrationActivity_external_service_error));
                                    } else {
                                      Log.i(TAG, "Unknown error during verification code request", processor.getError());
                                      showErrorDialog(register.getContext(), getString(R.string.RegistrationActivity_unable_to_connect_to_service));
                                    }

                                    exitInProgressUiState();
                                  });

    disposables.add(request);
  }

  private Disposable updateFcmTokenValue() {
    return viewModel.updateFcmTokenValue().subscribe();
  }

  private String formatMillisecondsToString(long milliseconds) {
    long totalSeconds = milliseconds / 1000;
    long HH           = totalSeconds / 3600;
    long MM           = (totalSeconds % 3600) / 60;
    long SS           = totalSeconds % 60;
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", HH, MM, SS);
  }

  public void showErrorDialog(Context context, String msg) {
    new MaterialAlertDialogBuilder(context).setMessage(msg).setPositiveButton(R.string.ok, null).show();
  }

  @Override
  public void onNumberFocused() {
    scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, register.getBottom()), 250);
  }

  @Override
  public void onNumberInputDone(@NonNull View view) {
    ViewUtil.hideKeyboard(requireContext(), view);
    handleRegister(requireContext());
  }

  @Override
  public void setNationalNumber(@NonNull String number) {
    viewModel.setNationalNumber(number);
  }

  @Override
  public void setCountry(int countryCode) {
    viewModel.onCountrySelected(null, countryCode);
  }

  @Override
  public void onStart() {
    super.onStart();
    String sessionE164 = viewModel.getSessionE164();
    if (sessionE164 != null && viewModel.getSessionId() != null && viewModel.getCaptchaToken() == null) {
      checkIfSessionIsInProgressAndAdvance(sessionE164);
    }
  }

  private void checkIfSessionIsInProgressAndAdvance(@NonNull String sessionE164) {
    NavController navController = NavHostFragment.findNavController(this);
    Disposable request = viewModel.validateSession(sessionE164)
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(processor -> {
                                    if (processor.hasResult() && processor.canSubmitProofImmediately()) {
                                      try {
                                        viewModel.restorePhoneNumberStateFromE164(sessionE164);
                                        SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionEnterVerificationCode());
                                      } catch (NumberParseException numberParseException) {
                                        viewModel.resetSession();
                                      }
                                    } else {
                                      viewModel.resetSession();
                                    }
                                  });

    disposables.add(request);
  }

  private void handleNonNormalizedNumberError(@NonNull String originalNumber, @NonNull String normalizedNumber, @NonNull Mode mode) {
    try {
      Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(normalizedNumber, null);

      new MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.RegistrationActivity_non_standard_number_format)
          .setMessage(getString(R.string.RegistrationActivity_the_number_you_entered_appears_to_be_a_non_standard, originalNumber, normalizedNumber))
          .setNegativeButton(android.R.string.no, (d, i) -> d.dismiss())
          .setNeutralButton(R.string.RegistrationActivity_contact_signal_support, (d, i) -> {
            String subject = getString(R.string.RegistrationActivity_signal_android_phone_number_format);
            String body    = SupportEmailUtil.generateSupportEmailBody(requireContext(), R.string.RegistrationActivity_signal_android_phone_number_format, null, null);

            CommunicationActions.openEmail(requireContext(), SupportEmailUtil.getSupportEmailAddress(requireContext()), subject, body);
            d.dismiss();
          })
          .setPositiveButton(R.string.yes, (d, i) -> {
            countryCode.getEditText().setText(String.valueOf(phoneNumber.getCountryCode()));
            number.getEditText().setText(String.valueOf(phoneNumber.getNationalNumber()));
            requestVerificationCode(mode);
            d.dismiss();
          })
          .show();
    } catch (NumberParseException e) {
      Log.w(TAG, "Failed to parse number!", e);

      Dialogs.showAlertDialog(requireContext(),
                              getString(R.string.RegistrationActivity_invalid_number),
                              String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid), viewModel.getNumber().getFullFormattedNumber()));
    }
  }

  private void handlePromptForNoPlayServices(@NonNull Context context) {
    new MaterialAlertDialogBuilder(context)
        .setTitle(R.string.RegistrationActivity_missing_google_play_services)
        .setMessage(R.string.RegistrationActivity_this_device_is_missing_google_play_services)
        .setPositiveButton(R.string.RegistrationActivity_i_understand, (dialog1, which) -> onE164EnteredSuccessfully(context, false))
        .setNegativeButton(android.R.string.cancel, null)
        .show();
  }

  private void confirmNumberPrompt(@NonNull Context context,
                                   @NonNull String e164number,
                                   @NonNull Runnable onConfirmed)
  {
    enterInProgressUiState();

    disposables.add(
        viewModel.canEnterSkipSmsFlow()
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(canSkipSms -> showConfirmNumberDialogIfTranslated(context,
                                                                              viewModel.hasUserSkippedReRegisterFlow() ? R.string.RegistrationActivity_additional_verification_required
                                                                                                                       : R.string.RegistrationActivity_phone_number_verification_dialog_title,
                                                                              canSkipSms ? null
                                                                                         : R.string.RegistrationActivity_a_verification_code_will_be_sent_to_this_number,
                                                                              e164number,
                                                                              () -> {
                                                                                exitInProgressUiState();
                                                                                ViewUtil.hideKeyboard(context, number.getEditText());
                                                                                onConfirmed.run();
                                                                              },
                                                                              () -> {
                                                                                exitInProgressUiState();
                                                                                ViewUtil.focusAndMoveCursorToEndAndOpenKeyboard(this.number.getEditText());
                                                                              }))
    );
  }




  //**TM_SA**//START
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(MessageEvent event) {
    com.tm.logger.Log.d(TAG,"onMessageEvent -> SelfAuthenticator and Intune authenticator");
    if (event.message != null) {
      com.tm.logger.Log.d(TAG, "event.message = " + event.message);
    } else {
      com.tm.logger.Log.d(TAG, "event.message = null, return;");
      return;
    }
    boolean authSucceed = event.message.equals(SelfAuthenticatorConstants.Companion.getSelfAuthenticationSucceed());
    boolean authFailed = event.message.equals(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed());


    //check if listener is valid
    if (authSucceed || authFailed) {
      int authStatus = PrefManager.getIntPref(requireContext(), IntuneAuthManager.MDM_Auth_Status_String,
              IntuneAuthManager.MdmAuthStatus.ALREADY_SIGN.ordinal());
      if (MDMAuthenticator.INSTANCE.isMDM(requireContext()) &&
              authStatus!= IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal()) {// for managed device,
        //this is managed device. if successful, user is signed and finish auth. if failure, move to self auth for regular flow.
        if (authSucceed) {
          PrefManager.setIntPref(requireContext(),IntuneAuthManager.MDM_Auth_Status_String,
                  IntuneAuthManager.MdmAuthStatus.ALREADY_SIGN.ordinal()); //update app that intune signed successfully
          updatedSelfAuthenticatorDonePreference();//for self auth. update that signed successfully
          com.tm.logger.Log.d(TAG, "status auth is ALREADY_SIGN");
        } else {
          PrefManager.setIntPref(requireContext(),IntuneAuthManager.MDM_Auth_Status_String,IntuneAuthManager.MdmAuthStatus.START_SELF_AUTH.ordinal()); //update app that auth should pass to self auth
          com.tm.logger.Log.d(TAG, "status auth is START_SELF_AUTH");
        }
      } else {

        if (progressBarShown) {
          hideProgressBar();
        }

        com.tm.logger.Log.d(TAG, "event.message 2  = " + event.message);
        if (authSucceed) {
          updatedSelfAuthenticatorDonePreference();
          com.tm.logger.Log.d(TAG, "SelfAuthenticationSucceed ");

        } else {
          //I Removed this because we just show that after 48 hours.
          //SelfAuthenticatorManager.INSTANCE.showTheRelevantDialogIfNeeded((FragmentActivity)mContext);
          com.tm.logger.Log.d(TAG, "getSelfAuthenticationFailure = " + event.message);
        }

      }
      final NumberViewState number = viewModel.getNumber();
      final String e164number = number.getE164Number();
      confirmNumberPrompt(mContext, e164number, () -> handleRequestVerification(mContext, true));
      com.tm.logger.Log.d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ");
      FCMConnector.initOfficialSignalFirebaseAccount(mContext);
    }


    /*if (event.message != null) {
      com.tm.logger.Log.d(TAG, "event.message = " + event.message);
    } else {
      com.tm.logger.Log.d(TAG, "event.message = null");
    }

    //check if listener is valid
    if (event.message != null && (event.message.equals(SelfAuthenticatorConstants.Companion.getSelfAuthenticationSucceed()) ||
                                  event.message.equals(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()))) {
      if (progressBarShown) {
        hideProgressBar();
      }

      com.tm.logger.Log.d(TAG, "event.message 2  = " + event.message);
      if (SelfAuthenticatorConstants.Companion.getSelfAuthenticationSucceed().equals(event.message)) {
        updatedSelfAuthenticatorDonePreference();
        com.tm.logger.Log.d(TAG, "SelfAuthenticationSucceed ");

      } else {
        //I Removed this because we just show that after 48 hours.
        //SelfAuthenticatorManager.INSTANCE.showTheRelevantDialogIfNeeded((FragmentActivity)mContext);
        com.tm.logger.Log.d(TAG, "getSelfAuthenticationFailure = " + event.message);
      }

      final NumberViewState number = viewModel.getNumber();
      final String e164number = number.getE164Number();
      confirmNumberPrompt(mContext, e164number, () -> onE164EnteredSuccessfully(mContext, true));

      com.tm.logger.Log.d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ");
      FCMConnector.initOfficialSignalFirebaseAccount(mContext);

    }*/
  }

  public void updatedSelfAuthenticatorDonePreference() {

    SharedPreferences        preferences = ApplicationContext.getInstance().getSharedPreferences(SelfAuthenticatorManager.SELF_AUTHENTICATION_PREFERENCE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor      = preferences.edit();
    editor.putBoolean("isAlreadyDoneSelfAuthentication", true);
    editor.apply();
  }


  @Override
  public void authenticationProcessMessage(@NotNull String message) {
    com.tm.logger.Log.d(TAG, "authenticationProcessMessage = " + message);
    if (!message.isEmpty()) {
      mIsLoginAuthenticationInProgress = false;
      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
    }
  }
  //**TM_SA**//End
}
