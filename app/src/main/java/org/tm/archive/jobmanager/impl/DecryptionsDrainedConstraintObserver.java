package org.tm.archive.jobmanager.impl;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.ConstraintObserver;

/**
 * An observer for {@link DecryptionsDrainedConstraint}. Will fire when the websocket is drained and
 * the relevant decryptions have finished.
 */
public class DecryptionsDrainedConstraintObserver implements ConstraintObserver {

  private static final String REASON = Log.tag(DecryptionsDrainedConstraintObserver.class);

  @Override
  public void register(@NonNull Notifier notifier) {
    ApplicationDependencies.getIncomingMessageObserver().addDecryptionDrainedListener(() -> {
      notifier.onConstraintMet(REASON);
    });
  }
}
