package com.bks.pokedex.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthLocalDataSourceImpl @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : AuthLocalDataSource {
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_SESSION_USER = "session_user"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration"
    }

    override fun isLoggedIn(): Boolean = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)

    override fun getAccessToken(): String? = sharedPrefs.getString(KEY_ACCESS_TOKEN, null)

    override fun getRefreshToken(): String? = sharedPrefs.getString(KEY_REFRESH_TOKEN, null)

    override fun getTokenExpiration(): Long = sharedPrefs.getLong(KEY_TOKEN_EXPIRATION, 0L)

    override fun saveSession(
        user: String,
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000)
        sharedPrefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_SESSION_USER, user)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRATION, expirationTime)
            apply()
        }
    }

    override fun updateAccessToken(newToken: String, expiresIn: Long) {
        val expirationTime = System.currentTimeMillis() + (expiresIn * 1000)
        sharedPrefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, newToken)
            putLong(KEY_TOKEN_EXPIRATION, expirationTime)
            apply()
        }
    }

    override fun clearSession() {
        sharedPrefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_SESSION_USER)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRATION)
            apply()
        }
    }
}
