package com.example.timerapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Репозиторий для хранения настроек приложения через DataStore Preferences
 */
class SettingsRepository(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        
        // Ключи для настроек
        val SOUND_URI_KEY = stringPreferencesKey("sound_uri")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val TIMER_A_NAME_KEY = stringPreferencesKey("timer_a_name")
        val TIMER_B_NAME_KEY = stringPreferencesKey("timer_b_name")
    }
    
    /**
     * Поток URI звука уведомления
     */
    val soundUriFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SOUND_URI_KEY]
        }
    
    /**
     * Поток режима темы (system, light, dark)
     */
    val themeModeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: "system"
        }
    
    /**
     * Сохранение URI звука
     */
    suspend fun saveSoundUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_URI_KEY] = uri
        }
    }
    
    /**
     * Сохранение режима темы
     */
    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }
    
    /**
     * Получение имени таймера A
     */
    val timerANameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TIMER_A_NAME_KEY] ?: "Timer A"
        }
    
    /**
     * Получение имени таймера B
     */
    val timerBNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TIMER_B_NAME_KEY] ?: "Timer B"
        }
    
    /**
     * Сохранение имени таймера A
     */
    suspend fun saveTimerAName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[TIMER_A_NAME_KEY] = name
        }
    }
    
    /**
     * Сохранение имени таймера B
     */
    suspend fun saveTimerBName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[TIMER_B_NAME_KEY] = name
        }
    }
}
