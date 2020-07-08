package com.edvardas.basictasksapp

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class AddEditActivityFragment : Fragment() {
    companion object {
        private const val TAG = "AddEditActivityFragment"
    }

    private var mode: FragmentEditMode? = null
    private var nameTextView: EditText? = null
    private var descriptionTextView: EditText? = null
    private var sortOrderTextView: EditText? = null
    private var saveButton: Button? = null
    private var listener: OnSaveClicked? = null

    val canClose: Boolean
        get() = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity !is OnSaveClicked) {
            throw ClassCastException("${activity?.javaClass?.simpleName} must implement OnSaveClicked interface")
        }
        listener = activity as OnSaveClicked
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit, container, false)
        nameTextView = view.findViewById(R.id.add_edit_name)
        descriptionTextView = view.findViewById(R.id.add_edit_description)
        sortOrderTextView = view.findViewById(R.id.add_edit_sort_order)
        saveButton = view.findViewById(R.id.add_edit_btn_save)

        val task: Task?
        if (arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task details")
            task = arguments?.getSerializable(Task::class.java.simpleName) as Task?
            mode = if (task != null) {
                Log.d(TAG, "onCreateView: task details found, editing...")
                nameTextView?.setText(task.name)
                descriptionTextView?.setText(task.description)
                sortOrderTextView?.setText(task.sortOrder)
                FragmentEditMode.EDIT
            } else {
                FragmentEditMode.ADD
            }
        } else {
            task = null
            Log.d(TAG, "onCreateView: No arguments retrieved, adding new task record.")
            mode = FragmentEditMode.ADD
        }

        saveButton?.setOnClickListener {
            val so = if (sortOrderTextView?.length()!! > 0) Integer.parseInt(sortOrderTextView?.text.toString()) else 0
            val contentResolver = activity?.contentResolver
            val values = ContentValues()

            when (mode) {
                FragmentEditMode.EDIT -> {
                    when {
                        nameTextView?.text.toString() == task?.name -> {
                            values.put(TasksMetaData.Column.TASKS_NAME, nameTextView?.text.toString())
                        }
                        descriptionTextView?.text.toString() == task?.description -> {
                            values.put(TasksMetaData.Column.TASKS_DESCRIPTION, descriptionTextView?.text.toString())
                        }
                        so == task?.sortOrder -> {
                            values.put(TasksMetaData.Column.SORT_ORDER, so)
                        }
                    }
                    if (values.size() != 0) {
                        Log.d(TAG, "onCreateView: Updating task")
                        contentResolver?.update(TasksMetaData.buildTaskUri(task?.id!!), values, null, null)
                    }
                }
                FragmentEditMode.ADD -> {
                    if (nameTextView?.length()!! > 0) {
                        Log.d(TAG, "onCreateView: Adding new task")
                        values.put(TasksMetaData.Column.TASKS_NAME, nameTextView?.text.toString())
                        values.put(TasksMetaData.Column.TASKS_DESCRIPTION, descriptionTextView?.text.toString())
                        values.put(TasksMetaData.Column.SORT_ORDER, so)
                        contentResolver?.insert(TasksMetaData.CONTENT_URI, values)
                    }
                }
            }
            Log.d(TAG, "onCreateView: Done editing")
            listener?.onSaveClicked()
        }
        Log.d(TAG, "onCreateView: Exiting...")
        return view
    }
}