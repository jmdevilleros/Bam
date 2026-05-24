// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import android.content.Context
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Toast

// ----------------------------------------------------------------------------------------
class FYIMessage(private val context: Context,
                 private val message: String) {

    private val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)

    // ----------------------------------------------------------------------------------------
    fun display(durationMillis: Long = 500L, rotation: Float = 0f) {
        val timer = object : CountDownTimer(durationMillis, 100) {
            override fun onTick(millisUntilFinished: Long) { toast.show() }
            override fun onFinish() { toast.cancel() }
        }
        with(toast) {
            setGravity(Gravity.CENTER, 0, 0)
            view.rotation = rotation
            show()
            //timer.start()
        }
    }
}