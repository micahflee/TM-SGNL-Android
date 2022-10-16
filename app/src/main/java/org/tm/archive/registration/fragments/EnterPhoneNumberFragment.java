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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.MessageEvent;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus;

import org.archive.selfAuthentication.SelfAuthenticatorConstants;
import org.archiver.ArchiveLogger;
import org.archiver.ArchivePreferenceConstants;
import org.archiver.ArchiveUtil;
import org.archiver.FCMConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.selfAuthentication.SelfAuthenticatorManager;
import org.signal.core.util.ThreadUtil;
import org.signal.core.util.logging.Log;
import org.tm.archive.ApplicationContext;
import org.tm.archive.LoggingFragment;
import org.tm.archive.R;
import org.tm.archive.components.LabeledEditText;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.registration.VerifyAccountRepository.Mode;
import org.tm.archive.registration.util.RegistrationNumberInputController;
import org.tm.archive.registration.viewmodel.NumberViewState;
import org.tm.archive.registration.viewmodel.RegistrationViewModel;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.Dialogs;
import org.tm.archive.util.LifecycleDisposable;
import org.tm.archive.util.PlayServicesUtil;
import org.tm.archive.util.SupportEmailUtil;
import org.tm.archive.util.ViewUtil;
import org.tm.archive.util.navigation.SafeNavigation;
import org.tm.archive.util.views.CircularProgressMaterialButton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import static org.tm.archive.registration.fragments.RegistrationViewDelegate.setDebugLogSubmitMultiTapView;
import static org.tm.archive.registration.fragments.RegistrationViewDelegate.showConfirmNumberDialogIfTranslated;

public final class EnterPhoneNumberFragment extends LoggingFragment implements RegistrationNumberInputController.Callbacks, IAuthenticationStatus {

  private static final String TAG = Log.tag(EnterPhoneNumberFragment.class);

  private LabeledEditText                countryCode;
  private LabeledEditText                number;
  private CircularProgressMaterialButton register;
  private Spinner                        countrySpinner;
  private View                           cancel;
  private ScrollView                     scrollView;
  private RegistrationViewModel viewModel;
  private ConstraintLayout      constraintLayout;//**TM_SA**//

  private final LifecycleDisposable disposables = new LifecycleDisposable();
  public static boolean mIsLoginAuthenticationInProgress = false;
  private Context mContext;

  //**TM_SA**// START
  private boolean     progressBarShown;
  private View progressBarCustomView;
  //**TM_SA**// END

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

  //**TM_SA**// START
  @Override public void onDestroy() {
    super.onDestroy();
    if(EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }
  //**TM_SA**// END

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_registration_enter_phone_number, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setDebugLogSubmitMultiTapView(view.findViewById(R.id.verify_header));

    countryCode    = view.findViewById(R.id.country_code);
    number         = view.findViewById(R.id.number);
    countrySpinner = view.findViewById(R.id.country_spinner);
    cancel         = view.findViewById(R.id.cancel_button);
    scrollView     = view.findViewById(R.id.scroll_view);
    register       = view.findViewById(R.id.registerButton);
    constraintLayout = view.findViewById(R.id.constraint_layout);  //**TM_SA**//

    initProgressBar();  //**TM_SA**//

    RegistrationNumberInputController controller = new RegistrationNumberInputController(requireContext(),
                                                                                         countryCode,
                                                                                         number,
                                                                                         countrySpinner,
                                                                                         true,
                                                                                         this);

    register.setOnClickListener(v -> handleRegister(requireContext()));

    disposables.bindTo(getViewLifecycleOwner().getLifecycle());
    viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

    if (viewModel.isReregister()) {
      cancel.setVisibility(View.VISIBLE);
      cancel.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    } else {
      cancel.setVisibility(View.GONE);
    }

    viewModel.getLiveNumber().observe(getViewLifecycleOwner(), controller::updateNumber);

    if (viewModel.hasCaptchaToken()) {
      ThreadUtil.runOnMainDelayed(() -> handleRegister(requireContext()), 250);
    }

    Toolbar toolbar = view.findViewById(R.id.toolbar);
    ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(null);

    constraintLayout.addView(progressBarCustomView);//**TM_SA**//
    FCMConnector.initTeleMessageSignalFirebaseAccount(null,true);//**TM_SA**//
  }

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
    if (TextUtils.isEmpty(countryCode.getText())) {
      Toast.makeText(context, getString(R.string.RegistrationActivity_you_must_specify_your_country_code), Toast.LENGTH_LONG).show();
      return;
    }

