package com.lokavo.data.repository

import com.lokavo.data.preferences.SwitchThemePreferences
import kotlinx.coroutines.flow.Flow

class ProfileRepository private constructor(
    private val preferences: SwitchThemePreferences
) {
    fun getThemeSetting() : Flow<Boolean> = preferences.getThemeSetting()
    suspend fun saveThemeSetting(isDarkModeActive : Boolean) {
        preferences.saveThemeSetting(isDarkModeActive)
    }
    companion object {
        @Volatile
        private var instance: ProfileRepository? = null
        fun getInstance(
            preferences: SwitchThemePreferences
        ) : ProfileRepository =
            instance ?: synchronized(this) {
                instance ?: ProfileRepository(preferences)
            }.also { instance = it }
    }
}