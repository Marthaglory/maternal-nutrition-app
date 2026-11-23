package com.example.maternalnutrition.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(

    @PrimaryKey(autoGenerate = true)
    val appointmentId: Int = 0,

    val userId: Int,

    val appointmentTitle: String,

    val appointmentDate: Long,

    val location: String = "",

    val notes: String = "",

    val reminderEnabled: Boolean = true
)