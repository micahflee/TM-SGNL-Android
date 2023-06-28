package org.tm.archive.jobs

import org.signal.core.util.logging.Log
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.groups.GroupChangeBusyException
import org.tm.archive.groups.GroupChangeFailedException
import org.tm.archive.groups.GroupId
import org.tm.archive.groups.GroupManager
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobmanager.JsonJobData
import org.tm.archive.jobmanager.impl.NetworkConstraint
import org.tm.archive.recipients.Recipient
import java.io.IOException

/**
 * Leave a group. See [LeaveGroupV2Job] for more details on how this job should be enqueued.
 */
class LeaveGroupV2WorkerJob(parameters: Parameters, private val groupId: GroupId.V2) : BaseJob(parameters) {

  constructor(groupId: GroupId.V2) : this(
    parameters = Parameters.Builder()
      .setQueue(PushProcessMessageJob.getQueueName(Recipient.externalGroupExact(groupId).id))
      .addConstraint(NetworkConstraint.KEY)
      .setMaxAttempts(Parameters.UNLIMITED)
      .setMaxInstancesForQueue(2)
      .build(),
    groupId = groupId
  )

  override fun serialize(): ByteArray? {
    return JsonJobData.Builder()
      .putString(KEY_GROUP_ID, groupId.toString())
      .serialize()
  }

  override fun getFactoryKey(): String {
    return KEY
  }

  override fun onRun() {
    Log.i(TAG, "Attempting to leave group $groupId")

    val groupRecipient = Recipient.externalGroupExact(groupId)

    GroupManager.leaveGroup(context, groupId)

    val threadId = SignalDatabase.threads.getThreadIdIfExistsFor(groupRecipient.id)
    if (threadId != -1L) {
      SignalDatabase.recipients.setProfileSharing(groupRecipient.id, true)
      SignalDatabase.threads.setEntireThreadRead(threadId)
      SignalDatabase.threads.update(threadId, false, false)
      ApplicationDependencies.getMessageNotifier().updateNotification(context)
    }
  }

  override fun onShouldRetry(e: Exception): Boolean {
    return e is GroupChangeBusyException || e is GroupChangeFailedException || e is IOException
  }

  override fun onFailure() = Unit

  class Factory : Job.Factory<LeaveGroupV2WorkerJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): LeaveGroupV2WorkerJob {
      val data = JsonJobData.deserialize(serializedData)
      return LeaveGroupV2WorkerJob(parameters, GroupId.parseOrThrow(data.getString(KEY_GROUP_ID)).requireV2())
    }
  }

  companion object {
    const val KEY = "LeaveGroupWorkerJob"

    private val TAG = Log.tag(LeaveGroupV2WorkerJob::class.java)

    private const val KEY_GROUP_ID = "group_id"
  }
}
