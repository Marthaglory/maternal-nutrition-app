package com.example.maternalnutrition

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class MealHistoryActivity : AppCompatActivity() {

    private lateinit var listMeals: ListView
    private lateinit var txtEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_meal_history
        )

        listMeals =
            findViewById(R.id.listMeals)

        txtEmpty =
            findViewById(R.id.txtEmpty)

        loadMeals()
    }

    override fun onResume() {
        super.onResume()
        loadMeals()
    }

    private fun loadMeals() {

        val preferences =
            getSharedPreferences(
                "MaternalNutritionPrefs",
                MODE_PRIVATE
            )

        val data =
            preferences.getString(
                "meals",
                "[]"
            )

        val meals =
            JSONArray(data)

        val mealList =
            ArrayList<String>()

        for (
        i in meals.length() - 1 downTo 0
        ) {

            val meal =
                meals.getJSONObject(i)

            val date =
                meal.optString("date")

            val type =
                meal.optString("mealType")

            val food =
                meal.optString("food")

            val notes =
                meal.optString("notes")

            val item = buildString {

                append(type)
                append("\n")

                append(food)
                append("\n\n")

                append(date)

                if (notes.isNotEmpty()) {
                    append("\n")
                    append(notes)
                }
            }

            mealList.add(item)
        }

        if (mealList.isEmpty()) {

            txtEmpty.visibility =
                View.VISIBLE

            listMeals.visibility =
                View.GONE

        } else {

            txtEmpty.visibility =
                View.GONE

            listMeals.visibility =
                View.VISIBLE

            val adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    mealList
                )

            listMeals.adapter =
                adapter
        }
    }
}