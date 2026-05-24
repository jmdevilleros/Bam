// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------

// ----------------------------------------------------------------------------------------
class HumanPlayer(game: BamGame, name: String) : BamPlayer(game, name) {

    // ------------------------------------------------------------------------------------
    override fun selectAction(): BamAction {
        var action: BamAction = NoAction()

/*
        val actionMenu = menu(title = null) {
            exitOnSelect = true
            prompt = "Action? "
            menuItem {
                text = "Move"
                item = {
                    action = MoveAction(this@HumanPlayer,
                        selectStack("Source"),
                        selectStack("Target", false))
                }
            }
            menuItem {
                text = "Pull"
                item = { action = PullAction(this@HumanPlayer) }
            }
            menuItem {
                text = "Reveal"
                item = { action = RevealAction(this@HumanPlayer) }
            }
            menuItem {
                text = "Dump"
                item = { action = DumpAction(this@HumanPlayer) }
            }
            menuItem {
                text = "Yield"
                item = { action = YieldAction(this@HumanPlayer) }
            }
            menuItem {
                text = "Flip"
                item = { action = FlipAction(this@HumanPlayer) }
            }
            menuItem {
                text = "Quit game"
                item = { action = QuitAction(this@HumanPlayer) }
            }
        }

        actionMenu.execute(columns = actionMenu.numOptions)
*/
        return action
    }

    // ------------------------------------------------------------------------------------
/*
    private fun selectStack(description: String = "", showItems: Boolean = true): CardStack {
        var selectedStack = CardStack()
        val stacksMenu = menu(null) {
            exitOnSelect = true
            prompt = "$description? "
            game.allStacks.forEach {
                menuItem {
                    text = it.name
                    item = { selectedStack = it }
                }
            }
        }

        stacksMenu.execute(game.allStacks.size / game.numPlayers, showItems)
        return selectedStack
    }
*/

/*
    // ------------------------------------------------------------------------------------
    fun old_selectAction(): Action {
        val prompt = "$name action? (pull/move/flip/reveal/yield/dump/undo/quit) -> "
        return when (game.table.input(prompt, false).toLowerCase()) {
            "p", "pull" ->
                PullAction(this)
            "m", "move" ->
                MoveAction(this, old_selectStack("Source? "), old_selectStack("Target? "))
            "f", "flip" ->
                FlipAction(this)
            "r", "reveal" ->
                RevealAction(this)
            "y", "yield" ->
                YieldAction(this)
            "d", "dump" ->
                DumpAction(this)
            "u", "undo" ->
                UndoAction()
            "q", "quit" ->
                QuitAction(this)
            else ->
                NoAction()
        }
    }

    // ------------------------------------------------------------------------------------
    private fun old_selectStack(prompt: String) : CardStack {
        var location: CardStack? = null
        while (location == null) {
            val input = game.table.input(prompt, false).toLowerCase()
            location = game.allStacks.firstOrNull { it.name == input }
        }
        return location
    }
*/
}
