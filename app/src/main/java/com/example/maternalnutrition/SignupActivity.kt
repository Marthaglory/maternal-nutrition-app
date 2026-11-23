
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
import com.example.maternalnutrition.database.User
import com.example.maternalnutrition.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var editFullName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editPregnancyWeek: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText

    private lateinit var buttonRegister: Button
    private lateinit var textLogin: TextView

    private lateinit var btnTogglePassword: ImageView
    private lateinit var btnToggleConfirmPassword: ImageView

    private lateinit var database: AppDatabase

    private var passwordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        database = AppDatabase.getDatabase(applicationContext)

        // Connect XML fields
        editFullName = findViewById(R.id.editFullName)
        editEmail = findViewById(R.id.editEmail)
        editPhone = findViewById(R.id.editPhone)
        editPregnancyWeek = findViewById(R.id.editPregnancyWeek)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)

        buttonRegister = findViewById(R.id.buttonRegister)
        textLogin = findViewById(R.id.textLogin)

        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleConfirmPassword =
            findViewById(R.id.btnToggleConfirmPassword)

        // Show or hide password
        btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible

            togglePassword(
                editPassword,
                passwordVisible,
                btnTogglePassword
            )
        }

        // Show or hide confirmation password
        btnToggleConfirmPassword.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible

            togglePassword(
                editConfirmPassword,
                confirmPasswordVisible,
                btnToggleConfirmPassword
            )
        }

        // Register button
        buttonRegister.setOnClickListener {
            registerUser()
        }

        // Navigate to login
        textLogin.setOnClickListener {
            finish()
        }
    }

    // Show or hide password
    private fun togglePassword(
        field: EditText,
        visible: Boolean,
        icon: ImageView
    ) {
        val cursorPosition = field.selectionStart

        field.inputType = if (visible) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        icon.contentDescription =
            if (visible) "Hide password" else "Show password"

        icon.setImageResource(
            if (visible) {
                android.R.drawable.ic_menu_close_clear_cancel
            } else {
                android.R.drawable.ic_menu_view
            }
        )

        field.setSelection(
            cursorPosition.coerceIn(0, field.text.length)
        )
    }

    // Register new user
    private fun registerUser() {

        val fullName = editFullName.text.toString().trim()
        val email = editEmail.text.toString().trim().lowercase()
        val phone = editPhone.text.toString().trim()

        val pregnancyWeekText =
            editPregnancyWeek.text.toString().trim()

        val password = editPassword.text.toString()
        val confirmPassword = editConfirmPassword.text.toString()

        // Validate required fields
        if (
            fullName.isEmpty() ||
            email.isEmpty() ||
            phone.isEmpty() ||
            pregnancyWeekText.isEmpty() ||
            password.isEmpty() ||
            confirmPassword.isEmpty()
        ) {
            showMessage("Please complete all fields")
            return
        }

        // Validate full name
        if (fullName.length < 2) {
            editFullName.error = "Enter a valid full name"
            editFullName.requestFocus()
            return
        }

        // Validate email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Enter a valid email address"
            editEmail.requestFocus()
            return
        }

        // Validate pregnancy week
        val pregnancyWeek = pregnancyWeekText.toIntOrNull()

        if (pregnancyWeek == null || pregnancyWeek !in 1..42) {
            editPregnancyWeek.error =
                "Enter a pregnancy week between 1 and 42"
            editPregnancyWeek.requestFocus()
            return
        }

        // Validate password
        if (password.length < 8) {
            editPassword.error =
                "Password must contain at least 8 characters"
            editPassword.requestFocus()
            return
        }

        // Confirm password
        if (password != confirmPassword) {
            editConfirmPassword.error = "Passwords do not match"
            editConfirmPassword.requestFocus()
            return
        }

        // Disable button while registering
        buttonRegister.isEnabled = false
        buttonRegister.text = "Registering..."

        lifecycleScope.launch {

            try {

                // Check whether email already exists
                val existingUser = withContext(Dispatchers.IO) {
                    database.userDao().getUserByEmail(email)
                }

                if (existingUser != null) {
                    editEmail.error =
                        "This email is already registered"
                    editEmail.requestFocus()
                    return@launch
                }

                // Generate password salt
                val salt = withContext(Dispatchers.Default) {
                    PasswordUtils.generateSalt()
                }

                // Hash password
                val passwordHash = withContext(Dispatchers.Default) {
                    PasswordUtils.hashPassword(password, salt)
                }

                // Create user account
                val user = User(
                    fullName = fullName,
                    email = email,
                    passwordHash = passwordHash,
                    passwordSalt = salt,
                    phone = phone,
                    pregnancyWeek = pregnancyWeek
                )

                // Save user in Room database
                withContext(Dispatchers.IO) {
                    database.userDao().insertUser(user)
                }

                showMessage(
                    "Registration successful. Please log in."
                )

                // Navigate to login screen
                val intent = Intent(
                    this@SignupActivity,
                    LoginActivity::class.java
                )

                startActivity(intent)
                finish()

            } catch (e: Exception) {

                showMessage(
                    "Registration failed. Please try again."
                )

            } finally {

                buttonRegister.isEnabled = true
                buttonRegister.text = "Create Account"
            }
        }
    }

    // Display messages
    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
