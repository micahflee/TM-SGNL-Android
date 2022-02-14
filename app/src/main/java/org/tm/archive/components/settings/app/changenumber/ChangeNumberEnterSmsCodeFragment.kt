package org.tm.archive.components.settings.app.changenumber

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import org.tm.archive.R
import org.tm.archive.components.settings.app.changenumber.ChangeNumberUtil.changeNumberSuccess
import org.tm.archive.components.settings.app.changenumber.ChangeNumberUtil.getCaptchaArguments
import org.tm.archive.components.settings.app.changenumber.ChangeNumberUtil.getViewModel
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.registration.fragments.BaseEnterSmsCodeFragment
import org.tm.archive.util.navigation.safeNavigate

class ChangeNumberEnterSmsCodeFragment : BaseEnterSmsCodeFragment<ChangeNumberViewModel>(R.layout.fragment_change_number_enter_code) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar: Toolbar = view.findViewById(R.id.toolbar)
    toolbar.title = viewModel.number.fullFormattedNumber
    toolbar.setNavigationOnClickListener { navigateUp() }

    view.findViewById<View>(R.id.verify_header).setOnClickListener(null)

    requireActivity().onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          navigateUp()
        }
      }
    )
  }

  private fun navigateUp() {
    if (SignalStore.misc().isChangeNumberLocked) {
      startActivity(ChangeNumberLockActivity.createIntent(requireContext()))
    } else {
      findNavController().navigateUp()
    }
  }

  override fun getViewModel(): ChangeNumberViewModel {
    return getViewModel(this)
  }

  override fun handleSuccessfulVerify() {
    displaySuccess { changeNumberSuccess() }
  }

  override fun navigateToCaptcha() {
    findNavController().safeNavigate(R.id.action_changeNumberEnterCodeFragment_to_captchaFragment, getCaptchaArguments())
  }

  override fun navigateToRegistrationLock(timeRemaining: Long) {
    findNavController().safeNavigate(ChangeNumberEnterSmsCodeFragmentDirections.actionChangeNumberEnterCodeFragmentToChangeNumberRegistrationLock(timeRemaining))
  }

  override fun navigateToKbsAccountLocked() {
    findNavController().safeNavigate(ChangeNumberEnterSmsCodeFragmentDirections.actionChangeNumberEnterCodeFragmentToChangeNumberAccountLocked())
  }
}
