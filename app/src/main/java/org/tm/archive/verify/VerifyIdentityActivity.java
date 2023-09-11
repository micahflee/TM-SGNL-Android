package org.tm.archive.verify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.signal.libsignal.protocol.IdentityKey;
import org.tm.archive.PassphraseRequiredActivity;
import org.tm.archive.R;
import org.tm.archive.crypto.IdentityKeyParcelable;
import org.tm.archive.database.IdentityTable;
import org.tm.archive.database.model.IdentityRecord;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.DynamicNoActionBarTheme;
import org.tm.archive.util.DynamicTheme;
import org.tm.archive.util.FeatureFlags;

/**
 * Activity for verifying identity keys.
 *
 * @author Moxie Marlinspike
 */
public class VerifyIdentityActivity extends PassphraseRequiredActivity {

  private static final String RECIPIENT_EXTRA = "recipient_id";
  private static final String IDENTITY_EXTRA  = "recipient_identity";
  private static final String VERIFIED_EXTRA  = "verified_state";

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  public static void startOrShowExchangeMessagesDialog(@NonNull Context context,
                                                       @Nullable IdentityRecord identityRecord) {
    if (identityRecord != null) {
      startOrShowExchangeMessagesDialog(context, identityRecord.getRecipientId(), identityRecord.getIdentityKey(), identityRecord.getVerifiedStatus() == IdentityTable.VerifiedStatus.VERIFIED);
    } else {
      showExchangeMessagesDialog(context);
    }
  }

  public static void startOrShowExchangeMessagesDialog(@NonNull Context context,
                                                       @NonNull IdentityRecord identityRecord,
                                                       boolean verified) {
    startOrShowExchangeMessagesDialog(context, identityRecord.getRecipientId(), identityRecord.getIdentityKey(), verified);
  }

  public static void startOrShowExchangeMessagesDialog(@NonNull Context context,
                                                         @NonNull RecipientId recipientId,
                                                         @NonNull IdentityKey identityKey,
                                                         boolean verified) {
    Recipient recipient = Recipient.live(recipientId).resolve();

    if (FeatureFlags.showAciSafetyNumberAsDefault()) {
      if (!recipient.hasServiceId()) {
        showExchangeMessagesDialog(context);
        return;
      }
    } else {
      if (!recipient.hasServiceId() || !recipient.hasE164()) {
        showExchangeMessagesDialog(context);
        return;
      }
    }

    context.startActivity(newIntent(context, recipientId, identityKey, verified));
  }

  private static void showExchangeMessagesDialog(@NonNull Context context) {
    new MaterialAlertDialogBuilder(context)
        .setMessage(R.string.VerifyIdentityActivity_dialog_exchange_messages_to_create_safety_number_message)
        .setPositiveButton(R.string.VerifyIdentityActivity_dialog_exchange_messages_to_create_safety_number_ok, null)
        .setNeutralButton(R.string.VerifyIdentityActivity_dialog_exchange_messages_to_create_safety_number_learn_more, (dialog, which) -> {
          CommunicationActions.openBrowserLink(context, "https://support.signal.org/hc/en-us/articles/360007060632");
        })
        .show();
  }

  public static Intent newIntent(@NonNull Context context,
                                 @NonNull IdentityRecord identityRecord)
  {
    return newIntent(context, identityRecord.getRecipientId(), identityRecord.getIdentityKey(), identityRecord.getVerifiedStatus() == IdentityTable.VerifiedStatus.VERIFIED);
  }

  public static Intent newIntent(@NonNull Context context,
                                 @NonNull RecipientId recipientId,
                                 @NonNull IdentityKey identityKey,
                                 boolean verified)
  {
    Intent intent = new Intent(context, VerifyIdentityActivity.class);

    intent.putExtra(RECIPIENT_EXTRA, recipientId);
    intent.putExtra(IDENTITY_EXTRA, new IdentityKeyParcelable(identityKey));
    intent.putExtra(VERIFIED_EXTRA, verified);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    super.onCreate(savedInstanceState, ready);
    dynamicTheme.onCreate(this);

    VerifyIdentityFragment fragment = VerifyIdentityFragment.create(
        getIntent().getParcelableExtra(RECIPIENT_EXTRA),
        getIntent().getParcelableExtra(IDENTITY_EXTRA),
        getIntent().getBooleanExtra(VERIFIED_EXTRA, false)
    );

    getSupportFragmentManager().beginTransaction()
                               .replace(android.R.id.content, fragment)
                               .commitAllowingStateLoss();
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }
}
