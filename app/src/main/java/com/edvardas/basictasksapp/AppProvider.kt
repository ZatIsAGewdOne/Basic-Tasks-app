package com.edvardas.basictasksapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
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
        @JvmStatic
        val uriMatcher = buildUriMatcher()

        private const val TASKS = 100
        private const val TASKS_ID = 101

        private const val TIMINGS = 200
        private const val TIMINGS_ID = 201

//        private const val TASK_TIMINGS = 300;
//        private const val TASK_TIMINGS_ID = 301;

        private const val TASK_DURATIONS = 400
        private const val TASK_DURATIONS_ID = 401

        @JvmStatic
        private fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(CONTENT_AUTH, TasksMetaData.TABLE_NAME, TASKS)
            matcher.addURI(CONTENT_AUTH, "${TasksMetaData.TABLE_NAME}/#", TASKS_ID)

            matcher.addURI(CONTENT_AUTH, DurationsMetaData.TABLE_NAME, TASK_DURATIONS)
            matcher.addURI(CONTENT_AUTH, "${DurationsMetaData.TABLE_NAME}/#", TASK_DURATIONS_ID)

            matcher.addURI(CONTENT_AUTH, TimingMetaData.TABLE_NAME, TIMINGS)
            matcher.addURI(CONTENT_AUTH, "${TimingMetaData.TABLE_NAME}/#", TIMINGS_ID)

            return matcher
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert: start")
        val match = uriMatcher.match(uri)
        val db: SQLiteDatabase
        val taskId: Long
        val uriReturned: Uri
        when (match) {
            TASKS -> {
                db = openHelper!!.writableDatabase
                taskId = db.insert(TasksMetaData.TABLE_NAME, null, values)
                if (taskId >= 0) {
                    uriReturned = TasksMetaData.buildTaskUri(taskId)
                } else {
                    throw SQLException("Failed to insert into $uri")
                }
            }

            TIMINGS -> {
                db = openHelper!!.writableDatabase
                taskId = db.insert(TimingMetaData.TABLE_NAME, null, values)
                if (taskId >= 0) {
                    uriReturned = TimingMetaData.buildTimingUri(taskId)
                } else {
                    throw SQLException("Failed to insert into $uri")
                }
            }

            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        if (taskId >= 0) {
            Log.d(TAG, "insert: setting notifyChanged with $uri")
            context?.contentResolver?.notifyChange(uri, null)
        } else {
            Log.d(TAG, "insert: noting inserted")
        }
        return uriReturned
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
                queryBuilder.appendWhere("${TasksMetaData.Columns.ID} = $taskId")
            }
            TIMINGS -> queryBuilder.tables = TimingMetaData.TABLE_NAME
            TIMINGS_ID -> {
                queryBuilder.tables = TimingMetaData.TABLE_NAME
                val timingId = TimingMetaData.getTimingId(uri)
                queryBuilder.appendWhere("${TimingMetaData.Columns.ID} = $timingId")
            }
            TASK_DURATIONS -> queryBuilder.tables = DurationsMetaData.TABLE_NAME
            TASK_DURATIONS_ID -> {
                queryBuilder.tables = DurationsMetaData.TABLE_NAME
                val durationId = DurationsMetaData.getDurationId(uri)
                queryBuilder.appendWhere("${DurationsMetaData.Columns.ID} = $durationId")
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
        val match = uriMatcher.match(uri)
        val db: SQLiteDatabase
        val count: Int
        var selectionCriteria: String
        when (match) {
            TASKS -> {
                db = openHelper!!.writableDatabase
                count = db.update(TasksMetaData.TABLE_NAME, values, selection, selectionArgs)
            }
            TASKS_ID -> {
                db = openHelper!!.writableDatabase
                val taskId = TasksMetaData.getTaskId(uri)
                selectionCriteria = "${TasksMetaData.Columns.ID} = $taskId"
                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TasksMetaData.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }
            TIMINGS -> {
                db = openHelper!!.writableDatabase
                count = db.update(TasksMetaData.TABLE_NAME, values, selection, selectionArgs)
            }
            TIMINGS_ID -> {
                db = openHelper!!.writableDatabase
                val timingId = TimingMetaData.getTimingId(uri)
                selectionCriteria = "${TasksMetaData.Columns.ID} = $timingId"
                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TimingMetaData.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }
        if (count > 0) {
            Log.d(TAG, "update: Setting notifyChange with $uri")
            context?.contentResolver?.notifyChange(uri, null)
        } else {
            Log.d(TAG, "update: nothing updated")
        }

        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "delete: update called with uri $uri")
        val match = uriMatcher.match(uri)
        val db: SQLiteDatabase
        val count: Int
        var selectionCriteria: String
        when (match) {
            TASKS -> {
                db = openHelper!!.writableDatabase
                count = db.delete(TasksMetaData.TABLE_NAME, selection, selectionArgs)
            }
            TASKS_ID -> {
                db = openHelper!!.writableDatabase
                val taskId = TasksMetaData.getTaskId(uri)
                selectionCriteria = "${TasksMetaData.Columns.ID} = $taskId"
                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TasksMetaData.TABLE_NAME, selectionCriteria, selectionArgs)
            }

            TIMINGS -> {
                db = openHelper!!.writableDatabase
                count = db.delete(TimingMetaData.TABLE_NAME, selection, selectionArgs)
            }

            TIMINGS_ID -> {
                db = openHelper!!.writableDatabase
                val timingId = TimingMetaData.getTimingId(uri)
                selectionCriteria = "${TimingMetaData.Columns.ID} = $timingId"
                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TimingMetaData.TABLE_NAME, selectionCriteria, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }
        if (count > 0) {
            Log.d(TAG, "delete: Setting notifyChange with $uri")
            context?.contentResolver?.notifyChange(uri, null)
        } else {
            Log.d(TAG, "delete: nothing deleted")
        }

        return count
    }

    override fun getType(uri: Uri): String? {
        return when(uriMatcher.match(uri)) {
            TASKS -> TasksMetaData.CONTENT_TYPE
            TASKS_ID -> TasksMetaData.CONTENT_TYPE_ITEM
            TIMINGS -> TimingMetaData.CONTENT_TYPE
            TIMINGS_ID -> TimingMetaData.CONTENT_TYPE_ITEM
            TASK_DURATIONS -> DurationsMetaData.CONTENT_TYPE
            TASK_DURATIONS_ID -> DurationsMetaData.CONTENT_TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
    }
}