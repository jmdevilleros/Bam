// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import cards.*

// ----------------------------------------------------------------------------------------
class BamGame(numHumans: Int = 1,
              numBots: Int = 1,
              delay: Long = 500,
              val hellSize: Int = Deck.create().size / 4,
              val numRows: Int = Card.Suit.values().size,
              val maxTurns: Int = 200) {

    val players: List<BamPlayer>
    val rules = BamRules(this)
    var currentPlayer: BamPlayer
    var currentTurn = 0
    val numPlayers = numHumans + numBots

    val playOrder: Sequence<BamPlayer>
    val playRotation: Iterator<BamPlayer>

    private val actionHistory = mutableListOf<BamAction>()

    private val playerTurnsStarted
        get() = numPlayers * (currentTurn - 1) + playOrder.indexOf(currentPlayer) + 1

    val winner
        get() = players.firstOrNull { it.remainingCards == 0 }

    val history
        get() = actionHistory.map { it }

    val allStacks
        get() = players.flatMap { it.zone.localStacks }

    val allMainStacks
        get() = players.flatMap { it.zone.mainStacks }

    val allRows
        get() = players.flatMap { it.zone.rows }

    val allPiles
        get() = players.flatMap { it.zone.piles }

    val validRows
        // Rows become progressively available after each player's turn
        get() = (0 until numRows).asSequence()
            .flatMap { playOrder.rowsOnIndex(it) }
            .take(playerTurnsStarted)

    // ------------------------------------------------------------------------------------
    init {
        players = List(numHumans) { HumanPlayer(this,"Jugador ${it + 1}") } +
                  List(numBots)   { BotPlayer(this,  "Máquina ${it + 1}",
                      actionDelayMillis = delay) }

        playOrder = players
            .sortedByDescending { it.zone.hell.top()?.rank?.ordinal }
            .asSequence()
        playRotation = playOrder.repeatForever().iterator()
        currentPlayer = playRotation.next()
    }

    // ------------------------------------------------------------------------------------
    fun run() {
        // val playRotation = playOrder.repeatForever().iterator()
        do {
            currentPlayer = playRotation.next()
            if (currentPlayer == playOrder.first())
                currentTurn++
            //table.update()
            do {
                playTurn()
                //table.update()
            } while (isActive())
            RevealAction(currentPlayer).execute()
        } while (isRunning())
    }

    // ------------------------------------------------------------------------------------
    private fun playTurn() {
        var action = currentPlayer.selectAction()
        if (rules.isBam(action))
            action = BamHappenAction(action)
        action.execute()
        actionHistory.add(action) // add action to history even if unsuccessful
        if (action.result?.OK == false || action is BamHappenAction) {
//            table.message(msg = action.result!!.detail,
//                          pause = currentPlayer is HumanPlayer)
        }
    }

    // ------------------------------------------------------------------------------------
    private fun isActive(): Boolean {
        val lastAction = actionHistory.last()
        val lastWasOK   = lastAction.result?.OK == true
        val actionEndsTurn = lastWasOK && when (lastAction) {
            is MoveAction ->
                lastAction.target == lastAction.player.zone.dump
            is BamHappenAction, is YieldAction, is DumpAction ->
                true
            else ->
                false
        }
        return isRunning() && !actionEndsTurn
    }

    // ------------------------------------------------------------------------------------
    private fun isRunning(): Boolean {
        val winner = winner != null
        val quit = actionHistory.last() is QuitAction
        val allYielded = actionHistory
            .takeLast(numPlayers)
            .count { it is YieldAction } == numPlayers

        return !(currentTurn >= maxTurns || allYielded || winner || quit)
    }

    // ------------------------------------------------------------------------------------
    private fun Sequence<BamPlayer>.rowsOnIndex(index: Int) =
        this.map { it.zone.rows[index] }

    // ----------------------------------------------------------------------------
    private fun Sequence<BamPlayer>.repeatForever() =
        generateSequence(this) { it }.flatten()

}
