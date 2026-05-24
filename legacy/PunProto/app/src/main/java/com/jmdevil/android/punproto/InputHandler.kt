// ========================================================================================
@file:Suppress("DEPRECATION")

package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
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
import cards.CardStack

// ----------------------------------------------------------------------------------------
open class InputHandler(private val context: Context,
                        private val game: BamGame,
                        private val stacksToViewsBridge: StacksToViewsBridge)
    : View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    val msgRotation: Float get() = when (game.currentPlayer) {
        game.players[0] -> 0f
        game.players[1] -> 180f
        else -> 0f
    }

    val msgDelay = 2000L

    // ------------------------------------------------------------------------------------
    override fun onClick(view: View?) {

        if (game.winner != null) return

        // Must not be empty
        val target = view as ViewGroup
        val targetStack = stacksToViewsBridge.viewStackMap[target]

        val action = with(game.currentPlayer.zone) {
            when (targetStack) {
                pull -> {
                    if (pull.isEmpty() && dump.isEmpty())
                        // Enable yield if no cards in pull and dump
                        YieldAction(game.currentPlayer)
                    else
                        PullAction(game.currentPlayer)
                }
                hell -> RevealAction(game.currentPlayer)
                dump -> FlipAction(game.currentPlayer)
                else -> NoAction()
            }
        }

        if (action.isIllegalOrPun())
            return

        when (action) {
            is PullAction, is RevealAction ->
                target.topCard()?.flip()
            is FlipAction ->
                stacksToViewsBridge.stackViewMap[game.currentPlayer.zone.pull]
                    .let { target.flipTo(it, stacksToViewsBridge) }
        }
    }

    // ------------------------------------------------------------------------------------
    override fun onLongClick(view: View): Boolean {

        if (game.winner != null) return false

        val container = view as ViewGroup
        val sourceStack = stacksToViewsBridge.viewStackMap[container] ?: return false
        val result = game.rules.verifySource(game.currentPlayer, sourceStack)
        if (!result.OK) {
            FYIMessage(context, result.detail).display(msgDelay, msgRotation)
            return false
        }

        val topGCard = container.children.last() as GraphicCard
        val data = ClipData.newPlainText("", "")
        val shadow = object : View.DragShadowBuilder(topGCard) {
            private val shadowScale = 1.25f
            private val shadowSize = Point(
                (topGCard.width * shadowScale).toInt(),
                (topGCard.height * shadowScale).toInt()
            )
            override fun onProvideShadowMetrics(size: Point?, touch: Point?) {
                size?.set(shadowSize.x, shadowSize.y)
                if (game.currentPlayer == game.players.first())
                    touch?.set(shadowSize.x, shadowSize.y)
                else
                    touch?.set(0, 0)
            }
            override fun onDrawShadow(canvas: Canvas?) {
                canvas?.scale(shadowScale, shadowScale)
                topGCard.draw(canvas)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            container.startDragAndDrop(data, shadow, topGCard, 0)
        else
            @Suppress("DEPRECATION")
            container.startDrag(data, shadow, topGCard, 0)

        return true
    }

    // ------------------------------------------------------------------------------------
    override fun onDrag(view: View, event: DragEvent): Boolean {
        val draggedView = event.localState as GraphicCard
        val source = draggedView.parent as ViewGroup
        val target = view as ViewGroup
        val sourceStack = stacksToViewsBridge.viewStackMap[source]
        val targetStack = stacksToViewsBridge.viewStackMap[target]

        // --------------------------------------------------------------------------------
        fun endDrag() {
            draggedView.visibility = View.VISIBLE
            target.resetBackground(game, stacksToViewsBridge)
        }

        // --------------------------------------------------------------------------------
        fun attemptMove(sourceStack: CardStack, targetStack: CardStack): Boolean {
            if (source == target)
                return false
            val action = MoveAction(game.currentPlayer, sourceStack, targetStack)

            // Pun move must be reversed, so a possible win will be blocked
            if (action.isIllegalOrPun())
                return false
            target.putCard(draggedView, stacksToViewsBridge)
            when (action.target) {
                game.currentPlayer.zone.dump -> // Dumping ends turn
                    endTurn()
                is BamPile -> // Turn top card down when filling pile
                    if (action.target.isFull()) target.topCard()?.flip()
            }
            if (game.winner != null) {
                endDrag()
                congratulateWinner()
            }
            return true
        }

        // --------------------------------------------------------------------------------
        if (game.winner != null)
            return false
        if (sourceStack == null || targetStack == null)
            return false
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED ->
                draggedView.visibility = View.INVISIBLE
            DragEvent.ACTION_DRAG_ENTERED ->
                target.background = context.resources.getDrawable(R.drawable.selectedstackborder)
            DragEvent.ACTION_DRAG_EXITED ->
                target.resetBackground(game, stacksToViewsBridge)
            DragEvent.ACTION_DROP ->
                return attemptMove(sourceStack, targetStack)
            DragEvent.ACTION_DRAG_ENDED ->
                endDrag()
        }
        return true
    }

    // --------------------------------------------------------------------------------
    private fun BamAction.isIllegalOrPun() : Boolean {
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

    // --------------------------------------------------------------------------------
    private fun congratulateWinner() {
        val winAlert = AlertDialog.Builder(context).run {
            setMessage("¡Ganador! ${game.winner?.name}")
            setView(ImageView(context).apply {
                setImageResource(R.drawable.happy_face_win)
                maxWidth = resources.getDimension(R.dimen.cardWidth).toInt()
                maxHeight = resources.getDimension(R.dimen.cardHeight).toInt()
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                rotation = msgRotation
            })
            setNeutralButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
        }
        winAlert.show()
    }

    // --------------------------------------------------------------------------------
    private fun notifyPun() {
        val punCard = game.rules.shouldMoveOrBam.first().top()
        val punAlert = AlertDialog.Builder(context).run {
            setMessage("¡PUN!")
            if (punCard != null)
                setView(GraphicCard(punCard, context))
            setNeutralButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
        }
        DeviceVibrator(context).vibrate(500)
        punAlert.show()
        endTurn()
    }

    // --------------------------------------------------------------------------------
    private fun endTurn() {
        val current = game.currentPlayer.zone
        val currentHell = stacksToViewsBridge.stackViewMap[current.hell]
        val currentPull = stacksToViewsBridge.stackViewMap[current.pull]
        val currentDump = stacksToViewsBridge.stackViewMap[current.dump]

        // Make sure hell top card is revealed
        if (currentHell?.topCard()?.isFaceUp == false) {
            FYIMessage(context, "Revelando infierno").display(msgDelay, msgRotation)
            currentHell.topCard()?.flip()
        }

        // If turn ends with pull revealed, pass card to dump
        val topCard = currentPull?.topCard()
        if (topCard?.isFaceUp == true) {
            FYIMessage(context, "Descartando").display(msgDelay, msgRotation)
            currentDump?.putCard(topCard, stacksToViewsBridge)
        }

        // Unmark player in turn
        //setVisualMark(game.currentPlayer, false)

        FYIMessage(context, "Fin del turno").display(msgDelay / 2, msgRotation)

        // Set next player
        game.currentPlayer = game.playRotation.next()
        if (game.currentPlayer == game.playOrder.first())
            game.currentTurn++

        // Mark new player in turn
        // setVisualMark(game.currentPlayer, true)

        // Mark available rows
        game.validRows.mapNotNull { stacksToViewsBridge.stackViewMap[it] }.forEach {
            it.background = context.resources.getDrawable(R.drawable.normalstackborder)
        }
    }

    // --------------------------------------------------------------------------------
    fun setVisualMark(player: BamPlayer, isActive: Boolean) {
        val visibility = if (isActive) View.VISIBLE else View.INVISIBLE
        stacksToViewsBridge.indicators[player]?.visibility = visibility
        player.zone.mainStacks.forEach {
            stacksToViewsBridge.stackViewMap[it]?.setHighlight(isActive)
        }
    }
}