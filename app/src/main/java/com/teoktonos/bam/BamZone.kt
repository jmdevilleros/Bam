package com.teoktonos.bam

import com.teoktonos.bam.model.cards.CardStack
import com.teoktonos.bam.model.cards.Deck
import com.teoktonos.bam.model.cards.Suit
import java.util.*

/**
 * Represents the complete zone of one player.
 * Contains: pull (draw pile), hell (reserve), dump, 4 rows and 4 BamPiles.
 */
class BamZone(hellSize: Int, numRows: Int) {

    val pull = Deck.createAsStack(name = "pull$zoneNumber", shuffled = true, facedown = true)

    val hell = CardStack(name = "hell$zoneNumber").apply {
        takeFrom(pull, hellSize)
        showTop(true)
    }

    val dump = CardStack(name = "dump$zoneNumber")

    val rows = (1..numRows).map {
        CardStack(name = "r${numRows * (zoneNumber - 1) + it}")
    }

    val piles = Suit.entries.map { suit ->
        BamPile(name = "${suit.name.lowercase(Locale.US)}$zoneNumber", suit = suit)
    }

    val mainStacks: List<CardStack>
        get() = listOf(pull, hell, dump)

    val localStacks: List<CardStack>
        get() = mainStacks + piles + rows

    companion object {
        private var zoneNumber: Int = 1
    }

    init {
        zoneNumber++
    }
}
