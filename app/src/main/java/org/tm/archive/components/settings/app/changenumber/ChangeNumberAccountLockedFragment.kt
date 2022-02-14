package org.tm.archive.components.settings.app.changenumber

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import org.tm.archive.R
import org.tm.archive.registration.fragments.BaseAccountLockedFragment
import org.tm.archive.registration.viewmodel.BaseRegistrationViewModel

class ChangeNumberAccountLockedFragment : BaseAccountLockedFragment(R.layout.fragment_change_number_account_locked) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
  }

  override fun getViewModel(): BaseRegistrationViewModel {
    return ChangeNumberUtil.getViewModel(this)
  }

  override fun onNext() {
    findNavController().navigateUp()
  }
}
