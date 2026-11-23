package com.example.maternalnutrition.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_records")
data class WeightRecord(

    @PrimaryKey(autoGenerate = true)
    val weightId: Int = 0,

    val userId: Int,

    val weightKg: Double,

    val recordedAt: Long = System.currentTimeMillis(),

    val notes: String = ""
)