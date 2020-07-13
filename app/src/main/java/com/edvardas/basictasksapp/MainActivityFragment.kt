package com.edvardas.basictasksapp

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.security.InvalidParameterException

class MainActivityFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, OnTaskClickListener {
    companion object {
        private const val TAG = "MainActivityFragment"
        const val LOADER_ID = 0
    }

    private var adapter: CursorRecyclerViewAdapter? = null
    private var timing: Timing? = null

    init {
        Log.d(TAG, "MainActivityFragment: starts")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)
        if (activity !is OnTaskClickListener) {
            throw ClassCastException("$activity must implement OnTaskClickListener interface")
        }
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
        setTimingText(timing)
    }

    private fun setTimingText(timing: Timing?) {
        val taskName = activity?.findViewById<TextView>(R.id.current_task)
        if (timing != null) {
            taskName?.text = getString(R.string.current_timing_text, timing.task.name)
        } else {
            taskName?.setText(R.string.no_task_msg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        val view =  inflater.inflate(R.layout.fragment_main, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.task_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (this.adapter == null) {
            this.adapter = CursorRecyclerViewAdapter(null, this)
        }
        recyclerView.adapter = this.adapter
        Log.d(TAG, "onCreateView: ends")
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader: starts")
        val projection = arrayOf(TasksMetaData.Columns.ID, TasksMetaData.Columns.TASKS_NAME,
            TasksMetaData.Columns.TASKS_DESCRIPTION, TasksMetaData.Columns.SORT_ORDER)
        val sortOrder = "${TasksMetaData.Columns.SORT_ORDER},${TasksMetaData.Columns.TASKS_NAME} COLLATE NOCASE"
        if (id == LOADER_ID) {
            return CursorLoader(requireActivity(), TasksMetaData.CONTENT_URI, projection, null, null, sortOrder)
        } else {
            throw InvalidParameterException("${TAG}.onCreateLoader called with invalid id $id")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "onLoadFinished: entered")
        this.adapter?.swapCursor(data)
        Log.d(TAG, "onLoadFinished: count is ${this.adapter?.itemCount}")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset: called")
        this.adapter?.swapCursor(null)
    }

    override fun onEditTask(task: Task) {
        Log.d(TAG, "onEditTask: called")
        (activity as OnTaskClickListener?)?.onEditTask(task)
    }

    override fun onDeleteTask(task: Task) {
        Log.d(TAG, "onDeleteTask: called")
        (activity as OnTaskClickListener?)?.onDeleteTask(task)
    }

    override fun onTaskLongTap(task: Task) {
        if (timing != null) {
            if (task.id == timing?.task?.id) {
                saveTiming(timing!!)
                timing = null
                setTimingText(null)
            } else {
                saveTiming(timing!!)
                timing = Timing(task)
                setTimingText(timing)
            }
        } else {
            timing = Timing(task)
            setTimingText(timing)
        }
    }

    private fun saveTiming(currentTiming: Timing) {
        currentTiming.setDuration()
        val contentResolver = activity?.contentResolver
        val values = ContentValues()
        values.put(TimingMetaData.Columns.TIMING_TASK_ID, currentTiming.task.id)
        values.put(TimingMetaData.Columns.TIMING_START_TIME, currentTiming.startTime)
        values.put(TimingMetaData.Columns.TIMING_DURATION, currentTiming.duration)
        contentResolver?.insert(TimingMetaData.CONTENT_URI, values)
    }
}