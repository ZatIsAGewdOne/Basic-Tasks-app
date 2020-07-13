package com.edvardas.basictasksapp

interface OnTaskClickListener {
    fun onEditTask(task: Task)
    fun onDeleteTask(task: Task)
    fun onTaskLongTap(task: Task)
}