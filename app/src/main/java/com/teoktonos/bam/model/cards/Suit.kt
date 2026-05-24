package com.teoktonos.bam.model.cards

enum class Suit {
    SPADES,
    HEARTS,
    DIAMONDS,
    CLUBS;

    val color: Color
        get() = when (this) {
            SPADES, CLUBS -> Color.BLACK
            HEARTS, DIAMONDS -> Color.RED
        }
}
