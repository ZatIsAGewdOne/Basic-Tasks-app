package com.edvardas.basictasksapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    companion object {
        private const val TAG = "DatePickerFragment"
        const val DATE_PICKER_ID = "ID"
        const val DATE_PICKER_DATE = "DATE"
        const val DATE_PICKER_TITLE = "TITLE"
    }

    var dialogId = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = GregorianCalendar()
        var title: String? = null
        if (arguments != null) {
            dialogId = arguments?.getInt(DATE_PICKER_ID)!!
            title = arguments?.getString(DATE_PICKER_TITLE)
            val givenDate = arguments?.getSerializable(DATE_PICKER_DATE) as Date?
            if (givenDate != null) {
                calendar.time = givenDate
                Log.d(TAG, "onCreateDialog: retrieved date: $givenDate")
            }
        }

        val year = calendar.get(GregorianCalendar.YEAR)
        val month = calendar.get(GregorianCalendar.MONTH)
        val day = calendar.get(GregorianCalendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(requireContext(), this, year, month, day)
        if (title != null) {
            dialog.setTitle(title)
        }
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is DatePickerDialog.OnDateSetListener) {
            throw ClassCastException("$context must implement DatePickerDialog.OnDateSetListener interface")
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG, "onDateSet: starts")
        val listener = activity as DatePickerDialog.OnDateSetListener?
        if (listener != null) {
            view?.tag = dialogId
            listener.onDateSet(view, year, month, dayOfMonth)
        }
        Log.d(TAG, "onDateSet: ends")
    }
}