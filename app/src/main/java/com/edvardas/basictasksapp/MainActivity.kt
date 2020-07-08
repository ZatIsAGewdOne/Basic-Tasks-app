package com.edvardas.basictasksapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnSaveClicked, DialogEvents {
    private var twoPane = false
    private var dialog: AlertDialog? = null
    companion object {
        private const val TAG = "MainActivity"
        const val DIALOG_ID_DELETE = 1
        const val DIALOG_ID_CANCEL_EDIT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (findViewById<FrameLayout>(R.id.task_details_container) != null) {
            twoPane = true
        }
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts")
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

    @SuppressLint("SetTextI18n")
    fun showAboutDialog() {
        TODO()
    }

    override fun onEditTask(task: Task?) {
        TODO("Not yet implemented")
    }

    override fun onDeleteTask(task: Task?) {
        TODO("Not yet implemented")
    }

    private fun taskEditRequest(task: Task?) {
        TODO()
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: called")
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}