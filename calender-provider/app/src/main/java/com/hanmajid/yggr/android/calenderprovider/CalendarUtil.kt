package com.hanmajid.yggr.android.calenderprovider

import android.provider.CalendarContract

class CalendarUtil {

    companion object {

        fun getEventStatusText(status: Int?): String {
            return when (status) {
                CalendarContract.Events.STATUS_TENTATIVE -> "Tentative"
                CalendarContract.Events.STATUS_CONFIRMED -> "Confirmed"
                CalendarContract.Events.STATUS_CANCELED -> "Canceled"
                else -> "Unknown"
            }
        }

        fun getEventAvailabilityText(availability: Int?): String {
            return when (availability) {
                CalendarContract.Events.AVAILABILITY_BUSY -> "Busy"
                CalendarContract.Events.AVAILABILITY_FREE -> "Free"
                CalendarContract.Events.AVAILABILITY_TENTATIVE -> "Tentative"
                else -> "Unknown"
            }
        }
    }
}