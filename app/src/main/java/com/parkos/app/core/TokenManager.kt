package com.parkos.app.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val ORG_ID_KEY = stringPreferencesKey("org_id")
        private val USER_FULL_NAME_KEY = stringPreferencesKey("user_full_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val SESSION_SAVED_AT_KEY = longPreferencesKey("session_saved_at")

        private const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L
    }

    fun getTokenFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }

    fun getUserIdFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }

    fun getUserTypeFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USER_TYPE_KEY]
        }

    fun getOrgIdFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[ORG_ID_KEY]
        }

    fun getUserFullNameFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USER_FULL_NAME_KEY]
        }

    fun getUserEmailFlow(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    fun getSessionSavedAtFlow(): Flow<Long?> =
        context.dataStore.data.map { preferences ->
            preferences[SESSION_SAVED_AT_KEY]
        }

    suspend fun getToken(): String? =
        getTokenFlow().first()

    suspend fun getUserId(): String? =
        getUserIdFlow().first()

    suspend fun getUserType(): String? =
        getUserTypeFlow().first()

    suspend fun getOrgId(): String? =
        getOrgIdFlow().first()

    suspend fun getUserFullName(): String? =
        getUserFullNameFlow().first()

    suspend fun getUserEmail(): String? =
        getUserEmailFlow().first()

    suspend fun getSessionSavedAt(): Long? =
        getSessionSavedAtFlow().first()

    suspend fun isSessionExpired(): Boolean {
        val savedAt = getSessionSavedAt() ?: return true
        val now = System.currentTimeMillis()
        return now - savedAt > SESSION_TIMEOUT_MS
    }

    suspend fun saveSession(
        token: String,
        userId: String,
        userType: String,
        orgId: String?,
        fullName: String? = null,
        email: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_TYPE_KEY] = userType
            preferences[SESSION_SAVED_AT_KEY] = System.currentTimeMillis()

            if (!orgId.isNullOrBlank()) {
                preferences[ORG_ID_KEY] = orgId
            } else {
                preferences.remove(ORG_ID_KEY)
            }

            if (!fullName.isNullOrBlank()) {
                preferences[USER_FULL_NAME_KEY] = fullName
            } else {
                preferences.remove(USER_FULL_NAME_KEY)
            }

            if (!email.isNullOrBlank()) {
                preferences[USER_EMAIL_KEY] = email
            } else {
                preferences.remove(USER_EMAIL_KEY)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}