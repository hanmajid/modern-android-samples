package com.hanmajid.yggr.android.calenderprovider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class EventItemAdapter :
    RecyclerView.Adapter<EventItemAdapter.ViewHolder>() {

    var data: MutableList<EventItem> = mutableListOf()

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun pushData(eventItem: EventItem) {
        data.add(eventItem)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColor: View = view.findViewById(R.id.view_color)
        val textTitle: TextView = view.findViewById(R.id.text_title)
        val textDtStart: TextView = view.findViewById(R.id.text_dt_start)
        val textDtUntil: TextView = view.findViewById(R.id.text_dt_until)
        val textDtEnd: TextView = view.findViewById(R.id.text_dt_end)
        val textDuration: TextView = view.findViewById(R.id.text_duration)
        val textAllDay: TextView = view.findViewById(R.id.text_all_day)
        val textStatus: TextView = view.findViewById(R.id.text_status)
        val textAvailability: TextView = view.findViewById(R.id.text_availability)
        val textVisible: TextView = view.findViewById(R.id.text_visible)
        val textRRule: TextView = view.findViewById(R.id.text_rrule)
        val textLocation: TextView = view.findViewById(R.id.text_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        if (datum.displayColor != null) {
            holder.viewColor.setBackgroundColor(datum.displayColor)
        } else {
            holder.viewColor.isVisible = false
        }
        holder.textTitle.text = datum.title
        holder.textDtStart.text = if (datum.dtStart != null) Date(datum.dtStart).toString() else "-"
        if (datum.dtEnd != null) {
            holder.textDtUntil.isVisible = true
            holder.textDtEnd.text = Date(datum.dtEnd).toString()
        } else {
            holder.textDtUntil.isVisible = false
            holder.textDtEnd.text = ""
        }
        holder.textDuration.text = "(${datum.duration})"
        holder.textAllDay.text = datum.allDay.toString()
        holder.textStatus.text = CalendarUtil.getEventStatusText(datum.status)
        holder.textAvailability.text = CalendarUtil.getEventAvailabilityText(datum.availability)
        holder.textVisible.text = datum.visible.toString()
        holder.textRRule.text = datum.rRule ?: "-"
        holder.textLocation.text = datum.eventLocation ?: "-"
    }

    override fun getItemCount(): Int {
        return data.size
    }
}