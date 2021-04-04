package org.tm.archive.preferences;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import org.signal.core.util.logging.Log;
import org.tm.archive.ApplicationPreferencesActivity;
import org.tm.archive.R;
import org.tm.archive.components.SwitchPreferenceCompat;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.RefreshAttributesJob;
import org.tm.archive.jobs.RefreshOwnProfileJob;
import org.tm.archive.jobs.RemoteConfigRefreshJob;
import org.tm.archive.jobs.RotateProfileKeyJob;
import org.tm.archive.jobs.StorageForcePushJob;
import org.tm.archive.keyvalue.InternalValues;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.util.ConversationUtil;

public class InternalOptionsPreferenceFragment extends CorrectedPreferenceFragment {
  private static final String TAG = Log.tag(InternalOptionsPreferenceFragment.class);

  @Override
  public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
  }

  @Override
  public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.preferences_internal);

    PreferenceDataStore preferenceDataStore = SignalStore.getPreferenceDataStore();

    initializeSwitchPreference(preferenceDataStore, InternalValues.RECIPIENT_DETAILS, SignalStore.internalValues().recipientDetails());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_DO_NOT_CREATE_GV2, SignalStore.internalValues().gv2DoNotCreateGv2Groups());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_FORCE_INVITES, SignalStore.internalValues().gv2ForceInvites());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_IGNORE_SERVER_CHANGES, SignalStore.internalValues().gv2IgnoreServerChanges());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_IGNORE_P2P_CHANGES, SignalStore.internalValues().gv2IgnoreP2PChanges());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_DISABLE_AUTOMIGRATE_INITIATION, SignalStore.internalValues().disableGv1AutoMigrateInitiation());
    initializeSwitchPreference(preferenceDataStore, InternalValues.GV2_DISABLE_AUTOMIGRATE_NOTIFICATION, SignalStore.internalValues().disableGv1AutoMigrateNotification());
    initializeSwitchPreference(preferenceDataStore, InternalValues.FORCE_CENSORSHIP, SignalStore.internalValues().forcedCensorship());

    findPreference("pref_refresh_attributes").setOnPreferenceClickListener(preference -> {
      ApplicationDependencies.getJobManager()
                             .startChain(new RefreshAttributesJob())
                             .then(new RefreshOwnProfileJob())
                             .enqueue();
      Toast.makeText(getContext(), "Scheduled attribute refresh", Toast.LENGTH_SHORT).show();
      return true;
    });

    findPreference("pref_rotate_profile_key").setOnPreferenceClickListener(preference -> {
      ApplicationDependencies.getJobManager().add(new RotateProfileKeyJob());
      Toast.makeText(getContext(), "Scheduled profile key rotation", Toast.LENGTH_SHORT).show();
      return true;
    });

    findPreference("pref_refresh_remote_values").setOnPreferenceClickListener(preference -> {
      ApplicationDependencies.getJobManager().add(new RemoteConfigRefreshJob());
      Toast.makeText(getContext(), "Scheduled remote config refresh", Toast.LENGTH_SHORT).show();
      return true;
    });

    findPreference("pref_force_send").setOnPreferenceClickListener(preference -> {
      ApplicationDependencies.getJobManager().add(new StorageForcePushJob());
      Toast.makeText(getContext(), "Scheduled storage force push", Toast.LENGTH_SHORT).show();
      return true;
    });

    findPreference("pref_delete_dynamic_shortcuts").setOnPreferenceClickListener(preference -> {
      ConversationUtil.clearAllShortcuts(requireContext());
      Toast.makeText(getContext(), "Deleted all dynamic shortcuts.", Toast.LENGTH_SHORT).show();
      return true;
    });
  }

  private void initializeSwitchPreference(@NonNull PreferenceDataStore preferenceDataStore,
                                          @NonNull String key,
                                          boolean checked)
  {
    SwitchPreferenceCompat forceGv2Preference = (SwitchPreferenceCompat) findPreference(key);
    forceGv2Preference.setPreferenceDataStore(preferenceDataStore);
    forceGv2Preference.setChecked(checked);
  }

  @Override
  public void onResume() {
    super.onResume();
    //noinspection ConstantConditions
    ((ApplicationPreferencesActivity) getActivity()).getSupportActionBar().setTitle(R.string.preferences__internal_preferences);
  }
}
