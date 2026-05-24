package com.teoktonos.bam

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast

/**
 * Shows temporary messages to the player (used for rule violations and "Pun" warnings).
 */
class FYIMessage(private val context: Context, private val message: String) {

    fun display(durationMillis: Long, rotation: Float = 0f) {
        // For nivel mínimo we use simple Toast.
        // A more polished version would use a custom rotated TextView.
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
