package org.tm.archive.keyvalue

class TeleMessageMiscellaneousValues(store: KeyValueStore) : MiscellaneousValues(store) {

  override var isClientDeprecated: Boolean
    get() = false
    set(value) {}
}