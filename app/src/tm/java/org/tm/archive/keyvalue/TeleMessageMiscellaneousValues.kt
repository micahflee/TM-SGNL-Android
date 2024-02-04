package org.tm.archive.keyvalue

class TeleMessageMiscellaneousValues(store: KeyValueStore) : MiscellaneousValues(store) {

  override fun isClientDeprecated(): Boolean = false
}