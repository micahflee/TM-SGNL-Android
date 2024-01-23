package org.selfAuthentication

import android.app.Activity
import android.os.Build

class AuthenticationUtils {

  companion object{
    fun forceCloseApplication(aContext: Activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        aContext.finishAndRemoveTask()
      } else {
        aContext.finishAffinity()
      }
    }
  }
}