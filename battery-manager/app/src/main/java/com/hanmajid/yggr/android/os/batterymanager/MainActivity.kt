package com.hanmajid.yggr.android.os.batterymanager

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hanmajid.yggr.android.os.batterymanager.model.BatteryData
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var recyclerView: RecyclerView
    private lateinit var batteryDataAdapter: BatteryDataAdapter

    private val batteryManager: BatteryManager by lazy {
        getSystemService(BATTERY_SERVICE) as BatteryManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        batteryDataAdapter = BatteryDataAdapter(applicationContext)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_battery_data).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = batteryDataAdapter
        }

        broadcastReceiver = BatteryManagerBroadcastReceiver {
            val propertyCapacity =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            val propertyChargeCounter =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            val propertyEnergyCounter =
                batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
            val propertyCurrentAverage =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            val propertyCurrentNow =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            var propertyStatus: Int? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                propertyStatus = batteryManager.getIntProperty(
                    BatteryManager.BATTERY_PROPERTY_STATUS
                )
            }

            var isCharging: Boolean? = null
            var chargeTimeRemaining: Long? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isCharging = batteryManager.isCharging
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                chargeTimeRemaining = batteryManager.computeChargeTimeRemaining()
            }

            val iconSmall = it.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -99)
            val health = it.getIntExtra(BatteryManager.EXTRA_HEALTH, -99)
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -99)
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -99)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -99)
            val plugged = it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -99)
            val present = it.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
            val technology = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            val batteryLow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false)
            } else {
                null
            }
            val voltage = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -99)
            val temperature = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -99)

            batteryDataAdapter.pushData(
                BatteryData(
                    timestamp = Date(),
                    iconSmall = iconSmall,
                    action = it.action.toString(),
                    health = health,
                    status = status,
                    level = level,
                    scale = scale,
                    plugged = plugged,
                    present = present,
                    technology = technology,
                    batteryLow = batteryLow,
                    voltage = voltage,
                    temperature = temperature,
                    propertyCapacity = propertyCapacity,
                    propertyChargeCounter = propertyChargeCounter,
                    propertyEnergyCounter = propertyEnergyCounter,
                    propertyCurrentAverage = propertyCurrentAverage,
                    propertyCurrentNow = propertyCurrentNow,
                    propertyStatus = propertyStatus,
                    isCharging = isCharging,
                    chargeTimeRemaining = chargeTimeRemaining,
                )
            )
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiver)
    }
}