// ========================================================================================
package com.jmdevil.android.punproto

// ----------------------------------------------------------------------------------------
import cards.*

// ------------------------------------------------------------------------------------
abstract class BamAction {
    var result: Result? = null // null means it has not been executed yet
    open val isBam: Boolean get() = false
    open fun execute() { result = verify() }
    open fun verify(): Result { return ResultOK }
}

// ------------------------------------------------------------------------------------
open class MoveAction(val player: BamPlayer,
                      val source: CardStack,
                      val target: CardStack) : BamAction() {
    val card: Card? = source.top()
    override val isBam: Boolean get() = player.game.rules.isBam(this)
    override fun execute() { result = verify().also { if (it.OK) source.moveTo(target) } }
    override fun verify() = player.game.rules.verifyMove(player, source, target)
    override fun toString(): String {
        return "${player.name}: MOVE $card FROM ${source.name} TO ${target.name} ($result}"
    }
}

// ------------------------------------------------------------------------------------
class DumpAction(player: BamPlayer)
    : MoveAction(player, player.zone.pull, player.zone.dump)

// ------------------------------------------------------------------------------------
open class ShowTopAction(val player: BamPlayer, val target: CardStack) : BamAction() {
    override val isBam: Boolean get() = player.game.rules.isBam(this)
    override fun execute() { result = verify().also { if (it.OK) target.showTop() } }
    override fun verify() = player.game.rules.verifyShowTop(target)
}

// ------------------------------------------------------------------------------------
class PullAction(player: BamPlayer) : ShowTopAction(player, player.zone.pull) {
    override fun toString() = "${player.name}: PULL CARD"
}

// ------------------------------------------------------------------------------------
class RevealAction(player: BamPlayer) : ShowTopAction(player, player.zone.hell) {
    override fun toString() = "${player.name}: REVEAL HELL"
}

// ------------------------------------------------------------------------------------
class YieldAction(val player: BamPlayer) : BamAction() {
    override fun execute() { result = verify() }
    override fun verify() = player.game.rules.verifyYield(player)
    override fun toString() = "${player.name}: YIELD"
}

// ------------------------------------------------------------------------------------
class FlipAction(val player: BamPlayer) : BamAction() {
    override val isBam: Boolean get() = player.game.rules.isBam(this)
    override fun execute() = with(player.zone) {
        result = verify()
        if (result?.OK == true) {
            pull.takeFrom(dump, dump.size)
            pull.flip(reverse = false)
        }
    }
    override fun verify() = player.game.rules.verifyFlip(player)
    override fun toString() = "${player.name}: FLIP"
}

// ------------------------------------------------------------------------------------
class QuitAction(val player: BamPlayer) : BamAction() {
    override fun toString() = "${player.name}: QUIT"
}

// ------------------------------------------------------------------------------------
class BamHappenAction(val attempted: BamAction): BamAction() {
    override fun execute() { result = verify() }
    override fun toString() = "BAM! attempting $attempted"
}

// ------------------------------------------------------------------------------------
class NoAction : BamAction()
