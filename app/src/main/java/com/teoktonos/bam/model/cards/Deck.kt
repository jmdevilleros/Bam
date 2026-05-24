package com.teoktonos.bam.model.cards

/**
 * Factory for creating standard 52-card decks.
 */
object Deck {

    /**
     * Creates a standard 52-card deck.
     *
     * @param name       Name to assign to the resulting CardStacks (if wrapping them)
     * @param shuffled   Whether to shuffle the deck
     * @param facedown   Whether cards should start face down
     */
    fun create(
        name: String = "deck",
        shuffled: Boolean = true,
        facedown: Boolean = true
    ): List<Card> {
        val cards = mutableListOf<Card>()

        Suit.entries.forEach { suit ->
            Rank.entries.forEach { rank ->
                cards.add(Card(rank, suit, faceup = !facedown))
            }
        }

        if (shuffled) {
            cards.shuffle()
        }

        return cards
    }

    /**
     * Convenience method that returns a ready-to-use CardStack.
     */
    fun createAsStack(
        name: String = "deck",
        shuffled: Boolean = true,
        facedown: Boolean = true
    ): CardStack {
        val stack = CardStack(name)
        stack.addAll(create(name, shuffled, facedown))
        return stack
    }
}
