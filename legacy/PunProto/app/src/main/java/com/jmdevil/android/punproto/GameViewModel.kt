// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import androidx.lifecycle.ViewModel

// ----------------------------------------------------------------------------------------
class GameViewModel : ViewModel() {
    val game = BamGame(numHumans = 2, numBots = 0)
    init {
        game.currentTurn = 1
    }
}