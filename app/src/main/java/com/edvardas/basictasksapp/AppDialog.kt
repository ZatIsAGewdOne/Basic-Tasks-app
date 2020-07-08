package com.edvardas.basictasksapp

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment

class AppDialog : AppCompatDialogFragment() {
    companion object {
        private const val TAG = "AppDialog"
        const val DIALOG_ID = "id"
        const val DIALOG_MESSAGE = "message"
        const val DIALOG_POSITIVE_RID = "positive_rid"
        const val DIALOG_NEGATIVE_RID = "negative_rid"
    }

    private var dialogEvents: DialogEvents? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is DialogEvents) {
            throw ClassCastException("$context must implement AppDialog.DialogEvents interface")
        }
        dialogEvents = context
    }

    override fun onDetach() {
        super.onDetach()
        dialogEvents = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog: starts")
        val builder = AlertDialog.Builder(requireActivity())
        val dialogId: Int
        val messageString: String?
        var positiveStringId: Int
        var negativeStringId: Int
        if (arguments != null) {
            dialogId = arguments?.getInt(DIALOG_ID)!!
            messageString = arguments?.getString(DIALOG_MESSAGE)
            if (dialogId == 0 || messageString == null) {
                throw IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle")
            }
            positiveStringId = arguments?.getInt(DIALOG_POSITIVE_RID)!!
            if (positiveStringId == 0) {
                positiveStringId = R.string.ok
            }
            negativeStringId = arguments?.getInt(DIALOG_NEGATIVE_RID)!!
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel
            }
        } else {
            throw IllegalArgumentException("DIALOG_ID or DIALOG_MESSAGE is missing.")
        }

        builder.setMessage(messageString)
            .setPositiveButton(positiveStringId) { _, _ ->
            dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .setNegativeButton(negativeStringId) { _, _ ->
                dialogEvents?.onNegativeDialogResult(dialogId, arguments)
            }
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        Log.d(TAG, "onCancel: called")
        if (dialogEvents != null) {
            val dialogId = arguments?.getInt(DIALOG_ID)!!
            dialogEvents?.onDialogCancelled(dialogId)
        }
    }
}