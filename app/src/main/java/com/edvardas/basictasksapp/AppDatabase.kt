package com.edvardas.basictasksapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val TAG = "AppDatabase"
        const val DATABASE_NAME = "Tasks.db"
        const val DATABASE_VERSION = 3
        private var instance: AppDatabase? = null

        /**
         * Singleton implementation for AppDatabase
         *
         * @param context the content provider context.
         * @return a SQLite database helper.
         */
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = AppDatabase(context)
            }
            return instance
        }
    }

    init {
        Log.d(TAG, "AppDatabase: created")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate: starts")
        val tableCreationQuery = "CREATE TABLE ${TasksMetaData.TABLE_NAME} " +
                "(${TasksMetaData.Columns.ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TasksMetaData.Columns.TASKS_NAME} TEXT NOT NULL, " +
                "${TasksMetaData.Columns.TASKS_DESCRIPTION} TEXT, " +
                "${TasksMetaData.Columns.SORT_ORDER} INTEGER);"
        Log.d(TAG, "onCreate: $tableCreationQuery")
        db?.execSQL(tableCreationQuery)
        addTimingsTable(db)
        addDurationsView(db)
        Log.d(TAG, "onCreate: ends")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: starts")
        when (oldVersion) {
            1 -> addTimingsTable(db)
            2 -> addDurationsView(db)
            else -> throw IllegalStateException("onUpgrade() with unknown newVersion: $newVersion")
        }
        Log.d(TAG, "onUpgrade: ends")
    }

    private fun addTimingsTable(db: SQLiteDatabase?) {
        val sql1 = "CREATE TABLE ${TimingMetaData.TABLE_NAME} " +
                "(${TimingMetaData.Columns.ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TimingMetaData.Columns.TIMING_TASK_ID} INTEGER NOT NULL, " +
                "${TimingMetaData.Columns.TIMING_START_TIME} INTEGER, " +
                "${TimingMetaData.Columns.TIMING_DURATION} INTEGER);"
        db?.execSQL(sql1)

        val sql2 = "CREATE TRIGGER Remove_Task " +
                "AFTER DELETE ON ${TasksMetaData.TABLE_NAME} " +
                "FOR EACH ROW " +
                "BEGIN DELETE FROM ${TimingMetaData.TABLE_NAME} " +
                "WHERE ${TimingMetaData.Columns.TIMING_TASK_ID} = OLD.${TasksMetaData.Columns.ID}; " +
                "END;"
        db?.execSQL(sql2)
    }

    private fun addDurationsView(db: SQLiteDatabase?) {
        /*
        * CREATE VIEW TaskDurations AS
        * SELECT Timing.id, Task.name, Task.description, Timing.startTime,
        * DATE(Timing.startTime, 'unixepoch') AS StartDate
        * SUM(Timing.duration) AS Duration
        * FROM Tasks INNER JOIN Timings
        * ON Task.id = Timing.taskId
        * GROUP BY Task.id, StartDate
        * */
        val sql = "CREATE VIEW ${DurationsMetaData.TABLE_NAME} AS " +
                "SELECT ${TimingMetaData.TABLE_NAME}.${TimingMetaData.Columns.ID}, " +
                "${TasksMetaData.TABLE_NAME}.${TasksMetaData.Columns.TASKS_NAME}, " +
                "${TasksMetaData.TABLE_NAME}.${TasksMetaData.Columns.TASKS_DESCRIPTION}, " +
                "${TimingMetaData.TABLE_NAME}.${TimingMetaData.Columns.TIMING_START_TIME}, " +
                "DATE(${TimingMetaData.TABLE_NAME}.${TimingMetaData.Columns.TIMING_START_TIME}, 'unixepoch') AS " +
                "${DurationsMetaData.Columns.DURATIONS_START_DATE}, " +
                "SUM(${TimingMetaData.TABLE_NAME}.${TimingMetaData.Columns.TIMING_DURATION}) AS " +
                "${DurationsMetaData.Columns.DURATIONS_DURATION} FROM ${TasksMetaData.TABLE_NAME} " +
                "JOIN ${TimingMetaData.TABLE_NAME} ON ${TasksMetaData.TABLE_NAME}.${TasksMetaData.Columns.ID} = ${TimingMetaData.TABLE_NAME}.${TimingMetaData.Columns.TIMING_TASK_ID} " +
                "GROUP BY ${DurationsMetaData.Columns.DURATIONS_START_TIME}, ${DurationsMetaData.Columns.DURATIONS_NAME};"
        db?.execSQL(sql)
    }
}