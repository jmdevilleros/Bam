package com.teoktonos.bam

/**
 * Human player.
 * In the Android version, actual input is handled via InputHandler + touch events.
 * This class is mostly a stub (as it was in the original PunProto).
 */
class HumanPlayer(game: BamGame, name: String) : BamPlayer(game, name) {

    override fun selectAction(): BamAction {
        // In the Android port, the real actions come from
        // InputHandler (clicks, long clicks, drag & drop).
        // The console menu code from the original was commented out.
        return NoAction()
    }
}
