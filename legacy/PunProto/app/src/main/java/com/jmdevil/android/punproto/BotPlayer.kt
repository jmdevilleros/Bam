// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import java.lang.Thread.sleep
import cards.*

// ----------------------------------------------------------------------------------------
class BotPlayer(game: BamGame, name: String, private val actionDelayMillis: Long = 500)
    : BamPlayer(game, name) {
    override fun selectAction(): BamAction {
        val moves = getValidMoves()
        val bamMove = moves.firstOrNull { it.second is BamPile }
        val hellMove = moves.firstOrNull { it.first == zone.hell }
        val pullMove = moves.firstOrNull { it.first == zone.pull }
        val dumpMove = moves.firstOrNull { it.first == zone.dump }
        val opponentTarget = moves.firstOrNull { it.second in opponentTargets }
        val fromRowMove = moves.firstOrNull { it.first in game.validRows }

        sleep(actionDelayMillis)
        return when {
            bamMove != null ->
                MoveAction(this, bamMove.first, bamMove.second)
            hellMove != null ->
                MoveAction(this, hellMove.first, hellMove.second)
            zone.hell.top()?.faceup == false ->
                RevealAction(this)
            pullMove != null ->
                MoveAction(this, pullMove.first, pullMove.second)
            dumpMove != null ->
                MoveAction(this, dumpMove.first, dumpMove.second)
            opponentTarget != null ->
                MoveAction(this, opponentTarget.first, opponentTarget.second)
            fromRowMove != null && fromRowMove.first.size == 1 && fromRowMove.second.size > 0 ->
                MoveAction(this, fromRowMove.first, fromRowMove.second)
            zone.pull.isEmpty() && zone.dump.isNotEmpty() ->
                FlipAction(this)
            zone.pull.top()?.faceup == false ->
                PullAction(this)
            zone.pull.top()?.faceup == true ->
                DumpAction(this)
            moves.isEmpty() ->
                YieldAction(this)
            else ->
                YieldAction(this)
        }
    }

    // ----------------------------------------------------------------------------------------
    private fun getValidMoves(): List<Pair<CardStack, CardStack>> {
        val validMoves = mutableListOf<Pair<CardStack, CardStack>>()
        val sources = game.allStacks.asSequence()
            .filterNot { it is BamPile }
            .filterNot { it.isEmpty() }
            .filterNot { it.top()?.faceup == false }
        val targets = game.allStacks.asSequence()
            .filterNot { it is BamPile && it.isFull() }
            .filterNot { it.top()?.faceup == false }
            .filterNot { it in game.players.map { zone.pull } }

        sources.forEach { source ->
            targets.forEach { target ->
                if (game.rules.verifyMove(this, source, target).OK)
                    validMoves.add(source to target)
            }
        }
        return validMoves
    }
}