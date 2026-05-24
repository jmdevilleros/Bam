// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import cards.*

// ----------------------------------------------------------------------------------------
class BamRules(private val game: BamGame) {

    // ------------------------------------------------------------------------------------
    private val bamCandidateCards
        get() = game.players
            .flatMap { player -> player.zone.piles.mapNotNull { it.next() } }

    val shouldMoveOrBam: List<CardStack>
        get() = (game.currentPlayer.zone.mainStacks + game.allRows)
            .filter { stack -> stack.top() in bamCandidateCards }

    // ------------------------------------------------------------------------------------
    fun isBam(action: BamAction) : Boolean {
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

    // ------------------------------------------------------------------------------------
    fun verifySource(player: BamPlayer, source: CardStack) = when {
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

        else ->
            ResultOK
    }

    // ------------------------------------------------------------------------------------
    private fun verifyTarget(player: BamPlayer, target: CardStack, source: CardStack) = with(game) {
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

            target is BamPile && target.isEmpty() && source.top()?.rank != Card.Rank.ACE ->
                Result(OK = false, detail = "No es un AS")

            target is BamPile && target.isNotEmpty() && source.top() != target.next() ->
                Result(OK = false, detail = "Fuera de secuencia")

            target in player.opponentTargets && target.isEmpty() ->
                Result(OK = false, detail = "Zona vacía")

            target in player.opponentTargets && target.isNotEmpty()
                    && source.top()!!.suit != target.top()!!.suit ->
                Result(OK = false, detail = "Diferente palo")

            target in player.opponentTargets && target.isNotEmpty()
                    && target.top()!!.rank !in source.top()!!.rank.adjacentsWithLoop() ->
                Result(OK = false, detail = "Fuera de secuencia")

            with(player.zone) {target == dump && source != pull } ->
                Result(OK = false, detail = "Descartar sólo desde la baraja")

            target in allRows && target.isNotEmpty()
                    && source.top()!!.color == target.top()!!.color ->
                Result(OK = false, detail = "Mismo color")

            target in allRows && target.isNotEmpty()
                    && source.top()!!.rank != target.top()!!.rank.previousOrNull() ->
                Result(OK = false, detail = "Fuera de secuencia")

            currentTurn <= numRows && target in allRows && target !in validRows ->
                Result(OK = false, detail = "Zona no disponible en este turno")

            else ->
                ResultOK
        }
    }

    // ------------------------------------------------------------------------------------
    // Consider every possible illegal move and reject it. Report back detailed reason.
    // TODO: Replace the Result class with a code number and look up in a table
    fun verifyMove(player: BamPlayer,
                   source: CardStack,
                   target: CardStack): Result {

        val sourceResult: Result = verifySource(player, source)
        if (!sourceResult.OK)
            return sourceResult

        val targetResult: Result = verifyTarget(player, target, source)
        if (!targetResult.OK)
            return targetResult

        // If every rule was checked, consider it a valid move
        return ResultOK
    }

    // ------------------------------------------------------------------------------------
    fun verifyShowTop(target: CardStack) = when {
        target.top()?.faceup == true ->
            Result(false, "Carta ya está boca arriba")
        game.currentPlayer.zone.pull.top()?.faceup == true ->
            Result(false, "Tomando de la baraja")
        target.isEmpty() ->
            Result(false, "Zona vacía")
        else ->
            ResultOK
    }

    // ------------------------------------------------------------------------------------
    fun verifyFlip(player: BamPlayer) = when {
        game.currentPlayer.zone.pull.top()?.faceup == true ->
            Result(false, "Tomando de la baraja")
        player.zone.pull.isNotEmpty() ->
            Result(false,"Baraja no está vacía")
        player.zone.dump.isEmpty() ->
            Result(false, "Descarte vacío")
        else ->
            ResultOK
    }

    // ------------------------------------------------------------------------------------
    fun verifyYield(player: BamPlayer) = when {
        player.zone.pull.top()?.faceup == true ->
            Result(false, "Tomando de la baraja")
        player.zone.pull.isNotEmpty() || player.zone.dump.isNotEmpty() ->
            Result(false, "Aún hay cartas en baraja o en descarte")
        else ->
            ResultOK
    }
}
