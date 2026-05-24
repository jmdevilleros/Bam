// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

// ----------------------------------------------------------------------------------------
class DeviceVibrator(context: Context) {
    private val vibrator: Vibrator by lazy {
        context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    }

    // --------------------------------------------------------------------------------
    fun vibrate(ms: Long) {
        with(vibrator) {
            if (hasVibrator())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
                else
                    @Suppress("DEPRECATION")
                    vibrate(ms) // Vibrate method for below API Level 26
        }
    }
}