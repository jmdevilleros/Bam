// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import android.content.Context
import android.widget.ImageView
import cards.*
import java.util.*

// ----------------------------------------------------------------------------------------
class GraphicCard(private val card: Card, context: Context) : ImageView(context) {
    private val frontResID by lazy {
        card.name.replace(' ', '_').toLowerCase(Locale.US).findID()
    }
    private val backResID by lazy {
        resources.getString(R.string.CardBackName).findID()
    }
    val isFaceUp get() = card.faceup

    // ------------------------------------------------------------------------------------
    init {
        updateImage()
    }

    // ------------------------------------------------------------------------------------
    private fun String.findID(): Int {
        return resources.getIdentifier(this, "drawable", context.packageName)
    }

    // ------------------------------------------------------------------------------------
    private fun updateImage() = setImageResource(if (card.faceup) frontResID else backResID)

    // ------------------------------------------------------------------------------------
    fun flip() {
        card.flip()
        updateImage()
    }
}