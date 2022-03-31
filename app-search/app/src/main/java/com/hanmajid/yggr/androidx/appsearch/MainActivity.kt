package com.hanmajid.yggr.androidx.appsearch

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appsearch.app.*
import androidx.appsearch.exceptions.AppSearchException
import androidx.appsearch.localstorage.LocalStorage
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Open a database
        val sessionFuture = LocalStorage.createSearchSession(
            LocalStorage.SearchContext.Builder(applicationContext, "notes_app")
                .build()
        )

        // Set a schema
        val setSchemaRequest = SetSchemaRequest.Builder()
            .addDocumentClasses(Note::class.java)
            .build()
        val mExecutor = ContextCompat.getMainExecutor(this)
        val setSchemaFuture = Futures.transformAsync(
            sessionFuture,
            { session ->
                session?.setSchema(setSchemaRequest)
            },
            mExecutor,
        )

        // Put a document in the database
        val note = Note(
            namespace = "user1",
            id = "noteId",
            score = 10,
            text = "Buy fresh fruit",
        )
        val putRequest = PutDocumentsRequest.Builder()
            .addDocuments(note)
            .build()
        val putFuture = Futures.transformAsync(
            sessionFuture,
            { session ->
                session?.put(putRequest)
            },
            mExecutor,
        )

        Futures.addCallback(
            putFuture,
            object : FutureCallback<AppSearchBatchResult<String, Void>?> {
                override fun onSuccess(result: AppSearchBatchResult<String, Void>?) {
                    // Gets map of successful results from Id to Void
                    val successfulResults = result?.successes

                    // Gets map of failed results from Id to AppSearchResult
                    val failedResults = result?.failures
                }

                override fun onFailure(t: Throwable) {
                    Log.e("TAG", "Failed to put documents.", t)
                }

            },
            mExecutor,
        )

        // Search
        val searchSpec = SearchSpec.Builder()
            .addFilterNamespaces("user1")
            .build()

        val searchFuture = Futures.transform(
            sessionFuture,
            { session ->
                session?.search("fruit", searchSpec)
            },
            mExecutor,
        )
        Futures.addCallback(
            searchFuture,
            object : FutureCallback<SearchResults?> {
                override fun onSuccess(result: SearchResults?) {
                    iterateSearchResults(result, mExecutor)
                }

                override fun onFailure(t: Throwable) {
                    Log.e("TAG", "Failed to search notes in AppSearch.", t)
                }

            },
            mExecutor,
        )

        val requestFlushFuture = Futures.transformAsync(
            sessionFuture,
            { session -> session?.requestFlush() }, mExecutor
        )
        Futures.addCallback(
            requestFlushFuture,
            object : FutureCallback<Void?> {
                override fun onSuccess(result: Void?) {
                    // Success! Database updates have been persisted to disk.
                }

                override fun onFailure(t: Throwable) {
                    Log.e("TAG", "Failed to flush database updates.", t)
                }
            },
            mExecutor,
        )
    }

    fun iterateSearchResults(searchResults: SearchResults?, mExecutor: Executor) {
        Futures.transform(
            searchResults?.nextPage,
            { page: List<SearchResult>? ->
                // Gets GenericDocument from SearchResult.
                val genericDocument: GenericDocument = page!![0].genericDocument
                val schemaType = genericDocument.schemaType
                val note: Note? = try {
                    if (schemaType == "Note") {
                        // Converts GenericDocument object to Note object.
                        genericDocument.toDocumentClass(Note::class.java)
                    } else null
                } catch (e: AppSearchException) {
                    Log.e("TAG", "Failed to convert GenericDocument to Note", e)
                    null
                }
                Log.wtf("NOTE", note.toString())
            },
            mExecutor,
        )
    }
}