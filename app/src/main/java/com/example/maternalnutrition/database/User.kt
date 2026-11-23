package com.example.maternalnutrition.database


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val fullName: String,

    val email: String,

    val passwordHash: String,

    val passwordSalt: String,

    val phone: String = "",

    val pregnancyWeek: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)