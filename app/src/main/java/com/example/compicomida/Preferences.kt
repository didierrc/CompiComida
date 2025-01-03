package com.example.compicomida

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.compicomida.CompiComidaApp.Companion.appModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "prefs_daily_recipe"
)

class PreferencesDailyRecipe(
    private val dataStore: DataStore<Preferences>,
) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    companion object {
        val KEY_RECIPE = stringPreferencesKey("recipe")
        val KEY_LAST_DATE = stringPreferencesKey("last_date")
    }

    val recipe: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_RECIPE].toString()
    }


    private val lastDate: Flow<String> = dataStore.data
        .map { prefs ->
            prefs[KEY_LAST_DATE] ?: LocalDate.now()
                .format(formatter)
        }

    suspend fun isRecipeToUpdate(): Boolean {
        if (dataStore.data.first()[KEY_RECIPE] == null)
            return true

        val currentDate = LocalDate.now()
        val storedDate = lastDate.first()

        val dateDifference = appModule.getDateDifference(
            LocalDate.parse(
                storedDate,
                formatter
            ), currentDate
        )
        return dateDifference >= 1
    }

    private suspend fun updateRecipe(id: String) {
        dataStore.edit { prefs ->
            prefs[KEY_RECIPE] = id
        }

    }

    private suspend fun updateLastDate(newDate: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_DATE] = newDate
        }
    }

    suspend fun saveRecipe(id: String) {
        updateRecipe(id)
        updateLastDate(LocalDate.now().format(formatter))
    }
}