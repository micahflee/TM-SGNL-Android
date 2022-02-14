package org.tm.archive.registration.fragments;

import androidx.lifecycle.ViewModelProvider;

import org.tm.archive.R;
import org.tm.archive.registration.viewmodel.BaseRegistrationViewModel;
import org.tm.archive.registration.viewmodel.RegistrationViewModel;

public class AccountLockedFragment extends BaseAccountLockedFragment {

  public AccountLockedFragment() {
    super(R.layout.account_locked_fragment);
  }

  @Override
  protected BaseRegistrationViewModel getViewModel() {
    return new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
  }

  @Override
  protected void onNext() {
    requireActivity().finish();
  }
}
