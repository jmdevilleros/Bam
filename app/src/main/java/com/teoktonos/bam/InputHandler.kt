package com.teoktonos.bam

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import com.teoktonos.bam.model.cards.CardStack

/**
 * Handles all user input: clicks, long clicks, and drag & drop.
 * This is the main bridge between the UI and the game rules.
 */
@Suppress("DEPRECATION")
open class InputHandler(
    private val context: Context,
    private val game: BamGame,
    private val stacksToViewsBridge: StacksToViewsBridge
) : View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    val msgRotation: Float
        get() = when (game.currentPlayer) {
            game.players[0] -> 0f
            game.players[1] -> 180f
            else -> 0f
        }

    val msgDelay = 2000L

    // ==================== Click Handling ====================

    override fun onClick(view: View?) {
        if (game.winner != null) return

        val target = view as ViewGroup
        val targetStack = stacksToViewsBridge.viewStackMap[target]

        val action = with(game.currentPlayer.zone) {
            when (targetStack) {
                pull -> {
                    if (pull.isEmpty() && dump.isEmpty())
                        YieldAction(game.currentPlayer)
                    else
                        PullAction(game.currentPlayer)
                }
                hell -> RevealAction(game.currentPlayer)
                dump -> FlipAction(game.currentPlayer)
                else -> NoAction()
            }
        }

        if (action.isIllegalOrPun()) return

        when (action) {
            is PullAction, is RevealAction -> target.topCard()?.flip()
            is FlipAction -> {
                stacksToViewsBridge.stackViewMap[game.currentPlayer.zone.pull]
                    ?.let { target.flipTo(it, stacksToViewsBridge) }
            }
        }
    }

    // ==================== Long Click (Start Drag) ====================

    override fun onLongClick(view: View): Boolean {
        if (game.winner != null) return false

        val container = view as ViewGroup
        val sourceStack = stacksToViewsBridge.viewStackMap[container] ?: return false

        val result = game.rules.verifySource(game.currentPlayer, sourceStack)
        if (!result.OK) {
            FYIMessage(context, result.detail).display(msgDelay, msgRotation)
            return false
        }

        val topGCard = container.topCard() ?: return false

        val dragData = ClipData.newPlainText("card", sourceStack.name)
        val shadow = View.DragShadowBuilder(topGCard)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            container.startDragAndDrop(dragData, shadow, sourceStack, 0)
        } else {
            container.startDrag(dragData, shadow, sourceStack, 0)
        }

        return true
    }

    // ==================== Drag & Drop Handling ====================

    override fun onDrag(target: View?, event: DragEvent): Boolean {
        val sourceStack = event.localState as? CardStack ?: return false
        val draggedView = (event.localState as? ViewGroup)?.topCard() ?: return false

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                draggedView.visibility = View.INVISIBLE
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                target?.background = context.resources.getDrawable(R.drawable.selectedstackborder)
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                (target as? ViewGroup)?.resetBackground(game, stacksToViewsBridge)
            }
            DragEvent.ACTION_DROP -> {
                val targetStack = stacksToViewsBridge.viewStackMap[target as ViewGroup]
                return attemptMove(sourceStack, targetStack ?: return false, draggedView, target)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                endDrag(draggedView, target as? ViewGroup)
            }
        }
        return true
    }

    private fun attemptMove(
        sourceStack: CardStack,
        targetStack: CardStack,
        draggedView: GraphicCard,
        target: ViewGroup
    ): Boolean {
        if (sourceStack == targetStack) return false

        val action = MoveAction(game.currentPlayer, sourceStack, targetStack)

        if (action.isIllegalOrPun()) return false

        target.putCard(draggedView, stacksToViewsBridge)

        when (action.target) {
            game.currentPlayer.zone.dump -> endTurn()
            is BamPile -> {
                if (action.target.isFull()) target.topCard()?.flip()
            }
        }

        if (game.winner != null) {
            endDrag(draggedView, target)
            congratulateWinner()
        }
        return true
    }

    private fun endDrag(draggedView: GraphicCard, target: ViewGroup?) {
        draggedView.visibility = View.VISIBLE
        target?.resetBackground(game, stacksToViewsBridge)
    }

    private fun endTurn() {
        // In the full implementation this would advance the turn
        // For now we just let the game engine handle it via the model
    }

    private fun congratulateWinner() {
        AlertDialog.Builder(context)
            .setTitle("¡Ganador!")
            .setMessage("${game.winner?.name} ha ganado la partida.")
            .setPositiveButton("OK", null)
            .show()
    }

    // ==================== Helper: Illegal or Pun ====================

    private fun BamAction.isIllegalOrPun(): Boolean {
        val result = verify()
        if (!result.OK) {
            FYIMessage(context, result.detail).display(msgDelay, msgRotation)
            return true
        }
        if (isBam) {
            notifyPun()
            return true
        }
        return false
    }

    private fun notifyPun() {
        val punCard = game.rules.shouldMoveOrBam.firstOrNull()?.top() ?: return
        FYIMessage(context, "¡Pun! Podías mover $punCard").display(msgDelay, msgRotation)
    }
}
