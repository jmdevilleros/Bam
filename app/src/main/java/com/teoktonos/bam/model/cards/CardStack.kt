package com.teoktonos.bam.model.cards

/**
 * A stack (or pile) of cards. This is the core data structure of the game.
 *
 * Many game zones (pull, hell, dump, rows, BamPiles) are CardStacks.
 * The class is designed to be iterable and to support the move/take/flip operations
 * that the original PunProto implementation relied on.
 */
open class CardStack(val name: String = "") : Iterable<Card> {

    protected val cards: MutableList<Card> = mutableListOf()

    val size: Int get() = cards.size
    fun isEmpty(): Boolean = cards.isEmpty()
    fun isNotEmpty(): Boolean = cards.isNotEmpty()

    /** Returns the top card without removing it, or null if empty. */
    fun top(): Card? = cards.lastOrNull()

    /** Adds a card to the top of the stack. */
    fun add(card: Card) {
        cards.add(card)
    }

    /** Adds multiple cards to the top (in order). */
    fun addAll(newCards: Collection<Card>) {
        cards.addAll(newCards)
    }

    /**
     * Moves the top card of this stack to the target stack.
     * Does nothing if this stack is empty.
     */
    fun moveTo(target: CardStack) {
        if (cards.isNotEmpty()) {
            val card = cards.removeAt(cards.lastIndex)
            target.cards.add(card)
        }
    }

    /**
     * Takes [count] cards from the top of [source] and adds them to this stack.
     */
    fun takeFrom(source: CardStack, count: Int) {
        val actualCount = count.coerceAtMost(source.size)
        repeat(actualCount) {
            if (source.cards.isNotEmpty()) {
                val card = source.cards.removeAt(source.cards.lastIndex)
                cards.add(card)
            }
        }
    }

    /**
     * Flips the entire stack.
     * If reverse = true, the order is reversed (typical when recycling dump → pull).
     */
    fun flip(reverse: Boolean = true) {
        cards.forEach { it.flip() }
        if (reverse) {
            cards.reverse()
        }
    }

    /**
     * Forces the top card to be face up or face down.
     */
    fun showTop(faceup: Boolean = true) {
        top()?.faceup = faceup
    }

    // --- Iterable support ---
    override fun iterator(): Iterator<Card> = cards.iterator()

    override fun toString(): String = "$name(${cards.size})"
}
