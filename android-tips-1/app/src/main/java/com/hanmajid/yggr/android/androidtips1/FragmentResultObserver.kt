package com.hanmajid.yggr.android.androidtips1

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class FragmentResultObserver(
    private val fragment: Fragment,
    private val onResult: (bundle: Bundle) -> Unit,
) :
    LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun setListener() {
        fragment.setFragmentResultListener("request_key") { _, bundle ->
            onResult(bundle)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun clearListener() {
        fragment.clearFragmentResultListener("request_key")
    }
}