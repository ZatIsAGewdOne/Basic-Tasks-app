package com.edvardas.basictasksapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem

class AddEditActivity : AppCompatActivity(), OnSaveClicked, DialogEvents {
    companion object {
        private const val TAG = "AddEditActivity"
        const val DIALOG_ID_CANCEL_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val fragment = AddEditActivityFragment()
        fragment.arguments = intent.extras
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected: home button pressed")
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as AddEditActivityFragment
            return if (fragment.canClose) {
                super.onOptionsItemSelected(item)
            } else {
                showConfirmationDialog()
                false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveClicked() {
        finish()
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle?) {
        Log.d(TAG, "onPositiveDialogResult: called")
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle?) {
        Log.d(TAG, "onNegativeDialogResult: called")
        finish()
    }

    override fun onDialogCancelled(dialogId: Int) {
        Log.d(TAG, "onDialogCancelled: called")
    }

    private fun showConfirmationDialog() {
        val dialog = AppDialog()
        val args = Bundle()
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT)
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancel_editdiag_message))
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancel_editdiag_positive)
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancel_editdiag_negative)

        dialog.arguments = args
        dialog.show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: called")
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as AddEditActivityFragment
        if (fragment.canClose) super.onBackPressed() else showConfirmationDialog()
    }
}