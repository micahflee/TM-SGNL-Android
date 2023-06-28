package org.tm.archive.profiles.edit.pnp

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.tm.archive.util.rx.RxStore

class WhoCanSeeMyPhoneNumberViewModel : ViewModel() {

  private val repository = WhoCanSeeMyPhoneNumberRepository()
  private val store = RxStore(repository.getCurrentState())
  private val disposables = CompositeDisposable()

  val state: Flowable<WhoCanSeeMyPhoneNumberState> = store.stateFlowable.subscribeOn(AndroidSchedulers.mainThread())

  fun onEveryoneCanSeeMyPhoneNumberSelected() {
    store.update { WhoCanSeeMyPhoneNumberState.EVERYONE }
  }

  fun onNobodyCanSeeMyPhoneNumberSelected() {
    store.update { WhoCanSeeMyPhoneNumberState.NOBODY }
  }

  fun onSave(): Completable {
    return repository.onSave(store.state).observeOn(AndroidSchedulers.mainThread())
  }

  override fun onCleared() {
    disposables.clear()
    store.dispose()
  }
}
