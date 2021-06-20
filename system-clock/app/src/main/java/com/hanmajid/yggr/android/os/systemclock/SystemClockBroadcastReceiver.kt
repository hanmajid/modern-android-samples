package com.hanmajid.yggr.android.os.systemclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SystemClockBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.wtf("action", intent.action)
        Log.wtf("action", intent.data.toString())
        Log.wtf("action", intent.type)
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val tz: TimeZone = TimeZone.getDefault()
                Log.wtf("TZ", tz.displayName)
            }
            Intent.ACTION_TIME_CHANGED -> {
                val calendar: Calendar = Calendar.getInstance()
                val format = SimpleDateFormat()
                Log.wtf("CAL", format.format(calendar.time))
            }
            else -> {
                val tz: TimeZone = TimeZone.getDefault()
                Log.wtf("TZ", tz.displayName)
                val calendar: Calendar = Calendar.getInstance()
                val format = SimpleDateFormat()
                Log.wtf("CAL", format.format(calendar.time))
            }
        }
    }
}