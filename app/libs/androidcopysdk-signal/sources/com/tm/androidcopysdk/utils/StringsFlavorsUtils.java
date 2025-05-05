package com.tm.androidcopysdk.utils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/StringsFlavorsUtils.class */
public class StringsFlavorsUtils {
    public static final String EVENTS_RECORDING_START = "Recording Calls Enabled";
    public static final String EVENTS_RECORDING_STOP = "Recording Calls Disabled";
    public static final String EVENTS_HEARTBEAT = "Android Archiver heartbeat";
    public static final String EVENTS_INSTALLATION = "Android Archiver App Version Installed (%s)";
    public static final String EVENTS_INSTALLATION_UPGRADE = "Android Archiver App Version Updated (%s)";
    public static final String EVENTS_LOW_STORAGE = "The device has reached minimum internal storage threshold.";
    public static final String EVENTS_SIM_CARD_CHANGE = "SIM Card was removed";
    public static final String EVENTS_SIM_CARD_BACK = "SIM Card was Restored";
    public static final String EVENTS_DUAL_SIM_CARD_BACK = "dual sim was change";
    public static final String EVENTS_UPDATE = "Android Archiver App Version Updated (%s)";
    public static final String EVENTS_SUBJECT = "Android Archiver System Event";
    public static final String FAILED_RECORDING_EVENT_TEXT = "Call Recording Failed";
    public static final String CHANEL_NAME = "Notification channel";
    public static final String CHANEL_GENERAL = "General channel";
    public static final String CHANEL_NAME_ARCH = "Archiver Notification channel";
    public static final String CHANEL_NAME_BACKUP = "Backup Notification channel";
    public static final String CHANEL_ID = "channel01";
    public static final String CHANEL_ID_BACKUP_SERVICE = "channel03";
    public static final String CHANEL_ID_SIGN_OUT = "channel04";
    public static final String CHANEL_ID_GENERAL = "channel05";
    public static final String SMS_MMS = "SMS/MMS";
    public static final String PHONE = "Phone";
    public static final String CALLLOGS = "Call logs";
    public static final String STORAGE = "Storage";
    public static final String NOTIFICATION_TITLE = "WPA archiver";
    public static final String ARCHIVER_MESSAGE_TITLE = "WhatsApp";
    public static final String NOTIFICATION_TEXT_FOR_BACKUP_SERVICE = "Enabled";
    public static final String MULTIPLE_OUTGOING_TYPE = "Outgoing Call";
    public static final String MULTIPLE_INCOMING_TYPE = "Incoming Call";
    public static final String NOTIFICATION_AUTO_RECORDING_IS_OFF = "Automatic call recording setting is turned OFF. Read more ";
    public static final String NOTIFICATION_AUTO_RECORDING_IS_OFF_URL = "https://www.telemessage.com/android-archiver-capturing-native-call-recording/";
    public static final String NOTIFICATION_RECORDING_WAS_TERMINATED = "Call recording was manually stopped during the call. Read more";
    public static final String NOTIFICATION_RECORDING_WAS_TERMINATED_URL = "https://www.telemessage.com/why-did-i-get-call-recording-was-manually-stopped-during-the-call-notification/";
    public static final String RECORDED_ACTIVITY_TEXT_SDK = "Select the folder in which your native phone application stores call recording. Open the native phone application , select Settings and look for the recorded calls folder.";
    public static final String COUNTRY_CODE_ERROR_DIALOG = "International number?";
    public static final String LOG_REPORT_SENT = "Log report sent";
    public static final String LOG_REPORT_NO_NETWORK = "Network is not available";
    public static final String PRIVACY_POLICY_DIALOG_MESSAGE = "TeleMessage Android  Archiver archives messages and calls in your company archive. \\nAndroid Archiver collects your address book data in order to display the contacts names when archiving communication with your company. \\nDo you allow the app to use your contacts names ?";

    public static boolean isWPAApp() {
        return FlavorSettings.getInstance().getAccountType().equals("androidcopy.tm.com.wpa");
    }
}
