
package com.example.maternalnutrition

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maternalnutrition.database.AppDatabase
import com.example.maternalnutrition.database.Meal
import com.example.maternalnutrition.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordMealActivity : AppCompatActivity() {

    private lateinit var spinnerMealType: Spinner
    private lateinit var editFood: EditText
    private lateinit var editNotes: EditText
    private lateinit var btnSaveMeal: Button

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record_meal)

        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        currentUserId = sessionManager.getUserId()

        if (!sessionManager.isLoggedIn() || currentUserId <= 0) {
            Toast.makeText(
                this,
                "Please log in to record a meal",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        spinnerMealType = findViewById(R.id.spinnerMealType)
        editFood = findViewById(R.id.editFood)
        editNotes = findViewById(R.id.editNotes)
        btnSaveMeal = findViewById(R.id.btnSaveMeal)

        val mealTypes = arrayOf(
            "Breakfast",
            "Morning Snack",
            "Lunch",
            "Afternoon Snack",
            "Dinner",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mealTypes
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerMealType.adapter = adapter

        btnSaveMeal.setOnClickListener {
            saveMeal()
        }
    }

    private fun saveMeal() {

        val mealType = spinnerMealType.selectedItem.toString()
        val food = editFood.text.toString().trim()
        val notes = editNotes.text.toString().trim()

        if (food.isEmpty()) {
            editFood.error = "Enter the food consumed"
            editFood.requestFocus()
            return
        }

        if (food.length < 2) {
            editFood.error = "Enter a valid food name"
            editFood.requestFocus()
            return
        }

        btnSaveMeal.isEnabled = false

        val meal = Meal(
            userId = currentUserId,
            mealType = mealType,
            foodName = food,
            notes = notes,
            mealDate = System.currentTimeMillis()
        )

        lifecycleScope.launch {

            try {
                withContext(Dispatchers.IO) {
                    database.mealDao().insertMeal(meal)
                }

                Toast.makeText(
                    this@RecordMealActivity,
                    "Meal record saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {

                btnSaveMeal.isEnabled = true

                Toast.makeText(
                    this@RecordMealActivity,
                    "Failed to save meal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
