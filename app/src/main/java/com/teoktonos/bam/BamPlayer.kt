package com.teoktonos.bam

import com.teoktonos.bam.model.cards.CardStack

/**
 * Abstract player. Each player owns a complete BamZone.
 */
abstract class BamPlayer(val game: BamGame, val name: String) {

    val zone = BamZone(game.hellSize, game.numRows)

    /** Cards still under this player's control (pull + hell + dump). */
    val remainingCards: Int
        get() = zone.mainStacks.sumOf { it.size }

    /** Stacks belonging to opponents that this player can legally play onto. */
    val opponentTargets: List<CardStack>
        get() = game.players
            .filter { it != this }
            .flatMap { listOf(it.zone.dump, it.zone.hell) }

    val opponentMainStacks: List<CardStack>
        get() = game.players
            .filter { it != this }
            .flatMap { it.zone.mainStacks }

    abstract fun selectAction(): BamAction
}
