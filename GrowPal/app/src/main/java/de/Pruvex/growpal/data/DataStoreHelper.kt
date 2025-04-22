package de.Pruvex.growpal.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Hilfsklasse zur Verwaltung von Einstellungen via DataStore.
 * Aktuell: "Angemeldet bleiben"-Status.
 */
object DataStoreHelper {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val KEY_STAY_LOGGED_IN = booleanPreferencesKey("stay_logged_in")

    /**
     * Setzt den "Angemeldet bleiben"-Status persistent.
     */
    suspend fun setStayLoggedIn(context: Context, value: Boolean) {
        context.dataStore.edit { it[KEY_STAY_LOGGED_IN] = value }
    }

    /**
     * Gibt einen Flow zurück, der den aktuellen "Angemeldet bleiben"-Status liefert.
     */
    fun stayLoggedInFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_STAY_LOGGED_IN] ?: false }
}
