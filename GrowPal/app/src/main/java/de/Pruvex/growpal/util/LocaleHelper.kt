package de.Pruvex.growpal.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val PREFS_NAME = "LocalePrefs"
    private const val TAG = "LocaleHelper" // Tag für Logs

    // Funktion zum Setzen der Sprache: Speichern und Anwenden
    fun setLocale(context: Context, languageCode: String) {
        Log.d(TAG, "setLocale called with languageCode: $languageCode")
        persist(context, languageCode)
        updateAppLocale(languageCode)
        // Hinweis: recreate() der Activity muss von außen aufgerufen werden (z.B. in SettingsScreen)
    }

    // Liest die gespeicherte Sprache aus SharedPreferences
    fun getPersistedLocale(context: Context): String? {
        val prefs = getPrefs(context)
        val lang = prefs.getString(SELECTED_LANGUAGE, null)
        Log.d(TAG, "getPersistedLocale returning: $lang")
        return lang
    }

    // Speichert die gewählte Sprache in SharedPreferences
    private fun persist(context: Context, languageCode: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
        Log.d(TAG, "Persisted language: $languageCode")
    }

    // Wendet die Sprache auf die App via AppCompatDelegate an
    fun updateAppLocale(languageCode: String?) {
        if (languageCode == null) {
            Log.d(TAG, "updateAppLocale called with null, clearing app locales.")
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            logCurrentAppCompatLocales("after clearing")
            return
        }
        Log.d(TAG, "updateAppLocale called with languageCode: $languageCode")
        val locale = Locale(languageCode)
        val localeList = LocaleListCompat.create(locale)
        Log.d(TAG, "Setting app locales to: ${localeList.toLanguageTags()}")
        AppCompatDelegate.setApplicationLocales(localeList)
        logCurrentAppCompatLocales("after set")
    }

    // Hilfsfunktion zum Holen der SharedPreferences
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Loggt die aktuell via AppCompat gesetzte(n) Locale(s)
    private fun logCurrentAppCompatLocales(contextMsg: String) {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        Log.d(TAG, "Current AppCompatDelegate locales $contextMsg: ${currentLocales.toLanguageTags()}")
    }

    // Loggt die Locale des übergebenen Context und die gespeicherte Locale
    fun logCurrentLocale(context: Context, callerTag: String) {
        val contextLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0) // Primäre Locale des Context
        } else {
            context.resources.configuration.locale // Veraltet, aber als Fallback
        }
        val persisted = getPersistedLocale(context) ?: "null"
        val appCompatLocales = AppCompatDelegate.getApplicationLocales().toLanguageTags() ?: "empty"

        Log.d(callerTag, "Context locale: ${contextLocale.toLanguageTag()} | Persisted: $persisted | AppCompat locales: $appCompatLocales")
    }

    // --- NEU: Erstellt einen ContextWrapper mit der gesetzten Locale ---
    fun wrapContext(context: Context): Context {
        val savedLocaleCode = getPersistedLocale(context)
        // Verwende die Standard-Sprache des Systems, wenn nichts gespeichert ist
        val languageCode = savedLocaleCode ?: Locale.getDefault().language
        // Stelle sicher, dass wir einen gültigen Code haben (z.B. "en", "de")
        val validLanguageCode = if (languageCode.isNullOrEmpty()) Locale.getDefault().language else languageCode
        val locale = Locale(validLanguageCode)
        Locale.setDefault(locale) // Sicherstellen, dass die Default-Locale auch gesetzt ist

        val config: Configuration = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale) // Wichtig für Rechts-nach-Links-Sprachen

        Log.d(TAG, "Wrapping context with locale: $validLanguageCode")
        // Erstelle und gib den neuen Context mit der angepassten Konfiguration zurück
        return context.createConfigurationContext(config)
    }
}