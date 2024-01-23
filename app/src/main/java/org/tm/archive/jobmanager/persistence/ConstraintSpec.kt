package org.tm.archive.jobmanager.persistence

data class ConstraintSpec(
  val jobSpecId: String,
  val factoryKey: String,
  val isMemoryOnly: Boolean
) {
  override fun toString(): String {
    return "jobSpecId: JOB::$jobSpecId | factoryKey: $factoryKey | memoryOnly: $isMemoryOnly"
  }
}
