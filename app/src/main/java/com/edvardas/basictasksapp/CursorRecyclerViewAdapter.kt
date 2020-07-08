package com.edvardas.basictasksapp

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CursorRecyclerViewAdapter(
    private var cursor: Cursor?,
    private var listener: OnTaskClickListener
) : RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder>() {
    companion object {
        private const val TAG = "CursorRecyclerViewAdapt"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_items, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if (cursor == null || cursor?.count == 0) {
            Log.d(TAG, "onBindViewHolder: providing with instructions")
            holder.name?.setText(R.string.instructions_heading)
            holder.description?.setText(R.string.instructions)
            holder.editButton?.visibility = View.GONE
            holder.deleteButton?.visibility = View.GONE
        } else {
            if (!cursor?.moveToPosition(position)!!) throw IllegalStateException("Couldn't move cursor to position $position")
            val task = Task(
                cursor!!.getLong(cursor!!.getColumnIndex(TasksMetaData.Column.ID)),
                cursor!!.getString(cursor!!.getColumnIndex(TasksMetaData.Column.TASKS_NAME)),
                cursor!!.getString(cursor!!.getColumnIndex(TasksMetaData.Column.TASKS_DESCRIPTION)),
                cursor!!.getInt(cursor!!.getColumnIndex(TasksMetaData.Column.SORT_ORDER))
            )
            holder.name?.text = task.name
            holder.description?.text = task.description
            holder.editButton?.visibility = View.VISIBLE
            holder.deleteButton?.visibility = View.VISIBLE

            val buttonListener = View.OnClickListener {
                when(it.id) {
                    R.id.tli_edit -> listener.onEditTask(task)
                    R.id.tli_delete -> listener.onDeleteTask(task)
                    else -> Log.d(TAG, "onBindViewHolder: found unexpected button id ${it.id}")
                }
            }

            holder.editButton?.setOnClickListener(buttonListener)
            holder.deleteButton?.setOnClickListener(buttonListener)
        }
    }

    override fun getItemCount(): Int {
        return if(cursor == null || cursor?.count == 0) 1 else cursor?.count!!
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == this.cursor) return null
        val oldCursor = this.cursor
        if (newCursor == null) notifyDataSetChanged() else notifyItemRangeChanged(0, itemCount)
        return oldCursor
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var description: TextView? = null
        var editButton: ImageButton? = null
        var deleteButton: ImageButton? = null

        init {
            this.name = itemView.findViewById(R.id.tli_name)
            this.description = itemView.findViewById(R.id.tli_description)
            this.editButton = itemView.findViewById(R.id.tli_edit)
            this.deleteButton = itemView.findViewById(R.id.tli_delete)
        }
    }
}