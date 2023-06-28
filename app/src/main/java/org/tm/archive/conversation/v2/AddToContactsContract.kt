package org.tm.archive.conversation.v2

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.logging.Log
import org.tm.archive.R
import org.tm.archive.contactshare.Contact
import org.tm.archive.contactshare.ContactUtil
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.DirectoryRefreshJob

/**
 * Wraps up the "Add shared contact to contact list" into a contract. The flow here is a little
 * weird because buildAddToContactsIntent has to be run in the background (as it loads image data).
 *
 * Usage:
 * Register for result from your fragment, and then pass the created launcher in when you call
 * createIntentAndLaunch.
 */
class AddToContactsContract : ActivityResultContract<Intent, Unit>() {
  override fun createIntent(context: Context, input: Intent): Intent = input

  override fun parseResult(resultCode: Int, intent: Intent?) {
    ApplicationDependencies.getJobManager().add(DirectoryRefreshJob(false))
  }

  companion object {

    private val TAG = Log.tag(AddToContactsContract::class.java)

    @JvmStatic
    fun createIntentAndLaunch(
      fragment: Fragment,
      launcher: ActivityResultLauncher<Intent>,
      contact: Contact
    ): Disposable {
      return Single.fromCallable { ContactUtil.buildAddToContactsIntent(fragment.requireContext(), contact) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy {
          try {
            launcher.launch(it)
          } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Could not locate contacts activity", e)
            Toast.makeText(fragment.requireContext(), R.string.ConversationFragment__contacts_app_not_found, Toast.LENGTH_SHORT).show()
          }
        }
    }
  }
}
