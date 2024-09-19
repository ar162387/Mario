package minigames.client.gwent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.gwent.Player;
import minigames.gwent.PlayerAI;
import minigames.gwent.Deck.Deck;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;

public class PlayerAITest {
    
    private Player playerOne;
    private PlayerAI playerTwo;

    @BeforeEach
    void setUp() {
        List<Card> cards = Arrays.asList(
        new PowerCard("Card1", "", "", CardType.MELEE, 10, 0, 0, 0, "", null),
        new PowerCard("Card2", "", "", CardType.MELEE, 5, 0, 0, 0, "", null),
        new PowerCard("Card3", "", "", CardType.RANGE, 10, 1, 0, 0, "", null),
        new PowerCard("Card4", "", "", CardType.SIEGE, 15, 0, 0, 0, "", null),
        new WeatherCard("Frost", "", "", CardType.WEATHER, null, null)
        );

        playerOne = new Player("Player one", new Deck(cards, "Some Faction", "Player ones deck","A description of the deck", "123abc"));
        playerTwo = new PlayerAI("Player two", new Deck(cards, "Some Faction", "Player twos deck","A description of the deck", "123abc"), playerOne);

        // Add cards to playerTwo's hand
        cards.forEach(card -> playerTwo.getHand().addCard(card));
        // Add cards to playerOnes's hand
        cards.forEach(card -> playerOne.getHand().addCard(card));
    }

    @Test
    void testIdentifyHighestPowerCard() {
        PowerCard highestPower = playerTwo.identifyHighestPowerCard();

        assertNotNull(highestPower, "Highest PowerCard is not null");
        assertEquals(15, highestPower.getCardBasePowerRating(), "Highest PowerCard should be power 15");
        assertEquals(CardType.SIEGE, highestPower.getCardType(), "Highest PowerCard should be type SIEGE");
        
        System.out.println(highestPower.getName());
    }

    @Test
    void testFindWeatherCard() {
        Card weatherCard = playerTwo.findWeatherCard();

        assertNotNull(weatherCard, "WeatherCard should not be null");
        assertEquals("Frost", weatherCard.getName(), "WeatherCard is Frost");
        assertEquals(CardType.WEATHER, weatherCard.getCardType(), "WeatherCard should be of type WEATHER");

        System.out.println(weatherCard.getName());
    }

    @Test
    void testIsWeatherCardOnBoard() {
        assertFalse(playerTwo.isWeatherCardOnBoard(), "WeatherCard is not initially on the board.");

        playerTwo.addCardToBoard(0, new WeatherCard("Frost", "", "", CardType.WEATHER, null, null));
        assertTrue(playerTwo.isWeatherCardOnBoard(), "A WeatherCard is on the board");
    }

    @Test
    void testChooseCardOne() {
        playerTwo.chooseCard();
        
        // Check that a weather card was added to the board
        List<Card> boardWeatherCards = playerTwo.getWeatherCardsOnBoard();
            
        assertFalse(boardWeatherCards.isEmpty(), "A weather card should have been added to the board.");
        assertTrue(boardWeatherCards.get(0) instanceof WeatherCard, "The card added to the board should be a weather card.");
    }

    //expected result is a power card - card4
    @Test
    void testChooseCardTwo() {
        WeatherCard weatherCard = new WeatherCard("WeatherCard", "", "", CardType.WEATHER, null, null);
        playerTwo.addCardToBoard(0, weatherCard);

        playerTwo.chooseCard();

        // Get Cards in each of the zones
        List<Card> meleeZoneCards = playerTwo.getCardsInZone(CardType.MELEE);
        List<Card> rangeZoneCards = playerTwo.getCardsInZone(CardType.RANGE);
        List<Card> siegeZoneCards = playerTwo.getCardsInZone(CardType.SIEGE);

        // Check that meleeZoneCards and rangeZoneCards are empty and siegeZoneCards is not empty
        assertTrue(meleeZoneCards.isEmpty());
        assertTrue(rangeZoneCards.isEmpty());
        assertFalse(siegeZoneCards.isEmpty());

        // Check that card4 is the only card in siegeZoneCards
        assertTrue(siegeZoneCards.get(0).getName() == "Card4");


    }
}
