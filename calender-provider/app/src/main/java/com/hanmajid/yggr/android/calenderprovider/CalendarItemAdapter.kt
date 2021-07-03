package com.hanmajid.yggr.android.calenderprovider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarItemAdapter() : RecyclerView.Adapter<CalendarItemAdapter.ViewHolder>() {

    var data: MutableList<CalendarItem> = mutableListOf()

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun pushData(calendarItem: CalendarItem) {
        data.add(calendarItem)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColor: View = view.findViewById(R.id.view_color)
        val textDisplayName: TextView = view.findViewById(R.id.text_display_name)
        val textAccountName: TextView = view.findViewById(R.id.text_account_name)
        val textAccountType: TextView = view.findViewById(R.id.text_account_type)
        val textVisible: TextView = view.findViewById(R.id.text_visible)
        val textSyncEvents: TextView = view.findViewById(R.id.text_sync_events)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        holder.viewColor.setBackgroundColor(datum.color)
        holder.textDisplayName.text = datum.displayName
        holder.textAccountName.text = datum.accountName
        holder.textAccountType.text = "(${datum.accountType})"
        holder.textVisible.text = datum.visible.toString()
        holder.textSyncEvents.text = datum.syncEvents.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}