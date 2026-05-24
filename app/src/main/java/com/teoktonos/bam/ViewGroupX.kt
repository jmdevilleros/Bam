package com.teoktonos.bam

import android.transition.Explode
import android.transition.TransitionManager
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.children
import com.teoktonos.bam.model.cards.CardStack

/**
 * Extension functions to manage the visual representation of CardStacks.
 * This file was one of the most important glue layers in the original implementation.
 */

// --------------------------------------------------------------------------------
fun ViewGroup.putCard(
    gCard: GraphicCard,
    stacksToViewsBridge: StacksToViewsBridge,
    animate: Boolean = false
) {
    val source = gCard.parent as ViewGroup?

    gCard.layoutParams = prepareMarginParams()

    if (animate) {
        val transition = Explode().apply {
            duration = 500
        }
        TransitionManager.beginDelayedTransition(this, transition)
    }

    // Update UI
    source?.removeView(gCard)
    addView(gCard)

    // Update model
    val sourceStack = stacksToViewsBridge.viewStackMap[source]
    val targetStack = stacksToViewsBridge.viewStackMap[this]
    if (sourceStack != null && targetStack != null) {
        sourceStack.moveTo(targetStack)
    }
}

// --------------------------------------------------------------------------------
fun ViewGroup.addStack(
    stack: CardStack,
    stacksToViewsBridge: StacksToViewsBridge,
    animate: Boolean = false
) {
    stack.forEach { card ->
        putCard(GraphicCard(card, context), stacksToViewsBridge, animate)
    }
}

// --------------------------------------------------------------------------------
fun ViewGroup.flipTo(
    target: ViewGroup?,
    stacksToViewsBridge: StacksToViewsBridge
) {
    children.toList().reversed().forEach { view ->
        val gCard = view as GraphicCard
        gCard.flip()
        target?.putCard(gCard, stacksToViewsBridge, true)
    }
}

// --------------------------------------------------------------------------------
private fun ViewGroup.prepareMarginParams(): ViewGroup.LayoutParams {
    val newMargins = ViewGroup.MarginLayoutParams(
        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
        ViewGroup.MarginLayoutParams.WRAP_CONTENT
    )

    return when (this) {
        is LinearLayout -> {
            newMargins.marginStart = resources.getDimension(
                if (childCount > 0) R.dimen.cardOffsetH else R.dimen.cardOffsetHFirst
            ).toInt()
            LinearLayout.LayoutParams(newMargins)
        }
        is FrameLayout -> {
            newMargins.marginStart = 0
            FrameLayout.LayoutParams(newMargins)
        }
        else -> newMargins
    }
}

// --------------------------------------------------------------------------------
fun ViewGroup.topCard(): GraphicCard? =
    if (childCount < 1) null else children.last() as GraphicCard

// --------------------------------------------------------------------------------
fun ViewGroup.setHighlight(isOn: Boolean) {
    @Suppress("DEPRECATION")
    background = context.resources.getDrawable(
        if (isOn) R.drawable.activeplayerborder
        else R.drawable.normalstackborder
    )
}

// --------------------------------------------------------------------------------
fun ViewGroup.resetBackground(game: BamGame, stacksToViewsBridge: StacksToViewsBridge) {
    val stack = stacksToViewsBridge.viewStackMap[this]
    val resource = when {
        stack in game.currentPlayer.zone.mainStacks -> R.drawable.activeplayerborder
        stack in game.allRows && stack !in game.validRows -> R.drawable.disabledstackborder
        else -> R.drawable.normalstackborder
    }
    @Suppress("DEPRECATION")
    background = context.resources.getDrawable(resource)
}
