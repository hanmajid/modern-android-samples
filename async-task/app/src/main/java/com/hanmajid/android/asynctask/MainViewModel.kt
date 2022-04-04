package com.hanmajid.android.asynctask

import android.os.AsyncTask
import androidx.lifecycle.ViewModel

/**
 * [MainActivity] ViewModel class.
 *
 * We put [AsyncTask] here so that it will not disappear during configuration changes.
 */
@Suppress("DEPRECATION")
class MainViewModel: ViewModel() {
    var asyncTask: AsyncTask<String, Int, Boolean>? = null
}