package com.hanmajid.yggr.android.androidtips1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(
            "shared_pref", Context.MODE_PRIVATE,
        )
        with(sharedPref.edit()) {
            putInt("user_id", 1)
            commit()
        }

        Log.wtf("user_id", sharedPref.getString("user_id", null))
    }
}