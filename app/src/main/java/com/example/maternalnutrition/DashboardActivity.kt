
package com.example.maternalnutrition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.cardview.widget.CardView
import com.example.maternalnutrition.database.AppDatabase
import com.example.maternalnutrition.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var txtWelcome: TextView
    private lateinit var txtMealCount: TextView
    private lateinit var txtDashboardSubtitle: TextView

    private lateinit var cardRecordMeal: CardView
    private lateinit var cardMealHistory: CardView
    private lateinit var cardNutritionInfo: CardView

    private lateinit var btnNutritionChat: Button
    private lateinit var btnLogout: Button

    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase

    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Check whether the user is logged in
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        currentUserId = sessionManager.getUserId()

        if (currentUserId <= 0) {
            sessionManager.clearSession()
            redirectToLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        database = AppDatabase.getDatabase(applicationContext)

        initializeViews()
        displayUserInformation()
        setupClickListeners()
    }

    // Initialize dashboard views
    private fun initializeViews() {

        txtWelcome = findViewById(R.id.txtWelcome)
        txtMealCount = findViewById(R.id.txtMealCount)
        txtDashboardSubtitle =
            findViewById(R.id.txtDashboardSubtitle)

        cardRecordMeal = findViewById(R.id.cardRecordMeal)
        cardMealHistory = findViewById(R.id.cardMealHistory)
        cardNutritionInfo = findViewById(R.id.cardNutritionInfo)

        btnNutritionChat = findViewById(R.id.btnNutritionChat)
        btnLogout = findViewById(R.id.btnLogout)
    }

    // Display logged-in user's information
    private fun displayUserInformation() {

        val name = sessionManager.getFullName()

        txtWelcome.text = if (name.isNotBlank()) {
            "Welcome, $name"
        } else {
            "Welcome, Mother"
        }

        txtDashboardSubtitle.text =
            "Take a moment to care for your nutrition today."

        loadMealCount()
    }

    // Set up dashboard navigation
    private fun setupClickListeners() {

        // Record Meal
        cardRecordMeal.setOnClickListener {
            openActivity(RecordMealActivity::class.java)
        }

        // Meal History
        cardMealHistory.setOnClickListener {
            openActivity(MealHistoryActivity::class.java)
        }

        // Nutrition Information
        cardNutritionInfo.setOnClickListener {
            openActivity(NutritionInfoActivity::class.java)
        }

        // Nutrition Assistant
        btnNutritionChat.setOnClickListener {
            openActivity(NutritionChatActivity::class.java)
        }

        // Logout
        btnLogout.setOnClickListener {
            logout()
        }
    }

    // Open another activity
    private fun openActivity(activityClass: Class<*>) {

        startActivity(
            Intent(this, activityClass)
        )
    }

    // Load meal count for the currently logged-in user
    private fun loadMealCount() {

        if (currentUserId <= 0 || !::database.isInitialized) {
            return
        }

        lifecycleScope.launch {

            try {

                val count = withContext(Dispatchers.IO) {
                    database.mealDao().countMeals(currentUserId)
                }

                if (!isFinishing && !isDestroyed) {
                    txtMealCount.text = count.toString()
                }

            } catch (e: Exception) {

                if (!isFinishing && !isDestroyed) {

                    txtMealCount.text = "0"

                    Toast.makeText(
                        this@DashboardActivity,
                        "Unable to load meal records",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Refresh dashboard whenever the user returns
    override fun onResume() {
        super.onResume()

        if (::sessionManager.isInitialized &&
            sessionManager.isLoggedIn()
        ) {

            currentUserId = sessionManager.getUserId()

            if (currentUserId > 0 &&
                ::database.isInitialized
            ) {
                loadMealCount()
            }
        }
    }

    // Logout current user
    private fun logout() {

        sessionManager.clearSession()

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

    // Redirect users without a valid session
    private fun redirectToLogin() {

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
