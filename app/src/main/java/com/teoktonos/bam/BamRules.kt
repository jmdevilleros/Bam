package com.teoktonos.bam

import com.teoktonos.bam.model.cards.Card
import com.teoktonos.bam.model.cards.CardStack
import com.teoktonos.bam.model.cards.Rank

/**
 * Contains all the validation rules for the game.
 * This is the heart of the "Bam" / "Pun" / "Tunj" penalty mechanic.
 */
class BamRules(private val game: BamGame) {

    // Cards that are currently candidates to be moved to a BamPile
    private val bamCandidateCards: List<Card>
        get() = game.players
            .flatMap { player -> player.zone.piles.mapNotNull { it.next() } }

    /**
     * Returns the stacks that currently have a card that can go to a foundation.
     * If this list is not empty, most moves become "Bam" (illegal) unless they resolve it.
     */
    val shouldMoveOrBam: List<CardStack>
        get() = (game.currentPlayer.zone.mainStacks + game.allRows)
            .filter { stack -> stack.top() in bamCandidateCards }

    /**
     * Determines whether performing this action during the current state would be a "Bam".
     */
    fun isBam(action: BamAction): Boolean {
        val bamDanger = shouldMoveOrBam.isNotEmpty()
        val safeTarget = action is MoveAction &&
                (action.target is BamPile || action.target == action.player.zone.dump)
        val safeSource = action is MoveAction && action.source in shouldMoveOrBam

        return bamDanger && (
                (action is MoveAction && (!safeTarget || !safeSource)) ||
                        (action is PullAction) ||
                        (action is RevealAction) ||
                        (action is FlipAction)
                )
    }

    // ==================== Source Validation ====================

    fun verifySource(player: BamPlayer, source: CardStack): Result = when {
        source in player.opponentMainStacks ->
            Result(OK = false, detail = "Zona de otro jugador")

        source != player.zone.pull && player.zone.pull.top()?.faceup == true ->
            Result(OK = false, detail = "Tomando de la baraja")

        source is BamPile ->
            Result(OK = false, detail = "No se puede tomar de las pilas")

        source.isEmpty() ->
            Result(OK = false, detail = "Zona vacía")

        source.top()?.faceup == false ->
            Result(OK = false, detail = "Carta boca abajo")

        else -> ResultOK
    }

    // ==================== Target Validation ====================

    private fun verifyTarget(player: BamPlayer, target: CardStack, source: CardStack): Result = with(game) {
        when {
            target == source ->
                Result(OK = false, detail = "Destino igual a origen")

            target in players.map { it.zone.pull } ->
                Result(OK = false, detail = "No puede poner sobre zona de baraja")

            target == player.zone.hell ->
                Result(OK = false, detail = "No puede llevar al propio infierno")

            target is BamPile && target.isFull() ->
                Result(OK = false, detail = "Pila llena")

            target.top()?.faceup == false ->
                Result(OK = false, detail = "Carta boca abajo")

            target is BamPile && target.suit != source.top()!!.suit ->
                Result(OK = false, detail = "Diferente palo")

            target is BamPile && target.isEmpty() && source.top()?.rank != Rank.ACE ->
                Result(OK = false, detail = "No es un AS")

            target is BamPile && target.isNotEmpty() && source.top() != target.next() ->
                Result(OK = false, detail = "Fuera de secuencia")

            target in player.opponentTargets && target.isEmpty() ->
                Result(OK = false, detail = "Zona vacía")

            target in player.opponentTargets && target.isNotEmpty() &&
                    source.top()!!.suit != target.top()!!.suit ->
                Result(OK = false, detail = "Diferente palo")

            target in player.opponentTargets && target.isNotEmpty() &&
                    !source.top()!!.rank.adjacentsWithLoop().contains(target.top()!!.rank) ->
                Result(OK = false, detail = "Fuera de secuencia")

            with(player.zone) { target == dump && source != pull } ->
                Result(OK = false, detail = "Descartar sólo desde la baraja")

            target in allRows && target.isNotEmpty() &&
                    source.top()!!.color == target.top()!!.color ->
                Result(OK = false, detail = "Mismo color")

            target in allRows && target.isNotEmpty() &&
                    source.top()!!.rank != target.top()!!.rank.previousOrNull() ->
                Result(OK = false, detail = "Fuera de secuencia")

            currentTurn <= numRows && target in allRows && target !in validRows ->
                Result(OK = false, detail = "Zona no disponible en este turno")

            else -> ResultOK
        }
    }

    fun verifyMove(player: BamPlayer, source: CardStack, target: CardStack): Result {
        val sourceResult = verifySource(player, source)
        if (!sourceResult.OK) return sourceResult

        val targetResult = verifyTarget(player, target, source)
        if (!targetResult.OK) return targetResult

        return ResultOK
    }

    // ==================== Other Action Validations ====================

    fun verifyShowTop(target: CardStack): Result = when {
        target.top()?.faceup == true ->
            Result(OK = false, detail = "Carta ya está boca arriba")
        game.currentPlayer.zone.pull.top()?.faceup == true ->
            Result(OK = false, detail = "Tomando de la baraja")
        target.isEmpty() ->
            Result(OK = false, detail = "Zona vacía")
        else -> ResultOK
    }

    fun verifyFlip(player: BamPlayer): Result = when {
        game.currentPlayer.zone.pull.top()?.faceup == true ->
            Result(OK = false, detail = "Tomando de la baraja")
        player.zone.pull.isNotEmpty() ->
            Result(OK = false, detail = "Baraja no está vacía")
        player.zone.dump.isEmpty() ->
            Result(OK = false, detail = "Descarte vacío")
        else -> ResultOK
    }

    fun verifyYield(player: BamPlayer): Result = when {
        player.zone.pull.top()?.faceup == true ->
            Result(OK = false, detail = "Tomando de la baraja")
        player.zone.pull.isNotEmpty() || player.zone.dump.isNotEmpty() ->
            Result(OK = false, detail = "Aún hay cartas en baraja o en descarte")
        else -> ResultOK
    }
}
