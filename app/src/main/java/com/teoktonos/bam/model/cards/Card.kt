package com.teoktonos.bam.model.cards

import java.util.*

/**
 * Represents a single playing card.
 * faceup is mutable because the game frequently flips cards in place.
 */
data class Card(
    val rank: Rank,
    val suit: Suit,
    var faceup: Boolean = false
) {
    val color: Color get() = suit.color

    /**
     * Human-readable name used by GraphicCard to resolve drawable resources.
     * Must produce values like:
     *   "Ace of Clubs", "Two of Diamonds", "Jack of Hearts", "King of Spades"
     */
    val name: String
        get() = "${rank.name.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }} of " +
                "${suit.name.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }}"

    /**
     * Flips the card (faceup <-> facedown).
     */
    fun flip() {
        faceup = !faceup
    }

    override fun toString(): String = if (faceup) name else "???"
}
