package com.edvardas.basictasksapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
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
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
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
        return when(item.itemId) {
            R.id.menumain_showAbout -> {
                showAboutDialog()
                true
            }
            R.id.menumain_generate -> true
            R.id.menumain_settings -> true
            R.id.menumain_addTask -> {
                taskEditRequest(null)
                true
            }
            R.id.menumain_showDurations -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    fun showAboutDialog() {
        val messageView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
            .setIcon(R.mipmap.ic_launcher)
            .setView(messageView)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (this.dialog != null && this.dialog?.isShowing!!) {
                    this.dialog?.dismiss()
                }
            }
        dialog = builder.create()
        dialog?.setCanceledOnTouchOutside(true)
        val tv = messageView.findViewById<TextView>(R.id.about_version)
        tv.text = "v${BuildConfig.VERSION_NAME}"
        messageView.findViewById<TextView>(R.id.about_url)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val s = (it as TextView).text.toString()
            intent.data = Uri.parse(s)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No browser application found, cannot visit world-wide web", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onEditTask(task: Task?) {
        taskEditRequest(task)
    }

    override fun onDeleteTask(task: Task?) {
        val dialog = AppDialog()
        val args = Bundle()
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE)
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task?.id, task?.name))
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_delete_agree)
        args.putLong("TaskId", task?.id!!)
        dialog.arguments = args
        dialog.show(supportFragmentManager, null)
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG, "taskEditRequest: starts")
        if(twoPane) {
            Log.d(TAG, "taskEditRequest: In two pane mode (tablet)")
            val fragment = AddEditActivityFragment()
            val arguments = Bundle()
            arguments.putSerializable(Task::class.java.simpleName, task)
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(R.id.task_details_container, fragment)
                .commit()
        } else {
            Log.d(TAG, "taskEditRequest: In single pane mode")
            // TODO - Fix to show AddEditActivityFragment later!!!
            val intent = Intent()
            if (task != null) {
                intent.putExtra(Task::class.java.simpleName, task)
                startActivity(intent)
            } else {
                startActivity(intent)
            }
        }
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle?) {
        Log.d(TAG, "onPositiveDialogResult: called")
        when(dialogId) {
            DIALOG_ID_DELETE -> {
                val taskId = args?.getLong("TaskId")!!
                if (BuildConfig.DEBUG && taskId == 0.toLong()) {
                    throw AssertionError("Task ID is zero")
                }
                contentResolver?.delete(TasksMetaData.buildTaskUri(taskId), null, null)
            }
            DIALOG_ID_CANCEL_EDIT -> { /* No action required */ }
        }
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle?) {
        when (dialogId) {
            DIALOG_ID_DELETE -> { /* No action required */ }
            DIALOG_ID_CANCEL_EDIT -> finish()
        }
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: called")
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: start")
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container) as AddEditActivityFragment
        if (fragment.canClose) {
            super.onBackPressed()
        } else {
            val dialog = AppDialog()
            val args = Bundle()
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT)
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancel_editdiag_message))
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancel_editdiag_positive)
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancel_editdiag_negative)
            dialog.arguments = args
            dialog.show(supportFragmentManager, null)
        }
    }

    override fun onStop() {
        super.onStop()
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}