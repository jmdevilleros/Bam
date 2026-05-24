package com.teoktonos.bam

import com.teoktonos.bam.model.cards.Card
import com.teoktonos.bam.model.cards.CardStack
import com.teoktonos.bam.model.cards.Rank
import com.teoktonos.bam.model.cards.Suit

/**
 * A foundation pile (one per suit).
 * Can only be built in ascending order of the same suit, starting with ACE.
 */
class BamPile(name: String, val suit: Suit) : CardStack(name) {

    fun next(): Card? = when {
        isFull()  -> null
        isEmpty() -> Card(Rank.ACE, suit)
        else      -> Card(top()!!.rank.nextOrNull()!!, suit)
    }

    fun isFull(): Boolean = top()?.rank == Rank.KING
}
