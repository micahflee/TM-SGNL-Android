package org.tm.archive.jobs;

import org.tm.archive.jobmanager.Job;

public abstract class PushReceivedJob extends BaseJob {

  private static final String TAG = PushReceivedJob.class.getSimpleName();


  protected PushReceivedJob(Job.Parameters parameters) {
    super(parameters);
  }

}
