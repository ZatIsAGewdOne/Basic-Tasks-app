package com.edvardas.basictasksapp

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class TimingMetaData {
    companion object {
        const val TABLE_NAME = "Timings"
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"
        const val CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"
        @JvmStatic
        val CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTH_URI, TABLE_NAME)

        @JvmStatic
        fun buildTimingUri(timingId: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, timingId)
        }

        @JvmStatic
        fun getTimingId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }

    class Columns private constructor() {
        companion object {
            const val ID = BaseColumns._ID
            const val TIMING_TASK_ID = "TaskId"
            const val TIMING_START_TIME = "StartTime"
            const val TIMING_DURATION = "Duration"
        }
    }
}