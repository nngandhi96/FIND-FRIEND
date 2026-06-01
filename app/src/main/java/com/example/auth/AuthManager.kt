package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val email: String,
    val name: String,
    val isGoogleUser: Boolean = false,
    val profilePicture: String = ""
)

object AuthManager {
    private const val PREFS_NAME = "findfriend_auth_prefs"
    private const val KEY_LOGGED_IN_EMAIL = "logged_in_email"
    private const val KEY_LOGGED_IN_NAME = "logged_in_name"
    private const val KEY_IS_GOOGLE_USER = "is_google_user"
    private const val KEY_USER_DB_PREFIX = "user_db_"

    private lateinit var prefs: SharedPreferences
    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_LOGGED_IN_EMAIL, null)
        val name = prefs.getString(KEY_LOGGED_IN_NAME, null)
        val isGoogle = prefs.getBoolean(KEY_IS_GOOGLE_USER, false)

        if (email != null && name != null) {
            _currentUser.value = UserSession(email, name, isGoogle)
        } else {
            // Seed a default mock account for easy first testing
            saveUserCredential("demo@findfriend.com", "password123", "Demo User")
        }
    }

    private fun saveUserCredential(email: String, secret: String, displayName: String) {
        prefs.edit().apply {
            putString("$KEY_USER_DB_PREFIX$email", secret)
            putString("${KEY_USER_DB_PREFIX}name_$email", displayName)
            apply()
        }
    }

    fun login(email: String, secret: String): Result<UserSession> {
        val trimmedEmail = email.trim().lowercase()
        val storedSecret = prefs.getString("$KEY_USER_DB_PREFIX$trimmedEmail", null)
        if (storedSecret == null) {
            return Result.failure(Exception("Account not found. Please sign up."))
        }
        if (storedSecret != secret) {
            return Result.failure(Exception("Invalid password. Please try again."))
        }

        val name = prefs.getString("${KEY_USER_DB_PREFIX}name_$trimmedEmail", "User") ?: "User"
        val session = UserSession(trimmedEmail, name, false)
        persistSession(session)
        return Result.success(session)
    }

    fun signup(name: String, email: String, secret: String): Result<UserSession> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isEmpty() || name.isEmpty() || secret.isEmpty()) {
            return Result.failure(Exception("All fields are required."))
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(Exception("Please enter a valid email address."))
        }
        if (secret.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }

        val storedSecret = prefs.getString("$KEY_USER_DB_PREFIX$trimmedEmail", null)
        if (storedSecret != null) {
            return Result.failure(Exception("Account already exists with this email."))
        }

        saveUserCredential(trimmedEmail, secret, name)
        val session = UserSession(trimmedEmail, name, false)
        persistSession(session)
        return Result.success(session)
    }

    fun loginWithGoogle(email: String, name: String): UserSession {
        val trimmedEmail = email.trim().lowercase()
        saveUserCredential(trimmedEmail, "google_auth_oauth_token", name)
        val session = UserSession(trimmedEmail, name, true)
        persistSession(session)
        return session
    }

    fun resetPassword(email: String): Result<String> {
        val trimmedEmail = email.trim().lowercase()
        val storedSecret = prefs.getString("$KEY_USER_DB_PREFIX$trimmedEmail", null)
        if (storedSecret == null) {
            return Result.failure(Exception("No account linked with this email address."))
        }
        // Simulated email dispatch
        return Result.success("A secure password reset link has been dispatched to $trimmedEmail.")
    }

    fun logout() {
        prefs.edit().apply {
            remove(KEY_LOGGED_IN_EMAIL)
            remove(KEY_LOGGED_IN_NAME)
            remove(KEY_IS_GOOGLE_USER)
            apply()
        }
        _currentUser.value = null
    }

    private fun persistSession(session: UserSession) {
        prefs.edit().apply {
            putString(KEY_LOGGED_IN_EMAIL, session.email)
            putString(KEY_LOGGED_IN_NAME, session.name)
            putBoolean(KEY_IS_GOOGLE_USER, session.isGoogleUser)
            apply()
        }
        _currentUser.value = session
    }

    fun deleteAccount() {
        val email = _currentUser.value?.email ?: return
        prefs.edit().apply {
            remove("$KEY_USER_DB_PREFIX$email")
            remove("${KEY_USER_DB_PREFIX}name_$email")
            remove(KEY_LOGGED_IN_EMAIL)
            remove(KEY_LOGGED_IN_NAME)
            remove(KEY_IS_GOOGLE_USER)
            apply()
        }
        _currentUser.value = null
    }
}
