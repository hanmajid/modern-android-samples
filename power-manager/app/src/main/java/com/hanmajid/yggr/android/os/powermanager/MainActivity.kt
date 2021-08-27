package com.hanmajid.yggr.android.os.powermanager

import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.Intent.ACTION_SCREEN_ON
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var thermalStatusListener: PowerManager.OnThermalStatusChangedListener
    private lateinit var deviceIdleModeBroadcastReceiver: DeviceIdleModeBroadcastReceiver
    private lateinit var powerSaveModeBroadcastReceiver: PowerSaveModeBroadcastReceiver
    private lateinit var screenOnOffBroadcastReceiver: ScreenOnOffBroadcastListener

    // Get the Power Manager service
    private val powerManager: PowerManager by lazy {
        getSystemService(POWER_SERVICE) as PowerManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        // Thermal status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermalStatusListener = PowerManager.OnThermalStatusChangedListener {
                findViewById<TextView>(R.id.text_thermal_status).text =
                    getThermalStatusName(it)
            }
            powerManager.addThermalStatusListener(thermalStatusListener)
        }

        // Thermal Headroom
        findViewById<TextView>(R.id.text_thermal_headroom).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                powerManager.getThermalHeadroom(30).toString()
            } else {
                "Required API level >= 30"
            }

        // Power Save Mode
        refreshIsPowerSaveModeText()
        powerSaveModeBroadcastReceiver = PowerSaveModeBroadcastReceiver {
            refreshIsPowerSaveModeText()
        }
        val powerSaveModeFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        registerReceiver(powerSaveModeBroadcastReceiver, powerSaveModeFilter)

        // Location Mode Power Save Mode
        findViewById<TextView>(R.id.text_location_power_save_mode).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getLocationModeName(powerManager.locationPowerSaveMode)
            } else {
                "Required API level >= 28"
            }

        // Idle Mode
        refreshIsIdleModeText()
        deviceIdleModeBroadcastReceiver = DeviceIdleModeBroadcastReceiver {
            refreshIsIdleModeText()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val idleModeFilter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
            registerReceiver(deviceIdleModeBroadcastReceiver, idleModeFilter)
        } else {
            makeToast("Required API level >= 23")
        }

        // Ignoring Battery Optimization
        findViewById<TextView>(R.id.text_is_ignoring_battery_optimizations).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager.isIgnoringBatteryOptimizations(packageName).toString()
            } else {
                "Required API level >= 23"
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById<Button>(R.id.button_request_allow_list).isEnabled =
                !powerManager.isIgnoringBatteryOptimizations(packageName)
        }
        findViewById<Button>(R.id.button_request_allow_list).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivity(
                    Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:$packageName")
                    }
                )
            } else {
                makeToast("Required API level >= 23")
            }
        }

        // Interactive State
        refreshIsInteractiveText()
        screenOnOffBroadcastReceiver = ScreenOnOffBroadcastListener {
            refreshIsInteractiveText()
        }
        val filterScreenOnOff = IntentFilter().apply {
            addAction(ACTION_SCREEN_ON)
            addAction(ACTION_SCREEN_OFF)
        }
        registerReceiver(screenOnOffBroadcastReceiver, filterScreenOnOff)

        // Rebooting
        findViewById<TextView>(R.id.text_is_rebooting_userspace_supported).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                powerManager.isRebootingUserspaceSupported.toString()
            } else {
                "Required API level >= 30"
            }

        // Sustained Performance Mode
        findViewById<TextView>(R.id.text_is_sustained_performance_mode_supported).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                powerManager.isSustainedPerformanceModeSupported.toString()
            } else {
                "Required API level >= 24"
            }

        // Wake Full
        findViewById<Button>(R.id.button_wake_lock_full).setOnClickListener {
            if (powerManager.isWakeLockLevelSupported(PowerManager.FULL_WAKE_LOCK)) {
                activateWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP)
            } else {
                makeToast("FULL_WAKE_LOCK not supported")
            }
        }
        // Screen Dim
        findViewById<Button>(R.id.button_wake_lock_screen_dim).setOnClickListener {
            if (powerManager.isWakeLockLevelSupported(PowerManager.SCREEN_DIM_WAKE_LOCK)) {
                activateWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP)
            } else {
                makeToast("SCREEN_DIM_WAKE_LOCK not supported")
            }
        }
        // Screen Bright
        findViewById<Button>(R.id.button_wake_lock_screen_bright).setOnClickListener {
            if (powerManager.isWakeLockLevelSupported(PowerManager.SCREEN_BRIGHT_WAKE_LOCK)) {
                activateWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP)
            } else {
                makeToast("SCREEN_BRIGHT_WAKE_LOCK not supported")
            }
        }
        // Wake Lock Partial
        findViewById<Button>(R.id.button_wake_lock_partial).setOnClickListener {
            if (powerManager.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK)) {
                activateWakeLock(PowerManager.PARTIAL_WAKE_LOCK, 0)
            } else {
                makeToast("PARTIAL_WAKE_LOCK not supported")
            }
        }
        // Wake Lock Proximity
        findViewById<Button>(R.id.button_wake_lock_proximity).setOnClickListener {
            if (powerManager.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {
                activateWakeLock(
                    PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    0,
                )
            } else {
                makeToast("PROXIMITY_SCREEN_OFF_WAKE_LOCK not supported")
            }
        }
    }

    private fun refreshIsIdleModeText() {
        findViewById<TextView>(R.id.text_is_device_idle_mode).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager.isDeviceIdleMode.toString()
            } else {
                "Required API level ?>= 23"
            }
    }

    private fun refreshIsPowerSaveModeText() {
        findViewById<TextView>(R.id.text_is_power_save_mode).text =
            powerManager.isPowerSaveMode.toString()
    }

    private fun refreshIsInteractiveText() {
        findViewById<TextView>(R.id.text_is_interactive).text =
            powerManager.isInteractive.toString()
    }

    private fun activateWakeLock(
        levelAndFlags: Int,
        repeat: Int = 1,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            powerManager.newWakeLock(
                levelAndFlags,
                "MyApp:MyWakelockTag"
            ).apply {
                withContext(Dispatchers.Main) {
                    setIsEnableAllWakeLocks(false)
                }
                for (i in 0..repeat) {
                    // Activate Wake Lock for 30 seconds.
                    acquire(10 * 60 * 1000L /*10 minutes*/)
                    Thread.sleep(30000)
                    release()

                    if (i != repeat) {
                        // Sleep for 5 seconds
                        Thread.sleep(5000)
                    }
                }
                withContext(Dispatchers.Main) {
                    setIsEnableAllWakeLocks(true)
                }
            }
        }
    }

    private fun setIsEnableAllWakeLocks(newValue: Boolean) {
        findViewById<Button>(R.id.button_wake_lock_full).isEnabled = newValue
        findViewById<Button>(R.id.button_wake_lock_screen_dim).isEnabled = newValue
        findViewById<Button>(R.id.button_wake_lock_screen_bright).isEnabled = newValue
        findViewById<Button>(R.id.button_wake_lock_partial).isEnabled = newValue
        findViewById<Button>(R.id.button_wake_lock_proximity).isEnabled = newValue
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.removeThermalStatusListener(thermalStatusListener)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            unregisterReceiver(deviceIdleModeBroadcastReceiver)
        }
        unregisterReceiver(powerSaveModeBroadcastReceiver)
    }

    private fun getThermalStatusName(status: Int): String {
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> "None"
            PowerManager.THERMAL_STATUS_LIGHT -> "Light"
            PowerManager.THERMAL_STATUS_MODERATE -> "Moderate"
            PowerManager.THERMAL_STATUS_SEVERE -> "Severe"
            PowerManager.THERMAL_STATUS_CRITICAL -> "Critical"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown"
            else -> "Unknown"
        }
    }

    private fun getLocationModeName(locationMode: Int): String {
        return when (locationMode) {
            PowerManager.LOCATION_MODE_NO_CHANGE -> "No Change"
            PowerManager.LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF -> "GPS Disabled When Screen Off"
            PowerManager.LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF -> "All Disabled When Screen Off"
            PowerManager.LOCATION_MODE_FOREGROUND_ONLY -> "Foreground Only"
            PowerManager.LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF -> "Throttle Request When Screen Off"
            else -> "Unknown"
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}