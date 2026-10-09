package com.example.maternalnutrition

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordMealActivity : AppCompatActivity() {

    private lateinit var spinnerMealType: Spinner
    private lateinit var editFood: EditText
    private lateinit var editNotes: EditText
    private lateinit var btnSaveMeal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record_meal)

        spinnerMealType =
            findViewById(R.id.spinnerMealType)

        editFood =
            findViewById(R.id.editFood)

        editNotes =
            findViewById(R.id.editNotes)

        btnSaveMeal =
            findViewById(R.id.btnSaveMeal)

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

        val mealType =
            spinnerMealType.selectedItem.toString()

        val food =
            editFood.text.toString().trim()

        val notes =
            editNotes.text.toString().trim()

        if (food.isEmpty()) {
            editFood.error = "Enter the food consumed"
            editFood.requestFocus()
            return
        }

        val date = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        ).format(Date())

        val preferences = getSharedPreferences(
            "MaternalNutritionPrefs",
            MODE_PRIVATE
        )

        val existing =
            preferences.getString(
                "meals",
                "[]"
            )

        val meals =
            JSONArray(existing)

        val meal =
            JSONObject()

        meal.put("date", date)
        meal.put("mealType", mealType)
        meal.put("food", food)
        meal.put("notes", notes)

        meals.put(meal)

        preferences.edit()
            .putString(
                "meals",
                meals.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Meal record saved",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}