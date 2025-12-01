package com.example.machteacher.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TOKEN = stringPreferencesKey("jwt_token")
    private val USER_ID = longPreferencesKey("user_id")
    private val USER_ROLE = stringPreferencesKey("user_role")

    // ⬇️ NUEVO: ID del último SOS aceptado que ya mostró popup
    private val LAST_SOS_ACCEPTED = longPreferencesKey("last_sos_accepted_id")

    // ---------------- TOKEN ----------------
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN]
    }

    // ---------------- USER ID ----------------
    suspend fun saveUserId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
        }
    }

    suspend fun getUserId(): Long? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID]
    }

    fun getUserIdAsFlow(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    // ---------------- USER ROLE ----------------
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ROLE] = role
        }
    }

    suspend fun getUserRole(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ROLE]
    }

    fun getUserRoleAsFlow(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[USER_ROLE]
        }
    }

    // ---------------- SOS ID (NUEVO!) ----------------
    suspend fun saveLastAcceptedSos(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SOS_ACCEPTED] = id
        }
    }

    suspend fun getLastAcceptedSos(): Long? {
        val prefs = context.dataStore.data.first()
        return prefs[LAST_SOS_ACCEPTED]
    }

    // ---------------- CLEAR ----------------
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
