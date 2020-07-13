package com.edvardas.basictasksapp

import android.app.DatePickerDialog
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.security.InvalidParameterException
import java.util.*

class DurationsReport : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener, DialogEvents, View.OnClickListener {
    companion object {
        private const val TAG = "DurationsReport"
        private const val LOADER_ID = 1
        const val DIALOG_FILTER = 1
        const val DIALOG_DELETE = 2
        private const val SELECTION_PARAM = "SELECTION"
        private const val SELECTION_ARGS_PARAM = "SELECTION_ARGS"
        private const val SORT_ORDER_PARAM = "SORT_ORDER"
        const val DELETION_DATE = "DELETION_DATE"
        const val CURRENT_DATE = "CURRENT_DATE"
        const val DISPLAY_WEEK = "DISPLAY_WEEK"
    }

    private val args = Bundle()
    private var displayWeek = true
    private var durationsAdapter: DurationsAdapter? = null
    private val calendar = GregorianCalendar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_durations_report)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState != null) {
            val timeInMillis = savedInstanceState.getLong(CURRENT_DATE, 0)
            if (timeInMillis != 0L) {
                calendar.timeInMillis = timeInMillis
                calendar.clear(GregorianCalendar.HOUR_OF_DAY)
                calendar.clear(GregorianCalendar.MINUTE)
                calendar.clear(GregorianCalendar.SECOND)
            }
            displayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true)
        }

        applyFilter()
        val taskName = findViewById<TextView>(R.id.td_name_heading)
        taskName.setOnClickListener(this)

        val taskDesc = findViewById<TextView>(R.id.td_description_heading)
        taskDesc?.setOnClickListener(this)

        val taskDate = findViewById<TextView>(R.id.td_start_heading)
        taskDate.setOnClickListener(this)

        val taskDuration = findViewById<TextView>(R.id.td_duration_heading)
        taskDuration.setOnClickListener(this)

        val recyclerView = findViewById<RecyclerView>(R.id.td_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (durationsAdapter == null) {
            durationsAdapter = DurationsAdapter(this, null)
        }
        recyclerView.adapter = durationsAdapter
        LoaderManager.getInstance(this).initLoader(LOADER_ID, args, this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.td_name_heading -> args.putString(SORT_ORDER_PARAM, DurationsMetaData.Columns.DURATIONS_NAME)
            R.id.td_description_heading -> args.putString(SORT_ORDER_PARAM, DurationsMetaData.Columns.DURATIONS_DESCRIPTION)
            R.id.td_start_heading -> args.putString(SORT_ORDER_PARAM, DurationsMetaData.Columns.DURATIONS_START_DATE)
            R.id.td_duration_heading -> args.putString(SORT_ORDER_PARAM, DurationsMetaData.Columns.DURATIONS_DURATION)
        }
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(CURRENT_DATE, calendar.timeInMillis)
        outState.putBoolean(DISPLAY_WEEK, displayWeek)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.rm_filter_period -> {
                displayWeek = !displayWeek
                applyFilter()
                invalidateOptionsMenu()
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this)
                return true
            }
            R.id.rm_filter_date -> {
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER)
                return true
            }
            R.id.rm_delete -> {
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.rm_filter_period)
        if (item != null) {
            if (displayWeek) {
                item.setIcon(R.drawable.ic_filter_1_black_24dp)
                item.setTitle(R.string.rm_title_filter_day)
            } else {
                item.setIcon(R.drawable.ic_filter_7_black_24dp)
                item.setTitle(R.string.rm_title_filter_week)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showDatePickerDialog(title: String, dialogId: Int) {
        val dateFragment = DatePickerFragment()
        val arguments = Bundle()
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId)
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title)
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, calendar.time)
        dateFragment.arguments = arguments
        dateFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateId = view?.tag as Int
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        when(dateId) {
            DIALOG_FILTER -> {
                applyFilter()
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this)
            }
            DIALOG_DELETE -> {
                val fromDate = DateFormat.getDateFormat(this).format(calendar.timeInMillis)
                val dialog = AppDialog()
                val args = Bundle()
                args.putInt(AppDialog.DIALOG_ID, 1)
                args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.delete_timings_message, fromDate))
                args.putLong(DELETION_DATE, calendar.timeInMillis)
                dialog.arguments = args
                dialog.show(supportFragmentManager, null)
            }
            else -> throw IllegalArgumentException("Invalid data when getting DatePickerDialog result.")
        }
    }

    private fun deleteRecords(timeInMillis: Long) {
        val longDate = timeInMillis / 1000
        val selectionArgs = arrayOf(longDate.toString())
        val selection = "${TimingMetaData.Columns.TIMING_START_TIME} < ?"
        Log.d(TAG, "deleteRecords: Deleting records prior to $longDate")
        contentResolver.delete(TimingMetaData.CONTENT_URI, selection, selectionArgs)
        applyFilter()
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this)
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle?) {
        deleteRecords(args?.getLong(DELETION_DATE)!!)
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, this.args, this)
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle?) {

    }

    override fun onDialogCancelled(dialogId: Int) {

    }

    private fun applyFilter() {
        if (displayWeek) {
            val currentDate = calendar.time
            val dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK)
            val weekStart = calendar.firstDayOfWeek
            Log.d(TAG, "applyFilter: first day of calendar week is $weekStart")
            Log.d(TAG, "applyFilter: dayOfWeek is $dayOfWeek")
            Log.d(TAG, "applyFilter: date is ${calendar.time}")

            calendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart)
            val startDate = String.format(Locale.ITALY, "%04d-%02d-%02d",
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH) + 1,
                calendar.get(GregorianCalendar.DAY_OF_MONTH))
            calendar.add(GregorianCalendar.DATE, 6)
            val endDate = String.format(Locale.ITALY, "%04d-%02d-%02d",
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH) + 1,
                calendar.get(GregorianCalendar.DAY_OF_MONTH))

            val selectionArgs = arrayOf(startDate, endDate)
            calendar.time = currentDate

            Log.d(TAG, "applyFilter: startDate is $startDate and endDate is $endDate")

            args.putString(SELECTION_PARAM, "StartDate Between ? AND ?")
            args.putStringArray(SELECTION_PARAM, selectionArgs)
        } else {
            val startDate = String.format(Locale.ITALY, "%04d-%02d-%02d",
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH) + 1,
                calendar.get(GregorianCalendar.DAY_OF_MONTH))
            val selectionArgs = arrayOf(startDate)
            args.putString(SELECTION_PARAM, "StartDate = ?")
            args.putStringArray(SELECTION_ARGS_PARAM, selectionArgs)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id == LOADER_ID) {
            val projection = arrayOf(BaseColumns._ID, DurationsMetaData.Columns.DURATIONS_NAME,
                DurationsMetaData.Columns.DURATIONS_DESCRIPTION, DurationsMetaData.Columns.DURATIONS_START_TIME,
                DurationsMetaData.Columns.DURATIONS_START_DATE, DurationsMetaData.Columns.DURATIONS_DURATION)

            var selection: String? = null
            var selectionArgs: Array<String>? = null
            var sortOrder: String? = null

            if (args != null) {
                selection = args.getString(SELECTION_PARAM)
                selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM)
                sortOrder = args.getString(SORT_ORDER_PARAM)
            }
            return CursorLoader(
                this,
                DurationsMetaData.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder)
        } else {
            throw InvalidParameterException("${TAG}.onCreateLoader called with invalid loader id $id")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        durationsAdapter?.swapCursor(data)
        Log.d(TAG, "onLoadFinished: count is ${durationsAdapter?.itemCount}")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        durationsAdapter?.swapCursor(null)
    }
}