    if (TextUtils.isEmpty(this.number.getText())) {
      Toast.makeText(context, getString(R.string.RegistrationActivity_you_must_specify_your_phone_number), Toast.LENGTH_LONG).show();
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
      startAutoAuthentication(e164number);
      //confirmNumberPrompt(context, e164number, () -> handleRequestVerification(context, true));
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
  private void startAutoAuthentication(String e164number) {
    SelfAuthenticatorManager.INSTANCE.initAuthenticator(e164number);
    SelfAuthenticatorManager.INSTANCE.startAuthentication(this);
    if (!progressBarShown) {
      showProgressBar();
    }
  }
  //**TM_SA**//END

  private void handleRequestVerification(@NonNull Context context, boolean fcmSupported) {
    register.setSpinning();
    disableAllEntries();

    if (fcmSupported) {
      SmsRetrieverClient client = SmsRetriever.getClient(context);
      Task<Void>         task   = client.startSmsRetriever();

      task.addOnSuccessListener(none -> {
        Log.i(TAG, "Successfully registered SMS listener.");
        requestVerificationCode(Mode.SMS_WITH_LISTENER);
      });

      task.addOnFailureListener(e -> {
        Log.w(TAG, "Failed to register SMS listener.", e);
        requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
      });
    } else {
      Log.i(TAG, "FCM is not supported, using no SMS listener");
      requestVerificationCode(Mode.SMS_WITHOUT_LISTENER);
    }
  }

  private void disableAllEntries() {
    countryCode.setEnabled(false);
    number.setEnabled(false);
    countrySpinner.setEnabled(false);
    cancel.setVisibility(View.GONE);
  }

  private void enableAllEntries() {
    countryCode.setEnabled(true);
    number.setEnabled(true);
    countrySpinner.setEnabled(true);
    if (viewModel.isReregister()) {
      cancel.setVisibility(View.VISIBLE);
    }
  }

  private void requestVerificationCode(@NonNull Mode mode) {
    NavController navController = NavHostFragment.findNavController(this);

    Disposable request = viewModel.requestVerificationCode(mode)
                                  .doOnSubscribe(unused -> SignalStore.account().setRegistered(false))
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(processor -> {
                                    if (processor.hasResult()) {
                                      SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionEnterVerificationCode());
                                    } else if (processor.localRateLimit()) {
                                      Log.i(TAG, "Unable to request sms code due to local rate limit");
                                      SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionEnterVerificationCode());
                                    } else if (processor.captchaRequired()) {
                                      Log.i(TAG, "Unable to request sms code due to captcha required");
                                      SafeNavigation.safeNavigate(navController, EnterPhoneNumberFragmentDirections.actionRequestCaptcha());
                                    } else if (processor.rateLimit()) {
                                      Log.i(TAG, "Unable to request sms code due to rate limit");
                                      Toast.makeText(register.getContext(), R.string.RegistrationActivity_rate_limited_to_service, Toast.LENGTH_LONG).show();
                                    } else if (processor.isImpossibleNumber()) {
                                      Log.w(TAG, "Impossible number", processor.getError());
                                      Dialogs.showAlertDialog(requireContext(),
                                                              getString(R.string.RegistrationActivity_invalid_number),
                                                              String.format(getString(R.string.RegistrationActivity_the_number_you_specified_s_is_invalid), viewModel.getNumber().getFullFormattedNumber()));
                                    } else if (processor.isNonNormalizedNumber()) {
                                      handleNonNormalizedNumberError(processor.getOriginalNumber(), processor.getNormalizedNumber(), mode);
                                    } else {
                                      Log.i(TAG, "Unknown error during verification code request", processor.getError());
                                      Toast.makeText(register.getContext(), R.string.RegistrationActivity_unable_to_connect_to_service, Toast.LENGTH_LONG).show();
                                    }

                                    register.cancelSpinning();
                                    enableAllEntries();
                                  });

    disposables.add(request);
  }

  @Override
  public void onNumberFocused() {
    scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, register.getBottom()), 250);
  }

  @Override
  public void onNumberInputNext(@NonNull View view) {
    // Intentionally left blank
  }

  @Override
  public void onNumberInputDone(@NonNull View view) {
    ViewUtil.hideKeyboard(requireContext(), view);
    handleRegister(requireContext());
  }

  @Override
  public void onPickCountry(@NonNull View view) {
    SafeNavigation.safeNavigate(Navigation.findNavController(view), R.id.action_pickCountry);
  }

  @Override
  public void setNationalNumber(@NonNull String number) {
    viewModel.setNationalNumber(number);
  }

  @Override
  public void setCountry(int countryCode) {
    viewModel.onCountrySelected(null, countryCode);
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
            countryCode.setText(String.valueOf(phoneNumber.getCountryCode()));
            number.setText(String.valueOf(phoneNumber.getNationalNumber()));
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
        .setPositiveButton(R.string.RegistrationActivity_i_understand, (dialog1, which) -> handleRequestVerification(context, false))
        .setNegativeButton(android.R.string.cancel, null)
        .show();
  }

  protected final void confirmNumberPrompt(@NonNull Context context,
                                           @NonNull String e164number,
                                           @NonNull Runnable onConfirmed)
  {
    showConfirmNumberDialogIfTranslated(context,
                                        R.string.RegistrationActivity_a_verification_code_will_be_sent_to,
                                        e164number,
                                        () -> {
                                          ViewUtil.hideKeyboard(context, number.getInput());
                                          onConfirmed.run();
                                        },
                                        () -> number.focusAndMoveCursorToEndAndOpenKeyboard());
  }

  //**TM_SA**//START
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(MessageEvent event) {
    if (event.message != null) {
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
      confirmNumberPrompt(mContext, e164number, () -> handleRequestVerification(mContext, true));

      com.tm.logger.Log.d("SelfAuthenticator", "initOfficialSignalFirebaseAccount!!! ");
      FCMConnector.initOfficialSignalFirebaseAccount();

    }


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
