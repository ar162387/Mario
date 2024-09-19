package minigames.client.gwent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.gwent.Deck.Deck;
import minigames.gwent.Player;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;

public class DeckTest {

    private Player playerOne;

    // Before each test; set up a list of cards, set up a Player and add cards to the players hand during initialisation via the constructor.
    @BeforeEach
    void setUp() {
        List<Card> cards = Arrays.asList(
        new PowerCard("Card1", "", "", CardType.MELEE, 10, 0, 0, 0, "", null),
        new PowerCard("Card2", "", "", CardType.MELEE, 5, 0, 0, 0, "", null),
        new PowerCard("Card3", "", "", CardType.RANGE, 10, 0, 0, 0, "", null),
        new PowerCard("Card4", "", "", CardType.SIEGE, 15, 0, 0, 0, "", null),
        new WeatherCard("Frost", "", "", CardType.WEATHER, null, null)
        );

        playerOne = new Player("Player one", new Deck(cards, "Some Faction", "Player ones deck","A description of the deck", "123abc"));
    }

    /*
     * Test shuffleDeck method 
     */ 
    @Test
    void testShuffleDeck() {
        List<Card> firtstOrder = new ArrayList<>(playerOne.getDeck().getCards());

        playerOne.getDeck().shuffleDeck();

        List<Card> shuffledOrder = new ArrayList<>(playerOne.getDeck().getCards());

        System.out.println("firstOrder top card: "+firtstOrder.get(0).getName());
        System.out.println("shuffleOrder top card: "+shuffledOrder.get(0).getName());
    }

    /*
     * Test drawCardFromDeck method
     */
    @Test
    void testDrawCardFromDeck() {
        Card drawnCard1 = playerOne.getDeck().drawCardFromDeck();
        assertEquals("Card1", drawnCard1.getName(), "First card drawn should be 'Card1'.");

        Card drawnCard2 = playerOne.getDeck().drawCardFromDeck();
        assertEquals("Card2", drawnCard2.getName(), "Second card drawn should be 'Card2'.");

        List<Card> remainingDeck = playerOne.getDeck().getCards();
        assertEquals(3, remainingDeck.size(), "Deck size after removing 2 cards should be 3.");
    }

    /*
     * Test resetDeck method
     */
    @Test
    void testResetDeck() {
        Card drawnCard1 = playerOne.getDeck().drawCardFromDeck();
        assertEquals("Card1", drawnCard1.getName(), "First card drawn should be 'Card1'.");

        Card drawnCard2 = playerOne.getDeck().drawCardFromDeck();
        assertEquals("Card2", drawnCard2.getName(), "Second card drawn should be 'Card2'.");

        List<Card> remainingDeck = playerOne.getDeck().getCards();
        assertEquals(3, remainingDeck.size(), "Deck size after removing 2 cards should be 3.");

        playerOne.getDeck().resetDeck();
        List<Card> finalOrder = new ArrayList<>(playerOne.getDeck().getCards());
        assertEquals("Card1", finalOrder.get(0).getName());
    }

    
}
