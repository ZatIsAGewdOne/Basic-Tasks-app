package com.edvardas.basictasksapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

class AppProvider : ContentProvider() {
    var openHelper: AppDatabase? = null

    companion object {
        private const val TAG = "AppProvider"
        const val CONTENT_AUTH = "com.edvardas.basictasksapp.provider"
        @JvmStatic
        val CONTENT_AUTH_URI: Uri = Uri.parse("content://$CONTENT_AUTH")
        val uriMatcher = buildUriMatcher()

        private const val TASKS = 100
        private const val TASKS_ID = 101

        private const val TIMINGS = 200
        private const val TIMINGS_ID = 201

        private const val TASK_TIMINGS = 300;
        private const val TASK_TIMINGS_ID = 301;

        private const val TASK_DURATIONS = 400
        private const val TASK_DURATIONS_ID = 401

        @JvmStatic
        private fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(CONTENT_AUTH, TasksMetaData.TABLE_NAME, TASKS)
            matcher.addURI(CONTENT_AUTH, "${TasksMetaData.TABLE_NAME}/#", TASKS_ID)

//        matcher.addURI(CONTENT_AUTH, TasksMetaData.TABLE_NAME, TIMINGS)
//        matcher.addURI(CONTENT_AUTH, "${TasksMetaData.TABLE_NAME}/#", TIMINGS_ID)
//
//        matcher.addURI(CONTENT_AUTH, TasksMetaData.TABLE_NAME, TASK_DURATIONS)
//        matcher.addURI(CONTENT_AUTH, "${TasksMetaData.TABLE_NAME}/#", TASK_DURATIONS_ID)

            return matcher
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query: called with URI: $uri")
        val match = uriMatcher.match(uri)
        val queryBuilder = SQLiteQueryBuilder()

        when (match) {
            TASKS -> queryBuilder.tables = TasksMetaData.TABLE_NAME
            TASKS_ID -> {
                queryBuilder.tables = TasksMetaData.TABLE_NAME
                val taskId = TasksMetaData.getTaskId(uri)
                queryBuilder.appendWhere("${TasksMetaData.Column.ID} = $taskId")
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val db = openHelper?.readableDatabase
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query: rows in returned cursor = ${cursor.count}")

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun onCreate(): Boolean {
        openHelper = AppDatabase.getInstance(context!!)!!
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        return when(uriMatcher.match(uri)) {
            TASKS -> TasksMetaData.CONTENT_TYPE
            TASKS_ID -> TasksMetaData.CONTENT_TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
    }
}