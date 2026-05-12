package com.parkos.app.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "auth")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY   = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val ORG_ID_KEY  = stringPreferencesKey("org_id")
    }

    fun getTokenFlow(): Flow<String?> =
        context.dataStore.data.map { it[TOKEN_KEY] }

    fun getUserIdFlow(): Flow<String?> =
        context.dataStore.data.map { it[USER_ID_KEY] }

    fun getUserTypeFlow(): Flow<String?> =
        context.dataStore.data.map { it[USER_TYPE_KEY] }

    suspend fun saveSession(
        token: String,
        userId: String,
        userType: String,
        orgId: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY]    = token
            prefs[USER_ID_KEY]  = userId
            prefs[USER_TYPE_KEY] = userType
            if (orgId != null) prefs[ORG_ID_KEY] = orgId
        }
    }

    fun getOrgIdFlow(): Flow<String?> =
        context.dataStore.data.map { it[ORG_ID_KEY] }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}