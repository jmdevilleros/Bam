package com.teoktonos.bam

import androidx.lifecycle.ViewModel

/**
 * Simple ViewModel that holds the game instance.
 * Using 2 human players by default for testing the UI.
 */
class GameViewModel : ViewModel() {

    val game = BamGame(
        numHumans = 2,
        numBots = 0
    )

    init {
        game.currentTurn = 1
    }
}
