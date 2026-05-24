// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProviders

// ----------------------------------------------------------------------------------------
class MainActivity : AppCompatActivity() {

    // ------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val model = ViewModelProviders.of(this)[GameViewModel::class.java]
        initializeUI(model.game)
    } // onCreate

    // ------------------------------------------------------------------------------------
    private fun initializeUI(game: BamGame) {
        val stacksToViewsBridge = StacksToViewsBridge(game, this)
        val inputHandler = InputHandler(this, game, stacksToViewsBridge)

        stacksToViewsBridge.stackViewMap.forEach { (stack, view) ->
            view.addStack(stack, stacksToViewsBridge)

            // Enable row if active
            if (stack in game.validRows)
                view.background = resources.getDrawable(R.drawable.normalstackborder)

            // Everyone listens to drag events
            view.setOnDragListener(inputHandler)

            // Only main stacks listen to click events
            // dump for flip, pull and hell for top card reveal
            if (stack in game.players.map { it.zone.mainStacks }.flatten())
                view.setOnClickListener(inputHandler)

            // Piles are not sources, so they don't listen to long clicks
            if (stack !is BamPile) {
                view.setOnLongClickListener(inputHandler)
            }
        }

        // Setup player names
        stacksToViewsBridge.indicators.forEach { (player, view) ->
            view.text = player.name
        }

        // Mark and signal initial player
        val handler = Handler()
        val runMarker = object: Runnable {
            override fun run() {
                game.players.forEach { player ->
                    if (player == game.currentPlayer) {
                        stacksToViewsBridge.indicators[player]?.visibility =  View.VISIBLE
                        player.zone.mainStacks.forEach {
                            stacksToViewsBridge.stackViewMap[it]?.setHighlight(true)
                        }
                    }
                    else {
                        stacksToViewsBridge.indicators[player]?.visibility =  View.INVISIBLE
                        player.zone.mainStacks.forEach {
                            stacksToViewsBridge.stackViewMap[it]?.setHighlight(false)
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runMarker)

        // inputHandler.setVisualMark(game.currentPlayer, true)
        FYIMessage(this, "Inicia: ${game.currentPlayer.name}").run {
            display(inputHandler.msgDelay, inputHandler.msgRotation)
        }
    }
}