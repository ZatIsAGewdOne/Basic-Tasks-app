package com.edvardas.basictasksapp

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.security.InvalidParameterException

class MainActivityFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    companion object {
        private const val TAG = "MainActivityFragment"
        const val LOADER_ID = 0
    }

    private var adapter: CursorRecyclerViewAdapter? = null

    init {
        Log.d(TAG, "MainActivityFragment: starts")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
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
        this.adapter = CursorRecyclerViewAdapter(null, activity as OnTaskClickListener)
        recyclerView.adapter = this.adapter
        Log.d(TAG, "onCreateView: ends")
        return view
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader: starts")
        val projection = arrayOf(TasksMetaData.Column.ID, TasksMetaData.Column.TASKS_NAME,
            TasksMetaData.Column.TASKS_DESCRIPTION, TasksMetaData.Column.SORT_ORDER)
        val sortOrder = "${TasksMetaData.Column.SORT_ORDER},${TasksMetaData.Column.TASKS_NAME} COLLATE NOCASE"
        if (id == LOADER_ID) {
            return CursorLoader(requireActivity(), TasksMetaData.CONTENT_URI, projection, null, null, sortOrder)
        } else {
            throw InvalidParameterException("${TAG}.oncreateLoader called with invalid id $id")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "onLoadFinished: entered")
        this.adapter?.swapCursor(data)
        Log.d(TAG, "onLoadFinished: count is ${this.adapter?.itemCount}")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        this.adapter?.swapCursor(null)
    }
}