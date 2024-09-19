package minigames.gwent;

import java.util.ArrayList;
import java.util.List;

import minigames.gwent.Deck.Deck;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;

public class PlayerAI extends Player {
    Player opponentPlayer;

    /**
     * Default constructor, initialize a blank AIPlayer.
     * TODO: May not be used - currently in place for completeness / if required for testing. TBD whether required ongoing.
     */
    public PlayerAI(String aiName, Player opponent) {
        super(aiName, new Deck());
        this.opponentPlayer = opponent;
    }

    /**
     * Constructor for an AIPlayer with a premade deck.
     */
    public PlayerAI(String aiName, Deck deck, Player opponent) {
        super(aiName, deck);
        this.opponentPlayer = opponent;
    }

    public PowerCard identifyHighestPowerCard() {
        List<Card> currentHand = this.getHand().getCards();
        List<PowerCard> currentPowerCards = new ArrayList<>();

        System.out.println("Printing current hand "+currentHand);

        for (Card card : currentHand) {
            if (card instanceof PowerCard) {
                currentPowerCards.add((PowerCard) card);
            }
        }

        if (currentPowerCards.isEmpty()) {
            System.out.println("Hand is empty. Unable to identify most powerful card.");
            return null;
        }

        PowerCard highestPowerCard = currentPowerCards.get(0);

        for (PowerCard currCard : currentPowerCards) {
            if (currCard.getCardBasePowerRating() > highestPowerCard.getCardBasePowerRating()) {
                highestPowerCard = currCard;
            }
        }

        return highestPowerCard;
    }

    public WeatherCard findWeatherCard() {
        List<Card> currentHand = this.getHand().getCards();
        for (Card card : currentHand) {
            if (card instanceof WeatherCard) {
                return (WeatherCard) card;
            }
        }
        return null;
    }

    public boolean isWeatherCardOnBoard() {
        List<Card> myWeatherCards = this.getWeatherCardsOnBoard();
        List<Card> opponentsWeatherCards = opponentPlayer.getWeatherCardsOnBoard();

        return !myWeatherCards.isEmpty() || !opponentsWeatherCards.isEmpty();
    }

    public void chooseCard() {
        if (!isWeatherCardOnBoard()) {
            WeatherCard weatherCard = findWeatherCard();
            if (weatherCard != null) {
                this.addCardToBoard(0, weatherCard); //TODO: Update zone appropriate for Weather card
                // System.out.println(weatherCard);
                return;
            }
        } else {
            PowerCard highestPowerCard = this.identifyHighestPowerCard();
            if (highestPowerCard != null) {
                // System.out.println("This is the returned card "+highestPowerCard.getName());
                // Add the card to the appropriate zone based on its type
                switch (highestPowerCard.getCardType()) {
                    case MELEE:
                        this.addCardToBoard(0, highestPowerCard); //TODO: Add correct zone for card type
                        break;
                    case RANGE:
                        this.addCardToBoard(0, highestPowerCard); //TODO: Add correct zone for card type
                        break;
                    case SIEGE:
                        this.addCardToBoard(0, highestPowerCard); //TODO: Add correct zone for card type
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("No valid cards. Pass turn");
                this.passRound();
            }
        }
    }
}
