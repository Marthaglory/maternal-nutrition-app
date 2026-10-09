package com.example.maternalnutrition

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var txtWelcome: TextView
    private lateinit var txtMealCount: TextView

    private lateinit var btnRecordMeal: Button
    private lateinit var btnMealHistory: Button
    private lateinit var btnNutritionInfo: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        txtWelcome = findViewById(R.id.txtWelcome)
        txtMealCount = findViewById(R.id.txtMealCount)

        btnRecordMeal = findViewById(R.id.btnRecordMeal)
        btnMealHistory = findViewById(R.id.btnMealHistory)
        btnNutritionInfo = findViewById(R.id.btnNutritionInfo)
        btnLogout = findViewById(R.id.btnLogout)

        loadUser()

        btnRecordMeal.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RecordMealActivity::class.java
                )
            )
        }

        btnMealHistory.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MealHistoryActivity::class.java
                )
            )
        }

        btnNutritionInfo.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    NutritionInfoActivity::class.java
                )
            )
        }

        btnLogout.setOnClickListener {

            val preferences = getSharedPreferences(
                "MaternalNutritionPrefs",
                MODE_PRIVATE
            )

            preferences.edit()
                .clear()
                .apply()

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadMealCount()
    }

    private fun loadUser() {

        val preferences = getSharedPreferences(
            "MaternalNutritionPrefs",
            MODE_PRIVATE
        )

        val name = preferences.getString(
            "userName",
            "Mother"
        )

        txtWelcome.text = "Good morning, $name"

        loadMealCount()
    }

    private fun loadMealCount() {

        val preferences = getSharedPreferences(
            "MaternalNutritionPrefs",
            MODE_PRIVATE
        )

        val data = preferences.getString(
            "meals",
            "[]"
        )

        try {

            val array =
                org.json.JSONArray(data)

            txtMealCount.text =
                array.length().toString()

        } catch (e: Exception) {

            txtMealCount.text = "0"
        }
    }
}