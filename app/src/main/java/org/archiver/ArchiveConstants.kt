package org.archiver

class ArchiveConstants {

    companion object{
        const val SIGNAL_ARCHIVE_VERSION = "V1"


        const val signalTestUserName = "signal"
        const val signalTestPassword = "Aa!123456"

        const val signalCurrentPassword = ""/*"Aa123456"*/
        const val signalCurrentUser = "qasam"

        const val signalTestMobileNumber = "+972520123456"
        const val isTestMode = false
       // const val signalTestMobileNumber = "+447520619489"
        //const val signalTestMobileNumber = "+972520099696" //EnterP

        const val integration = "https://integration.telemessage.co.il"
        const val integrationKeeper = "https://api-gateway-integration.devops.telemessage.co.il"

      /*  const val charlieProduction = "https://rest.telemessage.com"
        const val prodKeeper = "https://archive.telemessage.com"*/

      const val charlieProduction = "https://integration.telemessage.co.il"
      const val prodKeeper = "https://api-gateway-integration.devops.telemessage.co.il"

        const val ARCHIVE_TYPE_APP_MESSAGE = "Signal message"
        const val ARCHIVE_TYPE_SMS = "SMS"

        const val ARCHIVE_SUBJECT_CHAT_GROUP = "chat group"

        const val ARCHIVE_SUBJECT_FROM_TEXT = "from"
        const val ARCHIVE_SUBJECT_TO_TEXT = "to"

        const val ARCHIVE_FILE_FOLDER_NAME = "aa_archiver"

        const val SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX = SIGNAL_ARCHIVE_VERSION + "_" + "Signal" + "_"

        const val SIGNAL_PART_PATH = "content://org.tm.archive/part/"
        const val SIGNAL_STICKER_PATH = "content://org.tm.archive/sticker/"
        const val SIGNAL_BLOB_PATH = "org.tm.archive.blob"

        const val isNeedToSetTeleMessageBackgroundAsDefault = true


      const val GENERATE_TOK_NAME = "logfile"
      const val GENERATE_TOK_PASS = "enRR8UVVywXYbFkqU#QDPRkO"
      //**TM_SA**// START
      const val SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY = "sharedPreferenceBaseURLKeyProduction"
      const val SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY = "sharedPreferenceBaseURLKeyKeeper"
      //**TM_SA**// END
    }

    enum class ProtocolType(val type: String) {
        ARCHIVE_PARAM_PROTOCOL_SEND("1"),
        ARCHIVE_PARAM_PROTOCOL_INBOX("0")
    }
}