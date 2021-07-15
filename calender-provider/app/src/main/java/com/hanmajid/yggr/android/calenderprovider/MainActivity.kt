package com.hanmajid.yggr.android.calenderprovider

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var calendarItemAdapter: CalendarItemAdapter

    companion object {
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_DISPLAY_NAME_INDEX = 1
        private const val PROJECTION_NAME_INDEX = 2
        private const val PROJECTION_CALENDAR_COLOR_INDEX = 3
        private const val PROJECTION_VISIBLE_INDEX = 4
        private const val PROJECTION_SYNC_EVENTS_INDEX = 5
        private const val PROJECTION_ACCOUNT_NAME_INDEX = 6
        private const val PROJECTION_ACCOUNT_TYPE_INDEX = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup permission request launcher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it == true) {
                    getCalendars()
                } else {
                    Toast.makeText(
                        this,
                        "Please allow this app to access your calendar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        // Setup button's onClick event
        findViewById<Button>(R.id.button_do_query).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCalendars()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            }
        }

        // Setup RecyclerView adapter
        calendarItemAdapter = CalendarItemAdapter(this)
        findViewById<RecyclerView>(R.id.recycler_view_calendars).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = calendarItemAdapter
        }
    }

    private fun getCalendars() {
        calendarItemAdapter.clearData()
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
        val cur = contentResolver.query(
            uri,
            EVENT_PROJECTION,
            selection, selectionArgs,
            null,
        )
        while (cur?.moveToNext() == true) {
            val calId = cur.getLong(PROJECTION_ID_INDEX)
            val displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val name = cur.getString(PROJECTION_NAME_INDEX)
            val color = cur.getInt(PROJECTION_CALENDAR_COLOR_INDEX)
            val visible = cur.getInt(PROJECTION_VISIBLE_INDEX)
            val syncEvents = cur.getInt(PROJECTION_SYNC_EVENTS_INDEX)
            val accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            val accountType = cur.getString(PROJECTION_ACCOUNT_TYPE_INDEX)
            calendarItemAdapter.pushData(
                CalendarItem(
                    id = calId,
                    name = name,
                    displayName = displayName,
                    color = color,
                    visible = visible == 1,
                    syncEvents = syncEvents == 1,
                    accountName = accountName,
                    accountType = accountType,
                )
            )
        }
        cur?.close()
    }
}