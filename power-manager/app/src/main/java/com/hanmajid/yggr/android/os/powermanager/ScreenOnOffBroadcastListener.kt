package com.hanmajid.yggr.android.os.powermanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenOnOffBroadcastListener(val onValueChange: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        onValueChange()
    }
}