
package com.example.maternalnutrition

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maternalnutrition.database.AppDatabase
import com.example.maternalnutrition.database.Meal
import com.example.maternalnutrition.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MealHistoryActivity : AppCompatActivity() {

    private lateinit var listMeals: ListView
    private lateinit var txtEmpty: TextView

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_meal_history)

        listMeals = findViewById(R.id.listMeals)
        txtEmpty = findViewById(R.id.txtEmpty)

        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        currentUserId = sessionManager.getUserId()

        if (!sessionManager.isLoggedIn() || currentUserId <= 0) {

            Toast.makeText(
                this,
                "Please log in to view meal history",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        loadMeals()
    }

    private fun loadMeals() {

        lifecycleScope.launch {

            try {

                val meals = withContext(Dispatchers.IO) {
                    database.mealDao().getMealsByUser(currentUserId)
                }

                displayMeals(meals)

            } catch (e: Exception) {

                Toast.makeText(
                    this@MealHistoryActivity,
                    "Failed to load meal history: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayMeals(meals: List<Meal>) {

        val mealList = ArrayList<String>()

        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        )

        for (meal in meals) {

            val date = dateFormat.format(
                Date(meal.mealDate)
            )

            val item = buildString {

                append(meal.mealType)
                append("\n\n")

                append("Food: ")
                append(meal.foodName)
                append("\n\n")

                append("Date: ")
                append(date)

                if (meal.notes.isNotEmpty()) {
                    append("\n\n")
                    append("Notes: ")
                    append(meal.notes)
                }
            }

            mealList.add(item)
        }

        if (mealList.isEmpty()) {

            txtEmpty.visibility = View.VISIBLE
            listMeals.visibility = View.GONE

            txtEmpty.text = "No meal records found.\nStart by recording your first meal."

        } else {

            txtEmpty.visibility = View.GONE
            listMeals.visibility = View.VISIBLE

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                mealList
            )

            listMeals.adapter = adapter
        }
    }
}
