@file:Suppress("DEPRECATION")

package com.hanmajid.android.asynctask

import android.os.AsyncTask
import androidx.lifecycle.ViewModel

/**
 * [MainActivity] ViewModel class.
 *
 * We put [AsyncTask] here so that it will not disappear during configuration changes.
 */
class MainViewModel : ViewModel() {
    var downloadFilesTask: AsyncTask<String, Int, Boolean>? = null
}