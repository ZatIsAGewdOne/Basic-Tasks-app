package com.edvardas.basictasksapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val TAG = "AppDatabase"
        const val DATABASE_NAME = "Tasks.db"
        const val DATABASE_VERSION = 1
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
                "(${TasksMetaData.Column.ID} INTEGER PRIMARY KEY NOT NULL, " +
                "${TasksMetaData.Column.TASKS_NAME} TEXT NOT NULL, " +
                "${TasksMetaData.Column.TASKS_DESCRIPTION} TEXT, " +
                "${TasksMetaData.Column.SORT_ORDER} INTEGER);"
        Log.d(TAG, "onCreate: $tableCreationQuery")
        db?.execSQL(tableCreationQuery)
        Log.d(TAG, "onCreate: ends")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: starts")
        when (oldVersion) {
            1 -> {}
            else -> throw IllegalStateException("onUpgrade() with unknown newVersion: $newVersion")
        }
        Log.d(TAG, "onUpgrade: ends")
    }
}