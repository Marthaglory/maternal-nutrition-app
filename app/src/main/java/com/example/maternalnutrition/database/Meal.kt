package com.example.maternalnutrition.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(

    @PrimaryKey(autoGenerate = true)
    val mealId: Int = 0,

    val userId: Int,

    val mealType: String,

    val foodName: String,

    val notes: String = "",

    val mealDate: Long = System.currentTimeMillis()
)