/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.jobmanager

import org.tm.archive.jobmanager.impl.BackoffUtil
import org.tm.archive.util.FeatureFlags

/**
 * Helper to calculate the default backoff interval for a [Job] given it's run attempt count.
 */
fun Job.defaultBackoffInterval(): Long = BackoffUtil.exponentialBackoff(runAttempt + 1, FeatureFlags.getDefaultMaxBackoff())
