package com.edvardas.basictasksapp.debug

import android.content.ContentResolver
import com.edvardas.basictasksapp.TasksMetaData
import kotlin.math.roundToInt

class TestData {
    companion object {
        @JvmStatic
        fun generateTestData(contentResolver: ContentResolver?) {
            val SECONDS_IN_DAY = 86400
            val LOWER_BOUND = 100
            val UPPER_BOUND = 500
            val MAX_DURATION = SECONDS_IN_DAY / 6

            val projection = arrayOf(TasksMetaData.Columns.ID)
            val cursor = contentResolver?.query(
                TasksMetaData.CONTENT_URI,
                projection,
                null,
                null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val taskId = cursor.getLong(cursor.getColumnIndex(TasksMetaData.Columns.ID))
                    val loopCount = LOWER_BOUND + getRandomInt(UPPER_BOUND - LOWER_BOUND)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        @JvmStatic
        private fun getRandomInt(max: Int): Int {
            return (Math.random() * max).roundToInt()
        }
    }
}