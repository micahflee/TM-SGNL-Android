package org.tm.archive.jobmanager.workmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.jobs.AttachmentDownloadJob;
import org.tm.archive.jobs.AttachmentUploadJob;
import org.tm.archive.jobs.AvatarGroupsV1DownloadJob;
import org.tm.archive.jobs.CleanPreKeysJob;
import org.tm.archive.jobs.CreateSignedPreKeyJob;
import org.tm.archive.jobs.DirectoryRefreshJob;
import org.tm.archive.jobs.FailingJob;
import org.tm.archive.jobs.FcmRefreshJob;
import org.tm.archive.jobs.LocalBackupJob;
import org.tm.archive.jobs.LocalBackupJobApi29;
import org.tm.archive.jobs.MmsDownloadJob;
import org.tm.archive.jobs.MmsReceiveJob;
import org.tm.archive.jobs.MmsSendJob;
import org.tm.archive.jobs.MultiDeviceBlockedUpdateJob;
import org.tm.archive.jobs.MultiDeviceConfigurationUpdateJob;
import org.tm.archive.jobs.MultiDeviceContactUpdateJob;
import org.tm.archive.jobs.MultiDeviceGroupUpdateJob;
import org.tm.archive.jobs.MultiDeviceProfileKeyUpdateJob;
import org.tm.archive.jobs.MultiDeviceReadUpdateJob;
import org.tm.archive.jobs.MultiDeviceVerifiedUpdateJob;
import org.tm.archive.jobs.PushDecryptMessageJob;
import org.tm.archive.jobs.PushGroupSendJob;
import org.tm.archive.jobs.PushGroupUpdateJob;
import org.tm.archive.jobs.PushMediaSendJob;
import org.tm.archive.jobs.PushNotificationReceiveJob;
import org.tm.archive.jobs.PushTextSendJob;
import org.tm.archive.jobs.RefreshAttributesJob;
import org.tm.archive.jobs.RefreshPreKeysJob;
import org.tm.archive.jobs.RequestGroupInfoJob;
import org.tm.archive.jobs.RetrieveProfileAvatarJob;
import org.tm.archive.jobs.RetrieveProfileJob;
import org.tm.archive.jobs.RotateCertificateJob;
import org.tm.archive.jobs.RotateProfileKeyJob;
import org.tm.archive.jobs.RotateSignedPreKeyJob;
import org.tm.archive.jobs.SendDeliveryReceiptJob;
import org.tm.archive.jobs.SendReadReceiptJob;
import org.tm.archive.jobs.SendViewedReceiptJob;
import org.tm.archive.jobs.ServiceOutageDetectionJob;
import org.tm.archive.jobs.SmsReceiveJob;
import org.tm.archive.jobs.SmsSendJob;
import org.tm.archive.jobs.SmsSentJob;
import org.tm.archive.jobs.TrimThreadJob;
import org.tm.archive.jobs.TypingSendJob;
import org.tm.archive.jobs.UpdateApkJob;

import java.util.HashMap;
import java.util.Map;

public class WorkManagerFactoryMappings {

  private static final Map<String, String> FACTORY_MAP = new HashMap<String, String>() {{
    put("AttachmentDownloadJob", AttachmentDownloadJob.KEY);
    put("AttachmentUploadJob", AttachmentUploadJob.KEY);
    put("AvatarDownloadJob", AvatarGroupsV1DownloadJob.KEY);
    put("CleanPreKeysJob", CleanPreKeysJob.KEY);
    put("CreateSignedPreKeyJob", CreateSignedPreKeyJob.KEY);
    put("DirectoryRefreshJob", DirectoryRefreshJob.KEY);
    put("FcmRefreshJob", FcmRefreshJob.KEY);
    put("LocalBackupJob", LocalBackupJob.KEY);
    put("LocalBackupJobApi29", LocalBackupJobApi29.KEY);
    put("MmsDownloadJob", MmsDownloadJob.KEY);
    put("MmsReceiveJob", MmsReceiveJob.KEY);
    put("MmsSendJob", MmsSendJob.KEY);
    put("MultiDeviceBlockedUpdateJob", MultiDeviceBlockedUpdateJob.KEY);
    put("MultiDeviceConfigurationUpdateJob", MultiDeviceConfigurationUpdateJob.KEY);
    put("MultiDeviceContactUpdateJob", MultiDeviceContactUpdateJob.KEY);
    put("MultiDeviceGroupUpdateJob", MultiDeviceGroupUpdateJob.KEY);
    put("MultiDeviceProfileKeyUpdateJob", MultiDeviceProfileKeyUpdateJob.KEY);
    put("MultiDeviceReadUpdateJob", MultiDeviceReadUpdateJob.KEY);
    put("MultiDeviceVerifiedUpdateJob", MultiDeviceVerifiedUpdateJob.KEY);
    put("PushContentReceiveJob", FailingJob.KEY);
    put("PushDecryptJob", PushDecryptMessageJob.KEY);
    put("PushGroupSendJob", PushGroupSendJob.KEY);
    put("PushGroupUpdateJob", PushGroupUpdateJob.KEY);
    put("PushMediaSendJob", PushMediaSendJob.KEY);
    put("PushNotificationReceiveJob", PushNotificationReceiveJob.KEY);
    put("PushTextSendJob", PushTextSendJob.KEY);
    put("RefreshAttributesJob", RefreshAttributesJob.KEY);
    put("RefreshPreKeysJob", RefreshPreKeysJob.KEY);
    put("RefreshUnidentifiedDeliveryAbilityJob", FailingJob.KEY);
    put("RequestGroupInfoJob", RequestGroupInfoJob.KEY);
    put("RetrieveProfileAvatarJob", RetrieveProfileAvatarJob.KEY);
    put("RetrieveProfileJob", RetrieveProfileJob.KEY);
    put("RotateCertificateJob", RotateCertificateJob.KEY);
    put("RotateProfileKeyJob", RotateProfileKeyJob.KEY);
    put("RotateSignedPreKeyJob", RotateSignedPreKeyJob.KEY);
    put("SendDeliveryReceiptJob", SendDeliveryReceiptJob.KEY);
    put("SendReadReceiptJob", SendReadReceiptJob.KEY);
    put("SendViewedReceiptJob", SendViewedReceiptJob.KEY);
    put("ServiceOutageDetectionJob", ServiceOutageDetectionJob.KEY);
    put("SmsReceiveJob", SmsReceiveJob.KEY);
    put("SmsSendJob", SmsSendJob.KEY);
    put("SmsSentJob", SmsSentJob.KEY);
    put("TrimThreadJob", TrimThreadJob.KEY);
    put("TypingSendJob", TypingSendJob.KEY);
    put("UpdateApkJob", UpdateApkJob.KEY);
  }};

  public static @Nullable String getFactoryKey(@NonNull String workManagerClass) {
    return FACTORY_MAP.get(workManagerClass);
  }
}
