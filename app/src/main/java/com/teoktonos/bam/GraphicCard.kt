package com.teoktonos.bam

import android.content.Context
import android.widget.ImageView
import com.teoktonos.bam.model.cards.Card
import java.util.*

/**
 * Visual representation of a Card.
 * This class is tightly coupled to the drawable resources naming convention.
 */
class GraphicCard(private val card: Card, context: Context) : ImageView(context) {

    private val frontResID: Int by lazy {
        card.name.replace(' ', '_').lowercase(Locale.US).findID()
    }

    private val backResID: Int by lazy {
        resources.getString(R.string.CardBackName).findID()
    }

    val isFaceUp: Boolean get() = card.faceup

    init {
        updateImage()
    }

    private fun String.findID(): Int {
        return resources.getIdentifier(this, "drawable", context.packageName)
    }

    private fun updateImage() {
        setImageResource(if (card.faceup) frontResID else backResID)
    }

    fun flip() {
        card.flip()
        updateImage()
    }
}
