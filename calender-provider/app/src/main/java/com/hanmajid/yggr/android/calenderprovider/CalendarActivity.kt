package com.hanmajid.yggr.android.calenderprovider

import android.os.Bundle
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarActivity : AppCompatActivity() {
    private lateinit var eventItemAdapter: EventItemAdapter

    companion object {
        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.STATUS,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.DISPLAY_COLOR,
            CalendarContract.Events.VISIBLE,
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_TITLE_INDEX = 1
        private const val PROJECTION_EVENT_LOCATION_INDEX = 2
        private const val PROJECTION_STATUS_INDEX = 3
        private const val PROJECTION_DTSTART_INDEX = 4
        private const val PROJECTION_DTEND_INDEX = 5
        private const val PROJECTION_DURATION_INDEX = 6
        private const val PROJECTION_ALL_DAY_INDEX = 7
        private const val PROJECTION_AVAILABILITY_INDEX = 8
        private const val PROJECTION_RRULE_INDEX = 9
        private const val PROJECTION_DISPLAY_COLOR_INDEX = 10
        private const val PROJECTION_VISIBLE_INDEX = 11
        const val EXTRA_CALENDAR_ID = "com.hanmajid.yggr.android.calenderprovider.calendaractivity.extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarId = intent.getStringExtra(EXTRA_CALENDAR_ID)

        // Setup RecyclerView adapter
        eventItemAdapter = EventItemAdapter()
        findViewById<RecyclerView>(R.id.recycler_view_events).let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = eventItemAdapter
        }

        if (calendarId != null) {
            getEvents(calendarId)
        }
    }

    private fun getEvents(calendarId: String) {
        eventItemAdapter.clearData()
        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?)"
        val selectionArgs = arrayOf(calendarId)
        val cur = contentResolver.query(
            uri,
            EVENT_PROJECTION,
            selection, selectionArgs,
            null,
        )
        while (cur?.moveToNext() == true) {
            val eventId = cur.getLong(PROJECTION_ID_INDEX)
            val title = cur.getStringOrNull(PROJECTION_TITLE_INDEX)
            val eventLocation = cur.getStringOrNull(PROJECTION_EVENT_LOCATION_INDEX)
            val status = cur.getIntOrNull(PROJECTION_STATUS_INDEX)
            val dtStart = cur.getLongOrNull(PROJECTION_DTSTART_INDEX)
            val dtEnd = cur.getLongOrNull(PROJECTION_DTEND_INDEX)
            val duration = cur.getStringOrNull(PROJECTION_DURATION_INDEX)
            val allDay = cur.getIntOrNull(PROJECTION_ALL_DAY_INDEX) == 1
            val availability = cur.getIntOrNull(PROJECTION_AVAILABILITY_INDEX)
            val rRule = cur.getStringOrNull(PROJECTION_RRULE_INDEX)
            val displayColor = cur.getIntOrNull(PROJECTION_DISPLAY_COLOR_INDEX)
            val visible = cur.getIntOrNull(PROJECTION_VISIBLE_INDEX) == 1

            eventItemAdapter.pushData(
                EventItem(
                    id = eventId,
                    title = title,
                    eventLocation = eventLocation,
                    status = status,
                    dtStart = dtStart,
                    dtEnd = dtEnd,
                    duration = duration,
                    allDay = allDay,
                    availability = availability,
                    rRule = rRule,
                    displayColor = displayColor,
                    visible = visible,
                )
            )
        }
        cur?.close()
    }
}