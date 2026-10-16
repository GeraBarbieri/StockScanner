package com.stockscanner.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "stock_scanner_prefs")

class AppDataStore(private val context: Context) {

    companion object {
        val BUSINESS_NAME_KEY = stringPreferencesKey("business_name")
        val EXCEL_PATH_KEY    = stringPreferencesKey("excel_path")
    }

    val businessName: Flow<String?> = context.dataStore.data.map { it[BUSINESS_NAME_KEY] }
    val excelPath: Flow<String?>    = context.dataStore.data.map { it[EXCEL_PATH_KEY] }

    suspend fun saveBusinessName(name: String) {
        context.dataStore.edit { it[BUSINESS_NAME_KEY] = name }
    }

    suspend fun saveExcelPath(path: String) {
        context.dataStore.edit { it[EXCEL_PATH_KEY] = path }
    }
}
