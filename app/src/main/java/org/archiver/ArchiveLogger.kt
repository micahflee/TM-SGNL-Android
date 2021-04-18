package org.archiver

import com.tm.logger.Log

class ArchiveLogger {

    companion object{

        const val LOGGER_TAG = "TMSignalArchive"

        fun sendArchiveLog(log : String){
            Log.d(LOGGER_TAG, log)
        }

    }
}