package com.fixtech.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "fixtech_session_prefs"
)

data class SessionState(
    val userId: String = "",
    val userName: String = "",
    val role: String = ""
) {
    val isLoggedIn: Boolean
        get() = userId.isNotBlank() && role.isNotBlank()
}

class SessionManager(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        val LOGGED_IN_USER_ID = stringPreferencesKey("LOGGED_IN_USER_ID")
        val LOGGED_IN_USER_NAME = stringPreferencesKey("LOGGED_IN_USER_NAME")
        val USER_ROLE = stringPreferencesKey("USER_ROLE")
    }

    val sessionFlow: Flow<SessionState> = appContext.sessionDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SessionState(
                userId = preferences[LOGGED_IN_USER_ID].orEmpty(),
                userName = preferences[LOGGED_IN_USER_NAME].orEmpty(),
                role = preferences[USER_ROLE].orEmpty()
            )
        }

    val loggedInUserId: Flow<String?> = sessionFlow.map { it.userId.ifBlank { null } }
    val loggedInUserName: Flow<String?> = sessionFlow.map { it.userName.ifBlank { null } }
    val userRole: Flow<String?> = sessionFlow.map { it.role.ifBlank { null } }

    suspend fun saveSession(userId: String, name: String, role: String) {
        appContext.sessionDataStore.edit { preferences ->
            preferences[LOGGED_IN_USER_ID] = userId.trim()
            preferences[LOGGED_IN_USER_NAME] = name.trim()
            preferences[USER_ROLE] = role.trim().uppercase()
        }
    }

    suspend fun clearSession() {
        appContext.sessionDataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_USER_ID)
            preferences.remove(LOGGED_IN_USER_NAME)
            preferences.remove(USER_ROLE)
        }
    }
}
