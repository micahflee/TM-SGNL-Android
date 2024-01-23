package org.archiver

import android.util.Log


class ArchiveLogger {

    companion object{

        const val LOGGER_TAG = "TMSignalArchive"

        fun sendArchiveLog(log : String){
            Log.d(LOGGER_TAG, log)
        }

    }
}