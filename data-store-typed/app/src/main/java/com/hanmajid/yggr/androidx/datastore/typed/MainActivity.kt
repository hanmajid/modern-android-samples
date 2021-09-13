package com.hanmajid.yggr.androidx.datastore.typed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val dataStore: DataStore<UserPreferences> by dataStore(
        fileName = "user_preferences.pb",
        serializer = MyDataStoreTypedSerializer,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDataStoreTyped()
    }

    private fun setupDataStoreTyped() {
        setupIntegerDataStore()
        setupStringDataStore()
        setupBooleanDataStore()
    }

    /**
     * Setup [DataStore] Typed for [Int] field.
     *
     * Every time button is clicked, the integer value is incremented.
     */
    private fun setupIntegerDataStore() {
        val exampleIntFlow = dataStore.data.map { dataStore ->
            dataStore.exampleInt
        }
        lifecycleScope.launch {
            exampleIntFlow.collectLatest {
                findViewById<TextView>(R.id.text_pref_integer).text = it.toString()
            }
        }
        findViewById<Button>(R.id.button_increment_pref_integer).setOnClickListener {
            lifecycleScope.launch {
                dataStore.updateData { dataStore ->
                    val currentValue = dataStore.exampleInt
                    dataStore.toBuilder().setExampleInt(currentValue + 1).build()
                }
            }
        }
    }

    /**
     * Setup [DataStore] Typed for [String] field.
     *
     * Every time the text field value changes, the value is saved to DataStore.
     */
    private fun setupStringDataStore() {
        val exampleStringFlow = dataStore.data.map { dataStore ->
            dataStore.exampleString ?: ""
        }
        lifecycleScope.launch {
            findViewById<TextView>(R.id.text_field_pref_string).text =
                exampleStringFlow.firstOrNull { true }
        }
        findViewById<TextInputEditText>(R.id.text_field_pref_string).addTextChangedListener {
            it?.let {
                lifecycleScope.launch {
                    dataStore.updateData { dataStore ->
                        dataStore.toBuilder().setExampleString(it.toString()).build()
                    }
                }
            }
        }
    }

    /**
     * Setup [DataStore] Typed for [Boolean] field.
     *
     * Every time button is clicked, the boolean value is toggled.
     */
    private fun setupBooleanDataStore() {
        val exampleBooleanFlow = dataStore.data.map { dataStore ->
            dataStore.exampleBool
        }
        lifecycleScope.launch {
            exampleBooleanFlow.collectLatest {
                findViewById<TextView>(R.id.text_pref_boolean).text = it.toString()
            }
        }
        findViewById<Button>(R.id.button_toggle_pref_boolean).setOnClickListener {
            lifecycleScope.launch {
                dataStore.updateData { dataStore ->
                    val currentValue = dataStore.exampleBool
                    dataStore.toBuilder().setExampleBool(!currentValue).build()
                }
            }
        }
    }
}