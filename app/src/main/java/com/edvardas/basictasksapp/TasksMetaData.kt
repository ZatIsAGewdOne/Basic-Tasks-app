package com.edvardas.basictasksapp

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

class TasksMetaData {
    companion object {
        const val TABLE_NAME = "Tasks"
        @JvmStatic
        val CONTENT_URI: Uri = Uri.withAppendedPath(AppProvider.CONTENT_AUTH_URI, TABLE_NAME)
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"
        const val CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.${AppProvider.CONTENT_AUTH}.${TABLE_NAME}"

        @JvmStatic
        fun buildTaskUri(taskId: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, taskId)
        }

        @JvmStatic
        fun getTaskId(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }

    // Column
    class Column private constructor() {
        companion object {
            const val ID = BaseColumns._ID
            const val TASKS_NAME = "name"
            const val TASKS_DESCRIPTION = "description"
            const val SORT_ORDER = "sort_order"
        }
    }
}