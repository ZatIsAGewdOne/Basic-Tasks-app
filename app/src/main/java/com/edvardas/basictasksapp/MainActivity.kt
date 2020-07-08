package com.edvardas.basictasksapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val projection = arrayOf(TasksMetaData.Column.TASKS_NAME, TasksMetaData.Column.TASKS_DESCRIPTION)
        val cursor = contentResolver.query(TasksMetaData.CONTENT_URI, projection, null, null, TasksMetaData.Column.TASKS_NAME)
        if (cursor != null) {
            while(cursor.moveToNext()) {
                for(i in 0..cursor.columnCount) {
                    Log.d(TAG, "onCreate: ${cursor.getColumnName(i)} -> ${cursor.getString(i)}")
                }
                Log.d(TAG, "onCreate: ==================================")
            }
            cursor.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menumain_showAbout -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}