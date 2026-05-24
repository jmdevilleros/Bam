package com.teoktonos.bam

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.teoktonos.bam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var game: BamGame
    private lateinit var stacksToViewsBridge: StacksToViewsBridge
    private lateinit var inputHandler: InputHandler

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Create game via ViewModel
        val viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        game = viewModel.game

        initializeUI()
    }

    private fun initializeUI() {
        stacksToViewsBridge = StacksToViewsBridge(game, this)
        inputHandler = InputHandler(this, game, stacksToViewsBridge)

        // Wire every stack in the game to its corresponding View
        stacksToViewsBridge.stackViewMap.forEach { (stack, view) ->

            // Add all cards visually to the view
            view.addStack(stack, stacksToViewsBridge)

            // Set correct background for rows that are not yet available
            if (stack in game.allRows && stack !in game.validRows) {
                view.setBackgroundResource(R.drawable.disabledstackborder)
            } else {
                view.setBackgroundResource(R.drawable.normalstackborder)
            }

            // Everyone listens to drag events
            view.setOnDragListener(inputHandler)

            // Main stacks (pull, hell, dump, rows, piles) get click/long-click listeners
            if (stack in game.players.flatMap { it.zone.mainStacks } ||
                stack in game.allRows ||
                stack in game.allPiles
            ) {
                view.setOnClickListener(inputHandler)
            }

            // Piles and some stacks don't allow long-click to start drag
            if (stack !is BamPile) {
                view.setOnLongClickListener(inputHandler)
            }
        }

        // Set player names on the indicator labels
        stacksToViewsBridge.indicators.forEach { (player, textView) ->
            textView.text = " ${player.name} "
        }

        // Periodic highlight for current player
        val playerHighlightRunnable = object : Runnable {
            override fun run() {
                game.players.forEach { player ->
                    val isCurrent = player == game.currentPlayer

                    stacksToViewsBridge.indicators[player]?.visibility =
                        if (isCurrent) View.VISIBLE else View.INVISIBLE

                    player.zone.mainStacks.forEach { stack ->
                        stacksToViewsBridge.stackViewMap[stack]?.setHighlight(isCurrent)
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(playerHighlightRunnable)

        // Welcome message
        FYIMessage(this, "Inicia: ${game.currentPlayer.name}").display(
            inputHandler.msgDelay,
            inputHandler.msgRotation
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
