package com.bks.pokedex.data.local.prefs

interface AuthLocalDataSource {
    fun isLoggedIn(): Boolean
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getTokenExpiration(): Long
    fun saveSession(user: String, accessToken: String, refreshToken: String, expiresIn: Long)
    fun updateAccessToken(newToken: String, expiresIn: Long)
    fun clearSession()
}
