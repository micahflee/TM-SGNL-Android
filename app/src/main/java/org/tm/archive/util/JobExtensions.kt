/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.util

import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobmanager.JobManager

/** Starts a new chain with this job. */
fun Job.asChain(): JobManager.Chain {
  return ApplicationDependencies.getJobManager().startChain(this)
}
