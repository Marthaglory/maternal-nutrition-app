package com.example.maternalnutrition.utils


import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "MaternalNutritionSession",
        Context.MODE_PRIVATE
    )

    fun saveSession(
        userId: Int,
        fullName: String,
        email: String
    ) {
        preferences.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return preferences.getInt(KEY_USER_ID, -1)
    }

    fun getFullName(): String {
        return preferences.getString(KEY_FULL_NAME, "") ?: ""
    }

    fun getEmail(): String {
        return preferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}
