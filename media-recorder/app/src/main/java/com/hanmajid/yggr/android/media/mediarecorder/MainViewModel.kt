package com.hanmajid.yggr.android.media.mediarecorder

import android.media.MediaRecorder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val refreshCounter = MutableLiveData(0)
    var mediaRecorder: MediaRecorder? = null

    init {
        mediaRecorder = initConfigMediaRecorder()
        refresh()
    }

    /**
     * Initialize & configure [MediaRecorder] except [MediaRecorder.setOutputFile].
     */
    private fun initConfigMediaRecorder(): MediaRecorder {
        return MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
    }

    /**
     * Increment [refreshCounter].
     */
    fun refresh() {
        refreshCounter.value?.let {
            refreshCounter.postValue(it + 1)
        }
    }

    val audioSourceMax = Transformations.map(refreshCounter) {
        if (it > 0) {
            MediaRecorder.getAudioSourceMax().toString()
        } else {
            "-"
        }
    }
    val maxAmplitude = Transformations.map(refreshCounter) {
        if (it > 0) {
            mediaRecorder?.maxAmplitude.toString()
        } else {
            "-"
        }
    }
}