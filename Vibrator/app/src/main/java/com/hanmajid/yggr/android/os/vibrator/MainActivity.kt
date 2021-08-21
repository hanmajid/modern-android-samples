package com.hanmajid.yggr.android.os.vibrator

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val vibrator: Vibrator by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text_has_vibrator).text = vibrator.hasVibrator().toString()
        findViewById<TextView>(R.id.text_has_amplitude_control).text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.hasAmplitudeControl().toString()
            } else {
                "Min SDK Version: 26"
            }

        // A. Constant Vibration
        findViewById<Button>(R.id.button_vibrate_constant).setOnClickListener {
            vibrator.cancel()
            vibrator.vibrate(2000L)
        }
        findViewById<Button>(R.id.button_vibrate_constant_audio_attribute).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vibrator.cancel()
                vibrator.vibrate(
                    2000L,
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build(),
                )
            } else {
                makeToast("Min SDK Version: 21")
            }
        }

        // B. Pattern Vibration
        val pattern = longArrayOf(0, 100, 1000, 300, 200, 100, 500, 200, 100)
        findViewById<Button>(R.id.button_vibrate_pattern).setOnClickListener {
            vibrator.cancel()
            vibrator.vibrate(pattern, -1)
        }
        findViewById<Button>(R.id.button_vibrate_pattern_repeat).setOnClickListener {
            vibrator.cancel()
            vibrator.vibrate(pattern, 0)
        }
        findViewById<Button>(R.id.button_vibrate_pattern_audio_attribute).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                vibrator.cancel()
                vibrator.vibrate(
                    pattern,
                    -1,
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
            } else {
                makeToast("Min SDK Version: 21")
            }
        }

        // C. Effect Vibration
        // C.1. One shot Vibration
        findViewById<Button>(R.id.button_vibrate_effect_one_shot).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(
                    VibrationEffect.createOneShot(2000L, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                makeToast("Min SDK Version: 26")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_one_shot_min_amplitude).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(
                    VibrationEffect.createOneShot(2000L, 1)
                )
            } else {
                makeToast("Min SDK Version: 26")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_one_shot_mid_amplitude).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(
                    VibrationEffect.createOneShot(2000L, 127)
                )
            } else {
                makeToast("Min SDK Version: 26")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_one_shot_max_amplitude).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(
                    VibrationEffect.createOneShot(2000L, 255)
                )
            } else {
                makeToast("Min SDK Version: 26")
            }
        }

        // C.2. Predefined Vibration
        findViewById<Button>(R.id.button_vibrate_effect_predefined_tick).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isEffectSupported(VibrationEffect.EFFECT_TICK)) {
                    vibrator.cancel()
                    vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    )
                } else {
                    makeToast("EFFECT_TICK is not supported")
                }
            } else {
                makeToast("Min SDK Version: 29")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_predefined_click).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isEffectSupported(VibrationEffect.EFFECT_CLICK)) {
                    vibrator.cancel()
                    vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    )
                } else {
                    makeToast("EFFECT_CLICK is not supported")
                }
            } else {
                makeToast("Min SDK Version: 29")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_predefined_double_click).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isEffectSupported(VibrationEffect.EFFECT_DOUBLE_CLICK)) {
                    vibrator.cancel()
                    vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    )
                } else {
                    makeToast("EFFECT_DOUBLE_CLICK is not supported")
                }
            } else {
                makeToast("Min SDK Version: 29")
            }
        }
        findViewById<Button>(R.id.button_vibrate_effect_predefined_heavy_click).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isEffectSupported(VibrationEffect.EFFECT_HEAVY_CLICK)) {
                    vibrator.cancel()
                    vibrator.vibrate(
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    )
                } else {
                    makeToast("EFFECT_HEAVY_CLICK is not supported")
                }
            } else {
                makeToast("Min SDK Version: 29")
            }
        }

        // C.3. Waveform Vibration
        findViewById<Button>(R.id.button_vibrate_effect_waveform).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                makeToast("Min SDK Version: 26")
            }
        }
        val amplitudes = intArrayOf(0, 255, 0, 127, 0, 100, 0, 255, 0)
        findViewById<Button>(R.id.button_vibrate_effect_waveform_amplitudes).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.cancel()
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            } else {
                makeToast("Min SDK Version: 26")
            }
        }

        // C.4. Composition Vibration
        findViewById<Button>(R.id.button_vibrate_effect_composition).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                vibrator.cancel()
                val composition = VibrationEffect.startComposition()
                if (isPrimitiveSupported(VibrationEffect.Composition.PRIMITIVE_TICK)) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK)
                } else {
                    makeToast("PRIMITIVE_TICK is not supported")
                }
                if (isPrimitiveSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                } else {
                    makeToast("PRIMITIVE_CLICK is not supported")
                }
                if (isPrimitiveSupported(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL)) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL)
                } else {
                    makeToast("PRIMITIVE_QUICK_FALL is not supported")
                }
                if (isPrimitiveSupported(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_RISE)
                } else {
                    makeToast("PRIMITIVE_QUICK_RISE is not supported")
                }
                if (isPrimitiveSupported(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)) {
                    composition.addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                } else {
                    makeToast("PRIMITIVE_SLOW_RISE is not supported")
                }
                try {
                    vibrator.vibrate(composition.compose())
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message, e)
                    makeToast(e.message ?: "Error using composition vibration")
                }
            } else {
                makeToast("Min SDK Version: 30")
            }
        }
    }

    private fun isEffectSupported(effectId: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.areAllEffectsSupported(effectId) == Vibrator.VIBRATION_EFFECT_SUPPORT_YES
        } else {
            // Assume the effect is supported
            true
        }
    }

    private fun isPrimitiveSupported(primitiveId: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator.areAllPrimitivesSupported(primitiveId)
        } else {
            // Assume the primitive is supported
            true
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}