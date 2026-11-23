
package com.example.maternalnutrition

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maternalnutrition.database.AppDatabase
import com.example.maternalnutrition.database.Meal
import com.example.maternalnutrition.utils.NutritionChatbot
import com.example.maternalnutrition.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionChatActivity : AppCompatActivity() {

    private lateinit var chatMessages: LinearLayout
    private lateinit var chatScroll: ScrollView
    private lateinit var editChatMessage: EditText
    private lateinit var btnSendMessage: Button

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var currentUserId: Int = -1

    private var mealHistory: List<Meal> = emptyList()

    private var isHistoryLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            finish()
            return
        }

        currentUserId = sessionManager.getUserId()

        if (currentUserId <= 0) {
            sessionManager.clearSession()
            finish()
            return
        }

        setContentView(R.layout.activity_nutrition_chat)

        database = AppDatabase.getDatabase(applicationContext)

        chatMessages = findViewById(R.id.chatMessages)
        chatScroll = findViewById(R.id.chatScroll)
        editChatMessage = findViewById(R.id.editChatMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        showWelcomeMessage()

        btnSendMessage.isEnabled = false
        btnSendMessage.text = "Loading..."

        loadMealHistory()

        btnSendMessage.setOnClickListener {
            sendMessage()
        }

        editChatMessage.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun showWelcomeMessage() {

        val name = sessionManager.getFullName()

        val welcome = if (name.isNotBlank()) {
            "Hello $name! Welcome to your Nutrition Assistant."
        } else {
            "Hello! Welcome to your Nutrition Assistant."
        }

        addBotMessage(
            "$welcome\n\n" +
                    "I can help you review your recorded meals, " +
                    "identify repeated foods and explore different " +
                    "food options.\n\n" +
                    "Try asking: What foods have I recorded recently?"
        )
    }

    private fun loadMealHistory() {

        lifecycleScope.launch {

            try {

                val meals = withContext(Dispatchers.IO) {
                    database.mealDao().getMealsByUser(currentUserId)
                }

                mealHistory = meals
                isHistoryLoaded = true

                btnSendMessage.isEnabled = true
                btnSendMessage.text = "Send"

                if (meals.isEmpty()) {

                    addBotMessage(
                        "You have not recorded any meals yet. " +
                                "You can start by using the Record Meal " +
                                "feature on your dashboard."
                    )

                } else {

                    addBotMessage(
                        "I have loaded your meal records. " +
                                "You can now ask me about your eating " +
                                "patterns and food variety."
                    )
                }

            } catch (e: Exception) {

                mealHistory = emptyList()
                isHistoryLoaded = true

                btnSendMessage.isEnabled = true
                btnSendMessage.text = "Send"

                addBotMessage(
                    "I could not retrieve your meal records. " +
                            "You can still ask general nutrition questions."
                )
            }
        }
    }

    private fun sendMessage() {

        val message = editChatMessage.text.toString().trim()

        if (message.isEmpty()) {
            editChatMessage.error = "Please enter a message"
            return
        }

        if (!isHistoryLoaded) {

            Toast.makeText(
                this,
                "Please wait while meal records are loaded",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        addUserMessage(message)

        editChatMessage.text.clear()

        val response = NutritionChatbot.generateResponse(
            message = message,
            meals = mealHistory
        )

        addBotMessage(response)
    }

    private fun addUserMessage(message: String) {
        addMessage(message, true)
    }

    private fun addBotMessage(message: String) {
        addMessage(message, false)
    }

    private fun addMessage(
        message: String,
        isUser: Boolean
    ) {

        val messageText = TextView(this)

        messageText.text = message
        messageText.textSize = 14f

        messageText.setTextColor(
            if (isUser) Color.WHITE else Color.rgb(38, 50, 56)
        )

        messageText.setPadding(
            16.dp(),
            12.dp(),
            16.dp(),
            12.dp()
        )

        val background = GradientDrawable().apply {

            cornerRadius = 18.dp().toFloat()

            setColor(
                if (isUser) {
                    Color.rgb(0, 137, 123)
                } else {
                    Color.WHITE
                }
            )
        }

        messageText.background = background

        messageText.maxWidth =
            (resources.displayMetrics.widthPixels * 0.78f).toInt()

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            if (isUser) 45.dp() else 0,
            6.dp(),
            if (isUser) 0 else 45.dp(),
            6.dp()
        )

        params.gravity = if (isUser) {
            Gravity.END
        } else {
            Gravity.START
        }

        chatMessages.addView(messageText, params)

        chatScroll.post {
            chatScroll.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun Int.dp(): Int {

        return (
                this * resources.displayMetrics.density
                ).toInt()
    }
}
