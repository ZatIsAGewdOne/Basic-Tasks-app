package com.edvardas.basictasksapp

import android.content.Context
import android.database.Cursor
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DurationsAdapter(context: Context, private var cursor: Cursor?) : RecyclerView.Adapter<DurationsAdapter.ViewHolder>() {
    var dateFormat: java.text.DateFormat = DateFormat.getDateFormat(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_durations_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor != null && cursor!!.count != 0) {
            if (!cursor?.moveToPosition(position)!!) {
                throw Exception("Couldn't move cursor to position $position")
            }
            val name = cursor!!.getString(cursor!!.getColumnIndex(DurationsMetaData.Columns.DURATIONS_NAME))
            val description = cursor!!.getString(cursor!!.getColumnIndex(DurationsMetaData.Columns.DURATIONS_DESCRIPTION))
            val startTime = cursor!!.getLong(cursor!!.getColumnIndex(DurationsMetaData.Columns.DURATIONS_START_TIME))
            val duration = cursor!!.getLong(cursor!!.getColumnIndex(DurationsMetaData.Columns.DURATIONS_DURATION))
            holder.name?.text = name
            if (holder.description != null) {
                holder.description?.text = description
            }
            val userDate = dateFormat.format(startTime * 1000)
            val totalTime = formatDuration(duration)
            holder.startDate?.text = userDate
            holder.duration?.text = totalTime
        }
    }

    override fun getItemCount(): Int {
        return if (cursor != null) cursor!!.count else 0
    }

    private fun formatDuration(duration: Long): String {
        val hours = duration / 3600
        val remainder = duration - (hours * 3600)
        val minutes = remainder / 60
        val seconds = remainder - (minutes * 60)
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) {
            return null
        }
        val oldCursor = cursor
        this.cursor = newCursor
        if (newCursor != null) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeChanged(0, itemCount)
        }
        return oldCursor
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var description: TextView? = null
        var startDate: TextView? = null
        var duration: TextView? = null

        init {
            this.name = itemView.findViewById(R.id.td_name)
            this.description = itemView.findViewById(R.id.td_duration)
            this.startDate = itemView.findViewById(R.id.td_start)
            this.duration = itemView.findViewById(R.id.td_duration)
        }
    }
}