package com.teoktonos.bam

import com.teoktonos.bam.model.cards.Card
import com.teoktonos.bam.model.cards.CardStack

/**
 * The main game engine.
 * Manages players, turn order, history, and the core game loop.
 */
class BamGame(
    numHumans: Int = 1,
    numBots: Int = 1,
    delay: Long = 500,
    val hellSize: Int = 13,                    // 52 / 4
    val numRows: Int = 4,                      // one row per suit
    val maxTurns: Int = 200
) {

    val players: List<BamPlayer>
    val rules = BamRules(this)
    var currentPlayer: BamPlayer
    var currentTurn = 0
    val numPlayers = numHumans + numBots

    val playOrder: Sequence<BamPlayer>
    val playRotation: Iterator<BamPlayer>

    private val actionHistory = mutableListOf<BamAction>()

    private val playerTurnsStarted: Int
        get() = numPlayers * (currentTurn - 1) + playOrder.indexOf(currentPlayer) + 1

    val winner: BamPlayer?
        get() = players.firstOrNull { it.remainingCards == 0 }

    val history: List<BamAction>
        get() = actionHistory.toList()

    val allStacks: List<CardStack>
        get() = players.flatMap { it.zone.localStacks }

    val allMainStacks: List<CardStack>
        get() = players.flatMap { it.zone.mainStacks }

    val allRows: List<CardStack>
        get() = players.flatMap { it.zone.rows }

    val allPiles: List<CardStack>
        get() = players.flatMap { it.zone.piles }

    /**
     * Rows become progressively available after each player's turn.
     */
    val validRows: List<CardStack>
        get() = (0 until numRows).asSequence()
            .flatMap { playOrder.rowsOnIndex(it) }
            .take(playerTurnsStarted)
            .toList()

    // ==================== Initialization ====================

    init {
        players = buildList {
            addAll(List(numHumans) { HumanPlayer(this@BamGame, "Jugador ${it + 1}") })
            addAll(List(numBots) {
                BotPlayer(this@BamGame, "Máquina ${it + 1}", actionDelayMillis = delay)
            })
        }

        playOrder = players
            .sortedByDescending { it.zone.hell.top()?.rank?.ordinal }
            .asSequence()

        playRotation = playOrder.repeatForever().iterator()
        currentPlayer = playRotation.next()
    }

    // ==================== Game Loop ====================

    fun run() {
        do {
            currentPlayer = playRotation.next()
            if (currentPlayer == playOrder.first()) currentTurn++

            do {
                playTurn()
            } while (isActive())

            RevealAction(currentPlayer).execute()

        } while (isRunning())
    }

    private fun playTurn() {
        var action = currentPlayer.selectAction()

        if (rules.isBam(action)) {
            action = BamHappenAction(action)
        }

        action.execute()
        actionHistory.add(action)

        if (action.result?.OK == false || action is BamHappenAction) {
            // In the original console version messages were shown here.
            // In the Android version this is handled by InputHandler / FYIMessage.
        }
    }

    private fun isActive(): Boolean {
        val lastAction = actionHistory.lastOrNull() ?: return true
        val lastWasOK = lastAction.result?.OK == true

        val actionEndsTurn = lastWasOK && when (lastAction) {
            is MoveAction -> lastAction.target == currentPlayer.zone.dump || lastAction.target is BamPile
            is DumpAction -> true
            is YieldAction -> true
            else -> false
        }

        return !actionEndsTurn
    }

    private fun isRunning(): Boolean {
        return winner == null && currentTurn < maxTurns
    }
}

// ==================== Helper Extensions ====================

/** Allows repeating a sequence forever */
fun <T> Sequence<T>.repeatForever(): Sequence<T> = sequence {
    while (true) yieldAll(this@repeatForever)
}

/** Returns the row that becomes available at a given index in the rotation */
fun Sequence<BamPlayer>.rowsOnIndex(index: Int): Sequence<CardStack> =
    map { it.zone.rows.getOrNull(index) }.filterNotNull()
