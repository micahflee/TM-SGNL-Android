package org.tm.archive.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import org.signal.core.ui.theme.SignalTheme
import org.tm.archive.components.FixedRoundedCornerBottomSheetDialogFragment
import org.tm.archive.util.DynamicTheme

abstract class ComposeBottomSheetDialogFragment : FixedRoundedCornerBottomSheetDialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return ComposeView(requireContext()).apply {
      setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
      setContent {
        SignalTheme(
          isDarkMode = DynamicTheme.isDarkTheme(LocalContext.current)
        ) {
          Surface(shape = RoundedCornerShape(18.dp, 18.dp)) {
            SheetContent()
          }
        }
      }
    }
  }

  @Composable
  abstract fun SheetContent()
}
