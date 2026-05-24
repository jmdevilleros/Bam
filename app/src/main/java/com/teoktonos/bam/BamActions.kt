package com.teoktonos.bam

import com.teoktonos.bam.model.cards.Card
import com.teoktonos.bam.model.cards.CardStack

/**
 * Base class for all player actions.
 */
abstract class BamAction {
    var result: Result? = null
    open val isBam: Boolean get() = false

    open fun execute() {
        result = verify()
    }

    open fun verify(): Result = ResultOK
}

/**
 * Move a card from one stack to another.
 */
open class MoveAction(
    val player: BamPlayer,
    val source: CardStack,
    val target: CardStack
) : BamAction() {

    val card: Card? = source.top()

    override val isBam: Boolean
        get() = player.game.rules.isBam(this)

    override fun execute() {
        result = verify()
        if (result?.OK == true) {
            source.moveTo(target)
        }
    }

    override fun verify(): Result =
        player.game.rules.verifyMove(player, source, target)

    override fun toString(): String =
        "${player.name}: MOVE $card FROM ${source.name} TO ${target.name} ($result)"
}

/** Convenience action: move from pull to dump */
class DumpAction(player: BamPlayer) :
    MoveAction(player, player.zone.pull, player.zone.dump)

/** Base class for actions that reveal a card (Pull or Reveal Hell) */
open class ShowTopAction(val player: BamPlayer, val target: CardStack) : BamAction() {

    override val isBam: Boolean get() = player.game.rules.isBam(this)

    override fun execute() {
        result = verify()
        if (result?.OK == true) {
            target.showTop()
        }
    }

    override fun verify(): Result = player.game.rules.verifyShowTop(target)
}

class PullAction(player: BamPlayer) : ShowTopAction(player, player.zone.pull) {
    override fun toString() = "${player.name}: PULL CARD"
}

class RevealAction(player: BamPlayer) : ShowTopAction(player, player.zone.hell) {
    override fun toString() = "${player.name}: REVEAL HELL"
}

class YieldAction(val player: BamPlayer) : BamAction() {
    override fun execute() {
        result = verify()
    }

    override fun verify(): Result = player.game.rules.verifyYield(player)
    override fun toString() = "${player.name}: YIELD"
}

class FlipAction(val player: BamPlayer) : BamAction() {

    override val isBam: Boolean get() = player.game.rules.isBam(this)

    override fun execute() {
        with(player.zone) {
            result = verify()
            if (result?.OK == true) {
                pull.takeFrom(dump, dump.size)
                pull.flip(reverse = false)
            }
        }
    }

    override fun verify(): Result = player.game.rules.verifyFlip(player)
    override fun toString() = "${player.name}: FLIP"
}

class QuitAction(val player: BamPlayer) : BamAction() {
    override fun toString() = "${player.name}: QUIT"
}

/** Wrapper used when a Bam (illegal move during danger) occurs */
class BamHappenAction(val attempted: BamAction) : BamAction() {
    override fun execute() {
        result = verify()
    }

    override fun toString() = "BAM! attempting $attempted"
}

class NoAction : BamAction()
