package de.Pruvex.growpal.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object UserPreferences {
    private val KEY_STAY_LOGGED_IN = booleanPreferencesKey("stay_logged_in")

    fun stayLoggedInFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_STAY_LOGGED_IN] ?: false
        }

    suspend fun setStayLoggedIn(context: Context, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_STAY_LOGGED_IN] = value
        }
    }
}
