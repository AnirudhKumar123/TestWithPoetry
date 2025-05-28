package com.example.testwithpoetry.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.testwithpoetry.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettings @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_settings")

    suspend fun saveFirstLaunchComplete() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.FIRST_LAUNCH] = false
        }
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USER_NAME] = user.name
            settings[PreferencesKeys.USER_EMAIL] = user.email
        }
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.FIRST_LAUNCH] ?: true
    }

    val userData: Flow<User> = context.dataStore.data.map { prefs ->
        User(
            name = prefs[PreferencesKeys.USER_NAME] ?: "",
            email = prefs[PreferencesKeys.USER_EMAIL] ?: "",
            1232L
        )
    }

    private object PreferencesKeys {
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }
}