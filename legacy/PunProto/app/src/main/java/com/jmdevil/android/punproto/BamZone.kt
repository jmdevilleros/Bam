// ========================================================================================
package com.jmdevil.android.punproto

import cards.*
import java.util.*

// ----------------------------------------------------------------------------------------
class BamZone(infernoSize: Int, numRows: Int) {

    val pull = Deck.create(name = "pull$zoneNumber",
        facedown = true, shuffled = true
    )
    val hell = CardStack(name = "hell$zoneNumber").apply {
        takeFrom(pull, infernoSize)
        showTop(true)
    }
    val dump = CardStack(name = "dump$zoneNumber")
    val rows = (1..numRows)
        .map { CardStack(name = "r${numRows * (zoneNumber - 1) + it}") }
    val piles = Card.Suit.values() // Spades, Hearts, Diamonds, Clubs
        .map { BamPile(name = "${it.name.toLowerCase(Locale.US)}$zoneNumber", suit = it) }

    val mainStacks
        get() = listOf(pull, hell, dump)
    val localStacks
        get() = mainStacks + piles + rows

    // ----------------------------------------------------------)--------------------------
    companion object {
        var zoneNumber: Int = 1
    }

    // ------------------------------------------------------------------------------------
    init {
        zoneNumber++
    }
}