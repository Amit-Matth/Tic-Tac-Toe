package com.amitmatth.tictactoe

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.lifecycle.asLiveData

// Extension property to get the DataStore instance
val Context.dataStore by preferencesDataStore("player_stats")

class PlayerPreferencesManager(private val context: Context) {

    // Keys for storing data
    private val ARE_NAMES_SET = booleanPreferencesKey("are_names_set")
    private val PLAYER_X_NAME = stringPreferencesKey("player_x_name")
    private val PLAYER_O_NAME = stringPreferencesKey("player_o_name")
    private val TOTAL_MATCHES = intPreferencesKey("total_matches")
    private val PLAYER_X_WINS = intPreferencesKey("player_x_wins")
    private val PLAYER_O_WINS = intPreferencesKey("player_o_wins")
    private val DRAW_MATCHES = intPreferencesKey("draw_matches")

    // Functions to get stored values as Flow
    val areNamesSet: Flow<Boolean> = context.dataStore.data.map { it[ARE_NAMES_SET] ?: false }
    val playerXName: Flow<String> = context.dataStore.data.map { it[PLAYER_X_NAME] ?: "" }
    val playerOName: Flow<String> = context.dataStore.data.map { it[PLAYER_O_NAME] ?: "" }
    val totalMatches: Flow<Int> = context.dataStore.data.map { it[TOTAL_MATCHES] ?: 0 }
    val playerXWins: Flow<Int> = context.dataStore.data.map { it[PLAYER_X_WINS] ?: 0 }
    val playerOWins: Flow<Int> = context.dataStore.data.map { it[PLAYER_O_WINS] ?: 0 }
    val drawMatches: Flow<Int> = context.dataStore.data.map { it[DRAW_MATCHES] ?: 0 }

    val areNamesSetLiveData = areNamesSet.asLiveData()
    val playerXNameLiveData = playerXName.asLiveData()
    val playerONameLiveData = playerOName.asLiveData()
    val totalMatchesLiveData = totalMatches.asLiveData()
    val playerXWinsLiveData = playerXWins.asLiveData()
    val playerOWinsLiveData = playerOWins.asLiveData()
    val drawMatchesLiveData = drawMatches.asLiveData()

    // Function to save player names
    suspend fun savePlayerNames(playerX: String, playerO: String) {
        context.dataStore.edit { preferences ->
            preferences[PLAYER_X_NAME] = playerX
            preferences[PLAYER_O_NAME] = playerO
            preferences[ARE_NAMES_SET] = true // Mark names as set
        }
    }

    suspend fun resetPlayerStats() {
        context.dataStore.edit { preferences ->
            preferences[ARE_NAMES_SET] = false // Reset the flag
            preferences[TOTAL_MATCHES] = 0
            preferences[PLAYER_X_WINS] = 0
            preferences[PLAYER_O_WINS] = 0
            preferences[DRAW_MATCHES] = 0
        }
    }


    // Function to update match statistics
    suspend fun updateMatchStats(
        isXWinner: Boolean? = null,
        isDraw: Boolean = false
    ) {
        context.dataStore.edit { preferences ->
            val currentMatches = preferences[TOTAL_MATCHES] ?: 0
            preferences[TOTAL_MATCHES] = currentMatches + 1

            if (isXWinner == true) {
                val currentXWins = preferences[PLAYER_X_WINS] ?: 0
                preferences[PLAYER_X_WINS] = currentXWins + 1
            } else if (isXWinner == false) {
                val currentOWins = preferences[PLAYER_O_WINS] ?: 0
                preferences[PLAYER_O_WINS] = currentOWins + 1
            } else if (isDraw) {
                val currentDraws = preferences[DRAW_MATCHES] ?: 0
                preferences[DRAW_MATCHES] = currentDraws + 1
            }
        }
    }
}
