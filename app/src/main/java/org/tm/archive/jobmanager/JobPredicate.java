package org.tm.archive.jobmanager;

import androidx.annotation.NonNull;

import org.tm.archive.jobmanager.persistence.JobSpec;

public interface JobPredicate {
  JobPredicate NONE = jobSpec -> true;

  boolean shouldRun(@NonNull JobSpec jobSpec);
}
