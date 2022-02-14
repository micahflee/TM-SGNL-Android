package org.tm.archive.devicetransfer.newdevice;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import org.greenrobot.eventbus.EventBus;
import org.signal.devicetransfer.TransferStatus;
import org.tm.archive.LoggingFragment;
import org.tm.archive.R;
import org.tm.archive.util.navigation.SafeNavigation;

/**
 * Shows instructions for new device to being transfer.
 */
public final class NewDeviceTransferInstructionsFragment extends LoggingFragment {
  public NewDeviceTransferInstructionsFragment() {
    super(R.layout.new_device_transfer_instructions_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    view.findViewById(R.id.new_device_transfer_instructions_fragment_continue)
        .setOnClickListener(v -> SafeNavigation.safeNavigate(Navigation.findNavController(v), R.id.action_device_transfer_setup));
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().removeStickyEvent(TransferStatus.class);
  }
}
