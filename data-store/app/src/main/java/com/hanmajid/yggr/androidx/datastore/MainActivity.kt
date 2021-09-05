package com.hanmajid.yggr.androidx.datastore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /**
     * Get [DataStore] object.
     */
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_ds_pref")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDataStorePreferences()
    }

    /**
     * Setup [DataStore] Preferences components.
     */
    private fun setupDataStorePreferences() {
        setupIntegerPref()
        setupStringPref()
        setupBooleanPref()
    }

    /**
     * Setup [DataStore] Preferences for [Int] field using [intPreferencesKey].
     *
     * Every time button is clicked, the integer value is incremented.
     */
    private fun setupIntegerPref() {
        val prefInteger = intPreferencesKey("pref_integer")
        val prefIntegerFlow = dataStore.data.map { preferences ->
            preferences[prefInteger] ?: 0
        }
        lifecycleScope.launch {
            prefIntegerFlow.collectLatest {
                findViewById<TextView>(R.id.text_pref_integer).text = it.toString()
            }
        }
        findViewById<Button>(R.id.button_increment_pref_integer).setOnClickListener {
            lifecycleScope.launch {
                dataStore.edit { preferences ->
                    val currentValue = preferences[prefInteger] ?: 0
                    preferences[prefInteger] = currentValue + 1
                }
            }
        }
    }

    /**
     * Setup [DataStore] Preferences for [String] field using [stringPreferencesKey].
     *
     * Every time the text field value changes, the value is saved to DataStore.
     */
    private fun setupStringPref() {
        val prefString = stringPreferencesKey("pref_string")
        val prefStringFlow = dataStore.data.map { preferences ->
            preferences[prefString] ?: ""
        }
        lifecycleScope.launch {
            findViewById<TextView>(R.id.text_field_pref_string).text =
                prefStringFlow.firstOrNull { true }
        }
        findViewById<TextInputEditText>(R.id.text_field_pref_string).addTextChangedListener {
            it?.let {
                lifecycleScope.launch {
                    dataStore.edit { preferences ->
                        preferences[prefString] = it.toString()
                    }
                }
            }
        }
    }

    /**
     * Setup [DataStore] Preferences for [Boolean] field using [booleanPreferencesKey].
     *
     * Every time button is clicked, the boolean value is toggled.
     */
    private fun setupBooleanPref() {
        val prefBoolean = booleanPreferencesKey("pref_boolean")
        val prefBooleanFlow = dataStore.data.map { preferences ->
            preferences[prefBoolean] ?: false
        }
        lifecycleScope.launch {
            prefBooleanFlow.collectLatest {
                findViewById<TextView>(R.id.text_pref_boolean).text = it.toString()
            }
        }
        findViewById<Button>(R.id.button_toggle_pref_boolean).setOnClickListener {
            lifecycleScope.launch {
                dataStore.edit { preferences ->
                    val currentValue = preferences[prefBoolean] ?: false
                    preferences[prefBoolean] = !currentValue
                }
            }
        }
    }
}