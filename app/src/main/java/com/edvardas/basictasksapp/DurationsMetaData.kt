package com.edvardas.basictasksapp

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class DurationsMetaData {
    companion object {
        const val TABLE_NAME = "TaskDurations"
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"
        const val CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"
        @JvmStatic
        val CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTH_URI, TABLE_NAME)

        @JvmStatic
        fun getDurationId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }

    class Columns private constructor() {
        companion object {
            const val ID = BaseColumns._ID
            const val DURATIONS_NAME = TasksMetaData.Columns.TASKS_NAME
            const val DURATIONS_DESCRIPTION = TasksMetaData.Columns.TASKS_DESCRIPTION
            const val DURATIONS_START_TIME = TimingMetaData.Columns.TIMING_START_TIME
            const val DURATIONS_START_DATE = "StartDate"
            const val DURATIONS_DURATION = TimingMetaData.Columns.TIMING_DURATION
        }
    }
}