package com.zidan.myapplicationstory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zidan.myapplicationstory.response.LoginResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataPreferences @Inject constructor(@ApplicationContext val context: Context) {

    private val dataStore = context.dataStore

    fun getDataSession() : Flow<LoginResult> {
        return dataStore.data.map { preference ->
            LoginResult(
                preference[NAME_KEY] ?:"",
                preference[USER_ID_KEY] ?:"",
                preference[TOKEN_KEY] ?:""
            )
        }
    }

    fun getDataSetting(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                userId = preferences[USER_ID_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveDataSetting(dataSetting: LoginResult) {
        wrapEspressoIdlingResource {
            dataStore.edit { preferences ->
                preferences[USER_ID_KEY] = dataSetting.userId
                preferences[NAME_KEY] = dataSetting.name
                preferences[TOKEN_KEY] = dataSetting.token
            }
        }
    }

    suspend fun clearDataSetting() {
        dataStore.edit {
            it.clear()
        }
    }
companion object {
    private val USER_ID_KEY = stringPreferencesKey("id")
    private val NAME_KEY = stringPreferencesKey("name")
    private val TOKEN_KEY = stringPreferencesKey("token")
}
}