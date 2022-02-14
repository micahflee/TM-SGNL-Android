package org.tm.archive.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import io.reactivex.rxjava3.core.Observable
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.impl.NetworkConstraint

object InternetConnectionObserver {
  fun observe(): Observable<Boolean> = Observable.create {
    val application = ApplicationDependencies.getApplication()

    val observer = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (!it.isDisposed) {
          it.onNext(NetworkConstraint.isMet(application))
        }
      }
    }

    it.setCancellable { application.unregisterReceiver(observer) }
    application.registerReceiver(observer, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
  }
}
