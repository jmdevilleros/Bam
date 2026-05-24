package com.teoktonos.bam

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.teoktonos.bam.model.cards.CardStack

/**
 * Bridges between the model (CardStacks) and the views in activity_main.xml.
 * This class is responsible for mapping the many IDs in the layout to the game model.
 */
class StacksToViewsBridge(game: BamGame, activity: Activity) {

    val mainFrame: ConstraintLayout = activity.findViewById(R.id.mainFrame)

    val indicators: Map<BamPlayer, TextView> = mapOf(
        game.players[0] to activity.findViewById(R.id.indicador1),
        game.players[1] to activity.findViewById(R.id.indicador2)
    )

    // Player 1 views
    val p1Pull: FrameLayout = activity.findViewById(R.id.p1Pull)
    val p1Hell: FrameLayout = activity.findViewById(R.id.p1Hell)
    val p1Dump: FrameLayout = activity.findViewById(R.id.p1Dump)
    val p1Spades: FrameLayout = activity.findViewById(R.id.p1Spades)
    val p1Hearts: FrameLayout = activity.findViewById(R.id.p1Hearts)
    val p1Diamonds: FrameLayout = activity.findViewById(R.id.p1Diamonds)
    val p1Clubs: FrameLayout = activity.findViewById(R.id.p1Clubs)
    val p1Row1: LinearLayout = activity.findViewById(R.id.p1Row1)
    val p1Row2: LinearLayout = activity.findViewById(R.id.p1Row2)
    val p1Row3: LinearLayout = activity.findViewById(R.id.p1Row3)
    val p1Row4: LinearLayout = activity.findViewById(R.id.p1Row4)

    // Player 2 views
    val p2Pull: FrameLayout = activity.findViewById(R.id.p2Pull)
    val p2Hell: FrameLayout = activity.findViewById(R.id.p2Hell)
    val p2Dump: FrameLayout = activity.findViewById(R.id.p2Dump)
    val p2Spades: FrameLayout = activity.findViewById(R.id.p2Spades)
    val p2Hearts: FrameLayout = activity.findViewById(R.id.p2Hearts)
    val p2Diamonds: FrameLayout = activity.findViewById(R.id.p2Diamonds)
    val p2Clubs: FrameLayout = activity.findViewById(R.id.p2Clubs)
    val p2Row1: LinearLayout = activity.findViewById(R.id.p2Row1)
    val p2Row2: LinearLayout = activity.findViewById(R.id.p2Row2)
    val p2Row3: LinearLayout = activity.findViewById(R.id.p2Row3)
    val p2Row4: LinearLayout = activity.findViewById(R.id.p2Row4)

    // ==================== Mappings ====================

    val stackViewMap: Map<CardStack, ViewGroup> by lazy {
        buildMap {
            // Player 1
            put(game.players[0].zone.pull, p1Pull)
            put(game.players[0].zone.hell, p1Hell)
            put(game.players[0].zone.dump, p1Dump)
            put(game.players[0].zone.piles[0], p1Spades)
            put(game.players[0].zone.piles[1], p1Hearts)
            put(game.players[0].zone.piles[2], p1Diamonds)
            put(game.players[0].zone.piles[3], p1Clubs)
            game.players[0].zone.rows.forEachIndexed { i, row ->
                put(row, listOf(p1Row1, p1Row2, p1Row3, p1Row4)[i])
            }

            // Player 2
            put(game.players[1].zone.pull, p2Pull)
            put(game.players[1].zone.hell, p2Hell)
            put(game.players[1].zone.dump, p2Dump)
            put(game.players[1].zone.piles[0], p2Spades)
            put(game.players[1].zone.piles[1], p2Hearts)
            put(game.players[1].zone.piles[2], p2Diamonds)
            put(game.players[1].zone.piles[3], p2Clubs)
            game.players[1].zone.rows.forEachIndexed { i, row ->
                put(row, listOf(p2Row1, p2Row2, p2Row3, p2Row4)[i])
            }
        }
    }

    val viewStackMap: Map<ViewGroup, CardStack> by lazy {
        stackViewMap.entries.associate { (stack, view) -> view to stack }
    }
}
