package org.selfAuthentication

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import org.tm.archive.R

class ProgressDialog {
  companion object {
    fun progressDialog(context: Context): Dialog {
      val dialog = Dialog(context)
      val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
      dialog.setContentView(inflate)
      dialog.setCancelable(false)
      dialog.window!!.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
      )
      return dialog
    }
  }
}