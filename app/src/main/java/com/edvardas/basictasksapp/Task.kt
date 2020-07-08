package com.edvardas.basictasksapp

import java.io.Serializable

data class Task(
    var id: Long,
    val name: String,
    val description: String,
    val sortOrder: Int
) : Serializable