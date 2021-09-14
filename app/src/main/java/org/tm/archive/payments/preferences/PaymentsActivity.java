package org.tm.archive.payments.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.tm.archive.PassphraseRequiredActivity;
import org.tm.archive.R;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.PaymentLedgerUpdateJob;
import org.tm.archive.util.DynamicNoActionBarTheme;
import org.tm.archive.util.DynamicTheme;

public class PaymentsActivity extends PassphraseRequiredActivity {

  public static final String EXTRA_PAYMENTS_STARTING_ACTION = "payments_starting_action";
  public static final String EXTRA_STARTING_ARGUMENTS       = "payments_starting_arguments";

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState, boolean ready) {
    dynamicTheme.onCreate(this);

    setContentView(R.layout.payments_activity);

    NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
    controller.setGraph(R.navigation.payments_preferences);

    int startingAction = getIntent().getIntExtra(EXTRA_PAYMENTS_STARTING_ACTION, R.id.paymentsHome);
    if (startingAction != R.id.paymentsHome) {
      controller.navigate(startingAction, getIntent().getBundleExtra(EXTRA_STARTING_ARGUMENTS));
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    dynamicTheme.onResume(this);

    ApplicationDependencies.getJobManager()
                           .add(PaymentLedgerUpdateJob.updateLedger());
  }
}
