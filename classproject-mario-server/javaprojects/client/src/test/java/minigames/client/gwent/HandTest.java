package minigames.client.gwent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.gwent.Hand;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;

public class HandTest {

    private Hand hand;
    private Card card1;
    private Card card2;

    // Before each test, set up some cards and a hand
    @BeforeEach
    void setUp() {
        card1 = new PowerCard("Card1", "", "", CardType.MELEE, 10,0, 0, 0, "", null);
        card2 = new PowerCard("Card2", "", "", CardType.MELEE, 5, 0, 0, 0, "", null);
        hand = new Hand();
    }

    // Test adding a card to the player's hand
    @Test
    void testAddCardToHand() {
        // Hand should be empty
        assertTrue(hand.getCards().isEmpty(), "Hand should be initially empty");

        hand.addCard(card1);
        assertTrue(hand.getCards().contains(card1), "Hand should contain the added card");

        hand.addCard(card2);
        assertEquals(2, hand.getCards().size(), "Hand should contain two cards after adding another card");
    }

    // Test removing a card from the hand
    @Test
    void testRemoveCardFromHand() {
        hand.addCard(card1);
        hand.addCard(card2);

        hand.removeCard(card1);

        assertFalse(hand.getCards().contains(card1), "Hand should no longer contain the removed card");
        assertEquals(1, hand.getCards().size(), "Hand should contain one card after removal");

        Card card3 = new PowerCard("Card3", "", "", CardType.MELEE, 8, 0, 0, 0, "", null);
        hand.removeCard(card3); // Shouldnt change
        assertEquals(1, hand.getCards().size(), "Hand size should remain unchanged after attempting to remove a non-existent card");
    }
}