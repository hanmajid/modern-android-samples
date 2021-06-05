package com.hanmajid.yggr.android.textclassifier.classifytext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.textclassifier.TextClassification
import androidx.textclassifier.TextClassificationManager
import androidx.textclassifier.TextClassifier
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add on click listener
        findViewById<Button>(R.id.button_generate_actions).setOnClickListener {
            val inputText = findViewById<TextInputEditText>(R.id.input_text).text.toString()

            // Get the TextClassificationManager instance
            val manager = TextClassificationManager.of(applicationContext)

            // Return if input is blank
            if (inputText.isBlank()) return@setOnClickListener

            // Call `classifyText` in a coroutine
            lifecycleScope.launch(Dispatchers.IO) {
                // Classify the text input
                val textSelection = manager.textClassifier.classifyText(
                    TextClassification.Request.Builder(inputText, 0, inputText.length)
                        .setDefaultLocales(LocaleListCompat.getDefault())
                        .build()
                )

                // Get the confidence score for each text input type
                val types = listOf(
                    TextClassifier.TYPE_ADDRESS,
                    TextClassifier.TYPE_DATE,
                    TextClassifier.TYPE_DATE_TIME,
                    TextClassifier.TYPE_EMAIL,
                    TextClassifier.TYPE_FLIGHT_NUMBER,
                    TextClassifier.TYPE_OTHER,
                    TextClassifier.TYPE_PHONE,
                    TextClassifier.TYPE_URL,
                    TextClassifier.TYPE_UNKNOWN,
                )
                var strConfidences = ""
                for (type in types) {
                    strConfidences += "${if (type.isNotBlank()) type else "unknown"}: ${
                        textSelection.getConfidenceScore(
                            type
                        )
                    }\n"
                }

                // Update the UI in the main thread
                withContext(Dispatchers.Main) {
                    findViewById<RecyclerView>(R.id.recycler_view_action)?.apply {
                        layoutManager =
                            LinearLayoutManager(applicationContext)
                        adapter = RemoteActionListAdapter(applicationContext, textSelection.actions)
                    }
                    findViewById<TextView>(R.id.text_confidence).text = strConfidences
                }
            }
        }
    }
}