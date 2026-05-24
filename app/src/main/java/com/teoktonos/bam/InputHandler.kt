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

        if (action.isIllegalOrBam()) return

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

    // Used to track the visual card across the entire drag session (STARTED → DROP → ENDED),
    // because after a successful drop the card no longer lives in its original sourceView.
    private var currentDraggedCard: GraphicCard? = null

    override fun onDrag(target: View?, event: DragEvent): Boolean {
        if (game.winner != null) return false

        val sourceStack = event.localState as? CardStack ?: return false
        val sourceView = stacksToViewsBridge.stackViewMap[sourceStack] ?: return false

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                currentDraggedCard = sourceView.topCard()
                currentDraggedCard?.visibility = View.INVISIBLE
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                (target as? ViewGroup)?.background = context.resources.getDrawable(R.drawable.selectedstackborder)
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                (target as? ViewGroup)?.resetBackground(game, stacksToViewsBridge)
            }
            DragEvent.ACTION_DROP -> {
                val targetView = target as? ViewGroup ?: return false
                val targetStack = stacksToViewsBridge.viewStackMap[targetView] ?: return false
                val draggedView = currentDraggedCard ?: sourceView.topCard() ?: return false
                return attemptMove(sourceStack, targetStack, draggedView, targetView)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                // Restore visibility of the card we were dragging (it may now be in a different parent)
                currentDraggedCard?.visibility = View.VISIBLE
                currentDraggedCard = null

                (target as? ViewGroup)?.resetBackground(game, stacksToViewsBridge)
                sourceView.resetBackground(game, stacksToViewsBridge)
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

        if (action.isIllegalOrBam()) return false

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
        // Normal end of turn (after dumping to own dump): do cleanup then switch
        val current = game.currentPlayer.zone
        val currentHellView = stacksToViewsBridge.stackViewMap[current.hell]
        val currentPullView = stacksToViewsBridge.stackViewMap[current.pull]
        val currentDumpView = stacksToViewsBridge.stackViewMap[current.dump]

        // Reveal hell top if needed
        val hellTop = currentHellView?.topCard()
        if (hellTop?.isFaceUp == false) {
            FYIMessage(context, "Revelando infierno").display(msgDelay, msgRotation)
            hellTop.flip()
        }

        // Move revealed pull card to dump (classic Russian Bank behavior)
        val pullTop = currentPullView?.topCard()
        if (pullTop?.isFaceUp == true && currentDumpView != null) {
            FYIMessage(context, "Descartando").display(msgDelay, msgRotation)
            currentDumpView.putCard(pullTop, stacksToViewsBridge)
        }

        switchToNextPlayer()
    }

    /**
     * Immediately ends the current player's turn and gives it to the opponent.
     * Used both for normal dump and for BAM! penalty.
     */
    private fun switchToNextPlayer() {
        FYIMessage(context, "Fin del turno").display(msgDelay / 2, msgRotation)

        game.currentPlayer = game.playRotation.next()
        if (game.currentPlayer == game.playOrder.first()) {
            game.currentTurn++
        }

        // Refresh row availability visuals for the new player
        game.validRows.forEach { row ->
            stacksToViewsBridge.stackViewMap[row]?.resetBackground(game, stacksToViewsBridge)
        }
    }

    private fun congratulateWinner() {
        AlertDialog.Builder(context)
            .setTitle("¡Ganador!")
            .setMessage("${game.winner?.name} ha ganado la partida.")
            .setPositiveButton("OK", null)
            .show()
    }

    // ==================== Helper: Illegal or Bam ====================

    private fun BamAction.isIllegalOrBam(): Boolean {
        val result = verify()
        if (!result.OK) {
            FYIMessage(context, result.detail).display(msgDelay, msgRotation)
            return true
        }
        if (isBam) {
            notifyBam()
            return true
        }
        return false
    }

    private fun notifyBam() {
        val bamCard = game.rules.shouldMoveOrBam.firstOrNull()?.top() ?: return
        FYIMessage(context, "¡Bam! Podías mover $bamCard").display(msgDelay, msgRotation)

        // BAM! penalty: immediately pass the turn to the other player
        DeviceVibrator(context).vibrate(400)
        switchToNextPlayer()
    }
}
