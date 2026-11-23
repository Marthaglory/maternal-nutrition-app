package com.example.maternalnutrition

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maternalnutrition.database.AppDatabase
import com.example.maternalnutrition.utils.PasswordUtils
import com.example.maternalnutrition.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textSignup: TextView
    private lateinit var btnTogglePassword: ImageView

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        database = AppDatabase.getDatabase(applicationContext)
        sessionManager = SessionManager(this)

        // Initialize views
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textSignup = findViewById(R.id.textSignup)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        // Check existing login session
        if (sessionManager.isLoggedIn()) {
            openDashboard()
            return
        }

        // Show and hide password
        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Login button
        buttonLogin.setOnClickListener {
            loginUser()
        }

        // Navigate to registration
        textSignup.setOnClickListener {
            startActivity(
                Intent(this, SignupActivity::class.java)
            )
        }
    }

    private fun togglePasswordVisibility() {

        passwordVisible = !passwordVisible

        val cursorPosition = editPassword.selectionStart

        if (passwordVisible) {

            editPassword.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            btnTogglePassword.setImageResource(
                android.R.drawable.ic_menu_close_clear_cancel
            )

            btnTogglePassword.contentDescription = "Hide password"

        } else {

            editPassword.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            btnTogglePassword.setImageResource(
                android.R.drawable.ic_menu_view
            )

            btnTogglePassword.contentDescription = "Show password"
        }

        // Keep cursor in its previous position
        editPassword.setSelection(
            cursorPosition.coerceIn(0, editPassword.text.length)
        )
    }

    private fun loginUser() {

        val email = editEmail.text.toString()
            .trim()
            .lowercase()

        val password = editPassword.text.toString()

        // Validate fields
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter your email and password")
            return
        }

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Enter a valid email address"
            editEmail.requestFocus()
            return
        }

        buttonLogin.isEnabled = false
        buttonLogin.text = "Signing in..."

        lifecycleScope.launch {

            try {

                // Find user in database
                val user = withContext(Dispatchers.IO) {
                    database.userDao().getUserByEmail(email)
                }

                if (user == null) {
                    resetLoginButton()
                    showMessage("Invalid email or password")
                    return@launch
                }

                // Verify password hash
                val passwordIsValid = withContext(Dispatchers.Default) {
                    PasswordUtils.verifyPassword(
                        enteredPassword = password,
                        storedHash = user.passwordHash,
                        storedSalt = user.passwordSalt
                    )
                }

                if (passwordIsValid) {

                    // Save login session
                    sessionManager.saveSession(
                        userId = user.userId,
                        fullName = user.fullName,
                        email = user.email
                    )

                    // Open dashboard
                    openDashboard()

                } else {

                    resetLoginButton()
                    showMessage("Invalid email or password")
                }

            } catch (e: Exception) {

                resetLoginButton()

                showMessage(
                    "Unable to log in. Please try again."
                )
            }
        }
    }

    private fun resetLoginButton() {
        buttonLogin.isEnabled = true
        buttonLogin.text = "Login"
    }

    private fun openDashboard() {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
