package com.hanmajid.android.asynctask

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var broadcastReceiver: DownloadFilesBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        broadcastReceiver = DownloadFilesBroadcastReceiver { progress, isFinished, isCanceled ->
            progress?.let {
                findViewById<LinearProgressIndicator>(R.id.linear_progress_indicator).progress = it
            }
            if (isFinished) {
                Toast.makeText(this, "Finished!", Toast.LENGTH_SHORT).show()
            }
            if (isCanceled) {
                Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.button_start).setOnClickListener {
            viewModel.asyncTask?.cancel(true)
            viewModel.asyncTask = DownloadFilesTask(this)
            viewModel.asyncTask?.execute("")
        }
        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            viewModel.asyncTask?.cancel(true)
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(broadcastReceiver, IntentFilter(DOWNLOAD_FILES_INTENT))
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        const val DOWNLOAD_FILES_INTENT = "com.hanmajid.android.asynctask.intent"
        const val DOWNLOAD_FILES_INTENT_PROGRESS = "com.hanmajid.android.asynctask.intent.progress"
        const val DOWNLOAD_FILES_INTENT_FINISHED = "com.hanmajid.android.asynctask.intent.finished"
        const val DOWNLOAD_FILES_INTENT_CANCELED = "com.hanmajid.android.asynctask.intent.canceled"
    }

    /**
     * Broadcast Receiver that receives data from [DownloadFilesTask].
     */
    private class DownloadFilesBroadcastReceiver(
        private val onReceiveProgress: (progress: Int?, isFinished: Boolean, isCanceled: Boolean) -> Unit,
    ) :
        BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DOWNLOAD_FILES_INTENT) {
                val progress = intent.getIntExtra(DOWNLOAD_FILES_INTENT_PROGRESS, 0)
                val isFinished = intent.getBooleanExtra(DOWNLOAD_FILES_INTENT_FINISHED, false)
                val isCanceled = intent.getBooleanExtra(DOWNLOAD_FILES_INTENT_CANCELED, false)
                onReceiveProgress(progress, isFinished, isCanceled)
            }
        }
    }

    /**
     * This class simulates downloading file by sending broadcast of download progress.
     *
     * It also broadcast whether the task is finished and/or canceled.
     */
    private class DownloadFilesTask(_context: Activity) :
        AsyncTask<String, Int, Boolean>() {

        private var context: WeakReference<Activity> = WeakReference(_context)
        private var progress: Int = 0

        /**
         * Invoked on UI thread before executing the task.
         */
        override fun onPreExecute() {
            super.onPreExecute()

            context.get()?.let {
                Intent().also { intent ->
                    intent.action = DOWNLOAD_FILES_INTENT
                    intent.putExtra(DOWNLOAD_FILES_INTENT_PROGRESS, 0)
                    it.sendBroadcast(intent)
                }

            }
        }

        /**
         * Invoked on background thread to perform the long computation.
         *
         * [publishProgress] is invoked on the background thread.
         */
        override fun doInBackground(vararg urls: String?): Boolean {
            progress = 0
            while (progress < 100) {
                Thread.sleep(1000)
                progress += 10
                publishProgress(progress)
                if (isCancelled) break
            }
            return true
        }

        /**
         * Invoked on UI thread after a call to [publishProgress].
         */
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            if (values.size == 1) {
                context.get()?.let {
                    Intent().also { intent ->
                        intent.action = DOWNLOAD_FILES_INTENT
                        intent.putExtra(DOWNLOAD_FILES_INTENT_PROGRESS, values[0]!!)
                        it.sendBroadcast(intent)
                    }
                }
            }
        }

        /**
         * Invoked on UI thread after [cancel] is invoked and [doInBackground] has finished.
         */
        override fun onCancelled(result: Boolean?) {
            super.onCancelled(result)

            context.get()?.let {
                Intent().also { intent ->
                    intent.action = DOWNLOAD_FILES_INTENT
                    intent.putExtra(DOWNLOAD_FILES_INTENT_PROGRESS, progress)
                    intent.putExtra(DOWNLOAD_FILES_INTENT_CANCELED, true)
                    it.sendBroadcast(intent)
                }
            }
        }

        /**
         * Invoked on UI thread after background computation finishes.
         */
        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            context.get()?.let {
                Intent().also { intent ->
                    intent.action = DOWNLOAD_FILES_INTENT
                    intent.putExtra(DOWNLOAD_FILES_INTENT_PROGRESS, progress)
                    intent.putExtra(DOWNLOAD_FILES_INTENT_FINISHED, true)
                    it.sendBroadcast(intent)
                }
            }
        }

    }
}