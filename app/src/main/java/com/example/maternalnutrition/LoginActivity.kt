package com.example.maternalnutrition

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtDemo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtDemo = findViewById(R.id.txtDemo)

        btnLogin.setOnClickListener {

            val name = editName.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (name.isEmpty()) {
                editName.error = "Enter your name"
                editName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                editEmail.error = "Enter your email"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editPassword.error = "Enter your password"
                editPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 4) {
                editPassword.error =
                    "Password must have at least 4 characters"
                editPassword.requestFocus()
                return@setOnClickListener
            }

            val preferences = getSharedPreferences(
                "MaternalNutritionPrefs",
                MODE_PRIVATE
            )

            preferences.edit()
                .putBoolean("isLoggedIn", true)
                .putString("userName", name)
                .putString("userEmail", email)
                .apply()

            Toast.makeText(
                this,
                "Welcome to Maternal Nutrition",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(
                Intent(
                    this,
                    DashboardActivity::class.java
                )
            )

            finish()
        }

        txtDemo.setOnClickListener {
            editName.setText("Martha")
            editEmail.setText("martha@gmail.com")
            editPassword.setText("1234")
        }
    }
}