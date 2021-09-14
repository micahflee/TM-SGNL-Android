package org.tm.archive.jobs;

import org.signal.core.util.logging.Log;
import org.tm.archive.jobmanager.Job;

public abstract class PushReceivedJob extends BaseJob {

  private static final String TAG = Log.tag(PushReceivedJob.class);


  protected PushReceivedJob(Job.Parameters parameters) {
    super(parameters);
  }

}
