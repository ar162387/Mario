package minigames.gwent;

import minigames.gwent.cards.Card;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hand manages the collection of cards a player holds.
 */
public class Hand {
    private List<Card> cards;

    /**
     * Default constructor that initialises an empty hand.
     */
    public Hand() {
        this.cards = new ArrayList<>();
    }

    /**
     * Constructor that initialises the hand with an already made list
     *
     * @param initialCards The initial list of cards to populate the hand.
     */
    public Hand(List<? extends Card> initialCards) {
        this.cards = new ArrayList<>(initialCards);
    }

    /**
     * Adds a card to the hand.
     *
     * @param card The card to add to the hand.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Removes a card from the hand.
     *
     * @param card The card to remove.
     * @return boolean if successful
     */
    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    /**
     * Gets a copy of the cards in the hand.
     *
     * @return A new list containing all the cards in the hand.
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }


    /**
     * Returns a string repr of the hand - Testing Purposes
     * @return A string with all the cards in the hand or "None" if empty.
     */
    @Override
    public String toString() {
        if (cards.isEmpty()) {
            return "None";
        } else {
            return cards.stream()
                    .map(Card::toString)
                    .collect(Collectors.joining(", "));
        }
    }

    protected List<Card> getOriginalCards() {
        return cards;
    }

}
