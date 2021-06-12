package com.hanmajid.yggr.android.textclassifier.generatelinks

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.textclassifier.TextClassificationManager
import androidx.textclassifier.TextLinks
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add on click listener
        findViewById<Button>(R.id.button_extract_entities).setOnClickListener {
            val inputText = findViewById<TextInputEditText>(R.id.input_text).text.toString()

            // Get the TextClassificationManager instance
            val manager = TextClassificationManager.of(applicationContext)

            // Return if input is blank
            if (inputText.isBlank()) return@setOnClickListener

            // Call `generateLinks` in a coroutine
            lifecycleScope.launch(Dispatchers.IO) {
                // Extract entities from the text input
                val textLinks = manager.textClassifier.generateLinks(
                    TextLinks.Request.Builder(inputText)
                        .setDefaultLocales(LocaleListCompat.create(Locale("en_US")))
                        .build()
                )

                var strEntities = ""
                for (entity in textLinks.links) {
                    val entityText = inputText.substring(entity.start, entity.end)
                    val entityType =
                        entity.getEntityType(0) // It is possible that an `entityText` has multiple types. Here, we only take the first type.
                    strEntities += "($entityType) $entityText\n"
                }

                // Update the UI in the main thread
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.text_entities).text = strEntities
                }
            }
        }
    }
}