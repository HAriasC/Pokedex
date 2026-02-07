package com.bks.pokedex.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bks.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    override fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    }

    override suspend fun login(user: String, password: String): Result<Unit> {
        return if (user.isNotBlank() && password.length >= 4) {
            context.dataStore.edit { preferences ->
                preferences[IS_LOGGED_IN] = true
            }
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales inválidas (mínimo 4 caracteres)"))
        }
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
        }
    }
}
