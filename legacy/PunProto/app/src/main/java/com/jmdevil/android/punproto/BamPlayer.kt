// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
abstract class BamPlayer(val game: BamGame, val name: String) {

    val zone = BamZone(game.hellSize, game.numRows)
    val remainingCards get() = zone.mainStacks.flatten().size
    val opponentTargets get() = game.players
        .filter { it != this }
        .flatMap { listOf(it.zone.dump, it.zone.hell) }
    val opponentMainStacks get() = game.players
        .filter { it != this }
        .flatMap { it.zone.mainStacks }

    // ------------------------------------------------------------------------------------
    abstract fun selectAction(): BamAction
}
