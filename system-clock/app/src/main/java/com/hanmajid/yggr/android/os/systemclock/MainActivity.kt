package com.hanmajid.yggr.android.os.systemclock

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        broadcastReceiver = SystemClockBroadcastReceiver()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        registerReceiver(broadcastReceiver, filter)

        findViewById<TextView>(R.id.text_current_time_millis).apply {
            val millis = System.currentTimeMillis()
            val format = SimpleDateFormat()
            text = format.format(millis)
        }
        findViewById<TextView>(R.id.text_uptime_millis).apply {
            val millis = SystemClock.uptimeMillis()
            val hours = millis / 1000 / 60 / 60
            text = "$millis ms ($hours hours)"
        }
        findViewById<TextView>(R.id.text_elapsed_realtime).apply {
            val millis = SystemClock.elapsedRealtime()
            val hours = millis / 1000 / 60 / 60
            text = "$millis ms ($hours hours)"
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiver)
    }
}