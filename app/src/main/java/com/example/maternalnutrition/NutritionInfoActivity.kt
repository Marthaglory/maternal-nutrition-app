package com.example.maternalnutrition

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NutritionInfoActivity : AppCompatActivity() {

    private lateinit var txtNutrition: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_nutrition_info
        )

        txtNutrition =
            findViewById(R.id.txtNutrition)

        btnBack =
            findViewById(R.id.btnBack)

        txtNutrition.text = """
            Healthy Nutrition During Pregnancy

            A balanced diet during pregnancy supports the health
            and wellbeing of both mother and baby.

            🥗 1. Eat a variety of foods

            Include a variety of foods from different food
            groups, including vegetables, fruits, grains,
            legumes and appropriate protein sources.

            🍎 2. Include fruits and vegetables

            Fruits and vegetables provide important nutrients
            and fibre as part of a balanced diet.

            🥚 3. Choose appropriate protein sources

            Foods such as beans, eggs, fish and other suitable
            protein sources can contribute to a balanced diet.

            💧 4. Stay hydrated

            Drink safe fluids regularly throughout the day.

            📅 5. Attend antenatal appointments

            Attend scheduled antenatal appointments and follow
            recommendations provided by qualified health
            professionals.

            💚 6. Seek professional advice

            Nutritional needs differ between individuals.
            Consult a qualified health professional for
            personalised advice.

            IMPORTANT

            This application provides general educational
            information. It does not replace professional
            medical, antenatal or nutritional care.
        """.trimIndent()

        btnBack.setOnClickListener {
            finish()
        }
    }
}