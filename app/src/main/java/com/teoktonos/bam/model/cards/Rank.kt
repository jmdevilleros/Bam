package com.teoktonos.bam.model.cards

enum class Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;

    /**
     * Returns the next higher rank, or null if this is KING.
     */
    fun nextOrNull(): Rank? = when (this) {
        KING -> null
        else -> Rank.entries[ordinal + 1]
    }

    /**
     * Returns the previous lower rank, or null if this is ACE.
     */
    fun previousOrNull(): Rank? = when (this) {
        ACE -> null
        else -> Rank.entries[ordinal - 1]
    }

    /**
     * Returns adjacent ranks, allowing wrap-around from KING to ACE.
     * Used when playing on opponent's hell or dump.
     */
    fun adjacentsWithLoop(): List<Rank> = when (this) {
        ACE -> listOf(TWO, KING)
        KING -> listOf(QUEEN, ACE)
        else -> listOf(Rank.entries[ordinal - 1], Rank.entries[ordinal + 1])
    }
}
