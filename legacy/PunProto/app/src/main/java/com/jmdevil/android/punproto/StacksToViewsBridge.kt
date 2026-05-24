// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cards.CardStack

// ----------------------------------------------------------------------------------------
class StacksToViewsBridge(game: BamGame, activity: Activity) {

    val mainFrame: ConstraintLayout = activity.findViewById(R.id.mainFrame)
    val indicators = mapOf<BamPlayer, TextView>(
        game.players[0] to activity.findViewById(R.id.indicador1),
        game.players[1] to activity.findViewById(R.id.indicador2)
    )

    // ------------------------------------------------------------------------------------
    // Player 1 views
    val p1Pull:     FrameLayout  = activity.findViewById(R.id.p1Pull)
    val p1Hell:     FrameLayout  = activity.findViewById(R.id.p1Hell)
    val p1Dump:     FrameLayout  = activity.findViewById(R.id.p1Dump)
    val p1Spades:   FrameLayout  = activity.findViewById(R.id.p1Spades)
    val p1Hearts:   FrameLayout  = activity.findViewById(R.id.p1Hearts)
    val p1Diamonds: FrameLayout  = activity.findViewById(R.id.p1Diamonds)
    val p1Clubs:    FrameLayout  = activity.findViewById(R.id.p1Clubs)
    val p1Row1:     LinearLayout = activity.findViewById(R.id.p1Row1)
    val p1Row2:     LinearLayout = activity.findViewById(R.id.p1Row2)
    val p1Row3:     LinearLayout = activity.findViewById(R.id.p1Row3)
    val p1Row4:     LinearLayout = activity.findViewById(R.id.p1Row4)

    // ------------------------------------------------------------------------------------
    // Player 2 views
    val p2Pull:     FrameLayout  = activity.findViewById(R.id.p2Pull)
    val p2Hell:     FrameLayout  = activity.findViewById(R.id.p2Hell)
    val p2Dump:     FrameLayout  = activity.findViewById(R.id.p2Dump)
    val p2Spades:   FrameLayout  = activity.findViewById(R.id.p2Spades)
    val p2Hearts:   FrameLayout  = activity.findViewById(R.id.p2Hearts)
    val p2Diamonds: FrameLayout  = activity.findViewById(R.id.p2Diamonds)
    val p2Clubs:    FrameLayout  = activity.findViewById(R.id.p2Clubs)
    val p2Row1:     LinearLayout = activity.findViewById(R.id.p2Row1)
    val p2Row2:     LinearLayout = activity.findViewById(R.id.p2Row2)
    val p2Row3:     LinearLayout = activity.findViewById(R.id.p2Row3)
    val p2Row4:     LinearLayout = activity.findViewById(R.id.p2Row4)

    // ------------------------------------------------------------------------------------
    val allViews = listOf(
        p1Pull, p1Hell, p1Dump,
        p1Spades, p1Hearts, p1Diamonds, p1Clubs,
        p1Row1, p1Row2, p1Row3, p1Row4,
        p2Pull, p2Hell, p2Dump,
        p2Spades, p2Hearts, p2Diamonds, p2Clubs,
        p2Row1, p2Row2, p2Row3, p2Row4
    )

    // ------------------------------------------------------------------------------------
    val stackViewMap: Map<CardStack, ViewGroup> by lazy {
        (game.allStacks zip allViews).toMap()
    }

    // ------------------------------------------------------------------------------------
    val viewStackMap: Map<ViewGroup, CardStack> by lazy {
        (allViews zip game.allStacks).toMap()
    }
}