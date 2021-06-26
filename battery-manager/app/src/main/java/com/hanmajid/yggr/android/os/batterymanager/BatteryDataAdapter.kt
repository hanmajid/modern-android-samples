package com.hanmajid.yggr.android.os.batterymanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanmajid.yggr.android.os.batterymanager.model.BatteryData
import java.text.SimpleDateFormat

class BatteryDataAdapter(
    private val context: Context,
) : RecyclerView.Adapter<BatteryDataAdapter.ViewHolder>() {

    var data: MutableList<BatteryData> = mutableListOf()

    fun pushData(batteryData: BatteryData) {
        data.add(batteryData)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTimestamp: TextView = view.findViewById(R.id.text_timestamp)
        val iconSmall: ImageView = view.findViewById(R.id.image_icon_small)
        val textHealth: TextView = view.findViewById(R.id.text_health)
        val textStatus: TextView = view.findViewById(R.id.text_status)
        val textLevelScale: TextView = view.findViewById(R.id.text_level_scale)
        val textPlugged: TextView = view.findViewById(R.id.text_plugged)
        val textPresent: TextView = view.findViewById(R.id.text_present)
        val textTechnology: TextView = view.findViewById(R.id.text_technology)
        val textBatteryLow: TextView = view.findViewById(R.id.text_battery_low)
        val textVoltage: TextView = view.findViewById(R.id.text_voltage)
        val textTemperature: TextView = view.findViewById(R.id.text_temperature)

        val textPropertyCapacity: TextView = view.findViewById(R.id.text_property_capacity)
        val textPropertyChargeCounter: TextView =
            view.findViewById(R.id.text_property_charge_counter)
        val textPropertyEnergyCounter: TextView =
            view.findViewById(R.id.text_property_energy_counter)
        val textPropertyCurrentAverage: TextView =
            view.findViewById(R.id.text_property_current_average)
        val textPropertyCurrentNow: TextView = view.findViewById(R.id.text_property_current_now)
        val textPropertyStatus: TextView = view.findViewById(R.id.text_property_status)

        val textIsCharging: TextView = view.findViewById(R.id.text_is_charging)
        val textChargeTimeRemaining: TextView = view.findViewById(R.id.text_charge_time_remaining)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_battery_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = data[position]
        if (datum.iconSmall != -99) {
            holder.iconSmall.setImageDrawable(ContextCompat.getDrawable(context, datum.iconSmall))
        }
        holder.textTimestamp.text = SimpleDateFormat().format(datum.timestamp)
        holder.textHealth.text = BatteryUtil.getBatteryHealthText(datum.health)
        holder.textStatus.text = BatteryUtil.getBatteryStatusText(datum.status)
        holder.textLevelScale.text = "${datum.level} / ${datum.scale}"
        holder.textPlugged.text = BatteryUtil.getBatteryPluggedText(datum.plugged)
        holder.textPresent.text = datum.present.toString()
        holder.textTechnology.text = datum.technology ?: "Unknown"
        holder.textBatteryLow.text = datum.batteryLow?.toString() ?: "Unknown"
        holder.textVoltage.text = datum.voltage.toString()
        holder.textTemperature.text = datum.temperature.toString()

        holder.textPropertyCapacity.text = datum.propertyCapacity.toString()
        holder.textPropertyChargeCounter.text = "${datum.propertyChargeCounter} µAh"
        holder.textPropertyEnergyCounter.text = "${datum.propertyEnergyCounter} nWh"
        holder.textPropertyCurrentAverage.text = "${datum.propertyCurrentAverage} µA"
        holder.textPropertyCurrentNow.text = "${datum.propertyCurrentNow} µA"
        holder.textPropertyStatus.text =
            BatteryUtil.getBatteryStatusText(datum.propertyStatus ?: -99)

        holder.textIsCharging.text =
            if (datum.isCharging != null) datum.isCharging.toString() else "-"
        holder.textChargeTimeRemaining.text =
            if (datum.chargeTimeRemaining != null) "${datum.chargeTimeRemaining} ms" else "-"
    }

    override fun getItemCount(): Int {
        return data.size
    }
}