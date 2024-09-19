package minigames.client.gwent;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.gwent.GameState;
import minigames.gwent.Player;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;
import minigames.gwent.cards.abilities.Bond;
import minigames.gwent.cards.abilities.Morale;

public class PlayerTest {

    private Player player;

    @BeforeEach
    void setup() {
        // Create the cards
        List<Card> cards = Arrays.asList(
                new PowerCard("Card1", "", "", CardType.MELEE, 10, 0, 0, 0, "", null),
                new PowerCard("Card2", "", "", CardType.MELEE, 5, 0, 0, 0, "", null),
                new PowerCard("Card3", "", "", CardType.RANGE, 10, 1, 0, 0, "", null),
                new PowerCard("Card4", "", "", CardType.SIEGE, 15, 0, 0, 0, "", null),
                new WeatherCard("Frost", "", "", CardType.WEATHER, null,"")
        );

        player = new Player("Test Player");


        for (int i = 0; i < cards.size(); i++) {
            player.getHand().addCard(cards.get(i));
            player.addCardToBoard(i, cards.get(i));
        }
    }

    // Test calculation of total player strength
    @Test
    void testCalculatePlayerStrength() {
        assertEquals(41, player.calculatePlayerStrength(), "Total strength should be 41.");
    }

    // Test adding a card to the board
    @Test
    void testAddCardToBoard() {
        PowerCard newCard = new PowerCard("Card5", "", "", CardType.MELEE, 3, 0, 0, 0, "", null);
        player.addCardToBoard(2, newCard);
        assertEquals(newCard, player.getPowerCardsOnBoard(CardType.MELEE).get(2), "PowerCard should be in players board at position 2");
    }

    // Test adding a card to a specific zone
    @Test
    void testAddCardToSpecificZone() {
        PowerCard newCard = new PowerCard("Card6", "", "", CardType.RANGE, 7, 0, 0, 0, "", null);
        player.addCardToBoard(0, newCard);
        assertEquals(newCard, player.getCardsInZone(CardType.RANGE).getFirst(), "New card should be in the specified zone at the correct position.");
    }

    // Test removing a card from the board
    @Test
    void testRemoveCardFromBoard() {
        Card card = player.getPowerCardsOnBoard().get(0);
        if (card instanceof PowerCard) {
            player.removeCardFromBoard(card);
            assertFalse(player.getPowerCardsOnBoard().contains(card), "Card should not be in players board after removal");
        }
    }

    // Test clearing all cards from the board
    @Test
    void testClearBoard() {
        player.clearBoard();
        assertTrue(player.getPowerCardsOnBoard().isEmpty(), "Board should be empty after clearing.");
    }

    // Test getting cards from a given zone and all cards on the board
    @Test
    void testGetCardsOnBoard() {
        assertEquals(5, player.getPowerCardsOnBoard().size(), "There should be 5 cards on the board.");
        assertEquals(2, player.getPowerCardsOnBoard(CardType.MELEE).size(), "There should be 2 cards in the Melee zone.");
    }

    // Test clearing a specific zone
    @Test
    void testClearZone() {
        player.clearZone(CardType.MELEE);
        assertTrue(player.getPowerCardsOnBoard(CardType.MELEE).isEmpty(), "Melee zone should be empty after clearing.");
    }


    // Test clearing all weather cards from the board
    @Test
    void testClearWeatherCards() {
        player.addCardToBoard(0, new WeatherCard("Rain", "", "", CardType.WEATHER, null,""));
        player.clearWeatherEffects();
        assertEquals(0, player.getCardsInZone(CardType.WEATHER).size(), "There should be 0 weather cards on the board.");
    }

    // Test Morale ability
    @Test
    void testMoraleAbility() {
        GameState gameState = new GameState(player, player);
        player.addCardToBoard(0, new PowerCard("Card5", "", "", CardType.SIEGE, 5, 0, 0, 0, "", new Morale()));

        // Activate Morale ability
        new Morale().activate(gameState);

        assertEquals(47, player.calculatePlayerStrength(), "Card strength should be 47.");

    }

    // Test Bond ability
    @Test
    void testBondAbility() {
        GameState gameState = new GameState(player, player);
        player.addCardToBoard(0, new PowerCard("Card6", "", "", CardType.SIEGE, 5, 0, 0, 0, "", new Bond()));
        player.addCardToBoard(0, new PowerCard("Card6", "", "", CardType.SIEGE, 5, 0, 0, 0, "", new Bond()));

        // Activate Bond ability
        new Bond().activate(gameState);

        assertEquals(51, player.calculatePlayerStrength(), "Total strength should be 51.");
    }
    
}
