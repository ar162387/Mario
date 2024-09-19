package minigames.gwent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import minigames.gwent.Deck.Deck;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;
import minigames.gwent.cards.WeatherCard;

/**
 * Player holds the players state.
 */
public class Player {
    private final String name;
    private final Hand hand;
    private final Deck deck;
    private final EnumMap<CardType, List<Card>> zones;
    private int health;
    private boolean passRound; // Player has ended the round (no longer play cards)

    /**
     * Constructor for Player.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this(name, new Deck());
    }

    /**
     * Constructor for Player that initialises with a premade deck.
     *
     * @param name The name of the player.
     * @param deck The deck of the player.
     */
    public Player(String name, Deck deck) {
        this.name = name;
        this.hand = new Hand();
        this.deck = deck;
        this.health = 2;
        this.zones = new EnumMap<>(CardType.class);
        for (CardType type : CardType.values()) {
            zones.put(type, new ArrayList<>());
        }
        this.passRound = false;
    }

    /**
     * Gets the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the player's hand
     *
     * @return the players Hand.
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Gets the player's deck
     *
     * @return the players Deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the player's health
     *
     * @return the players health.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets Power Cards on the board. Provide zone if you only want that zone.
     *
     * @param zone Optional parameter to specify a zone
     * @return A list containing the cards
     */
    public List<Card> getPowerCardsOnBoard(CardType... zone) {
        if (zone.length == 1) {
            return new ArrayList<>(zones.get(zone[0]));
        } else {
            List<Card> allCards = new ArrayList<>();
            for (List<Card> zoneCards : zones.values()) {
                allCards.addAll(zoneCards);
            }
            return allCards;
        }
    }

    /**
     * Gets all cards in a specific zone
     *
     * @param zone The zone to get cards from
     * @return A list of cards in the specified zone
     */
    public List<Card> getCardsInZone(CardType zone) {
        return new ArrayList<>(zones.get(zone));
    }

    /**
     * Gets weather cards. Provide zone if you only want that zone.
     *
     * @return A list of the weather cards
     */
    public List<Card> getWeatherCardsOnBoard() {
        return new ArrayList<>(zones.get(CardType.WEATHER));
    }

    /**
     * Adds a card to the board at a given position.
     *
     * @param position The position in the zone to place the card.
     * @param card     The card to be added
     */
    public void addCardToBoard(int position, Card card) {
        // Get the list of cards for the given zone
        List<Card> zoneCards = zones.get(card.getCardType());

        // Handle PowerCard
        if (card instanceof PowerCard) {
            if (position >= 0 && position <= zoneCards.size()) {
                zoneCards.add(position, card);
            } else {
                zoneCards.add(card); // Add to the end if position is out of bounds
            }
        }
        // Handle WeatherCard
        else if (card instanceof WeatherCard) {
            // Check for duplicate WeatherCard in the zone
            //TODO: Update to check both players zones
            if (zoneCards.contains(card)) {
                throw new IllegalArgumentException("This card is already active in the zone");
            } else {
                if (position >= 0 && position <= zoneCards.size()) {
                    zoneCards.add(position, card);
                } else {
                    zoneCards.add(card);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported card type");
        }
    }

    /**
     * Removes all weather cards
     */
    public void clearWeatherEffects() {
        // Get the list of weather cards in the weather zone
        List<Card> weatherCards = zones.get(CardType.WEATHER);

        // Clear the weather cards from the zone
        weatherCards.clear();
    }

    /**
     * Removes a card from the board.     *
     *
     * @param card The card to be removed
     */
    public void removeCardFromBoard(Card card) {
        List<Card> zoneCards = zones.get(card.getCardType());
        zoneCards.remove(card);
    }

    /**
     * Calculates the total strength of the player based on all PowerCard instances on the board.
     *
     * @return Total strength of the player.
     */
    public int calculatePlayerStrength() {
        int totalStrength = 0;
        for (CardType type : CardType.values()) {
            List<Card> zoneCards = zones.get(type);
            int zoneStrength = zoneCards.stream()
                    .filter(card -> card instanceof PowerCard)
                    .mapToInt(card -> ((PowerCard) card).getCardBasePowerRating() + ((PowerCard) card).getCardWeatherModifier()+ ((PowerCard) card).getCardMoraleModifier() + ((PowerCard) card).getCardBondModifier())
                    .sum();
            totalStrength += zoneStrength;
        }
        return totalStrength;
    }

    /**
     * Calculates the total strength of the player based on all PowerCard instances in the specified zone.
     *
     * @param zone The zone for which to calculate the total strength.
     * @return Total strength of the specified zone.
     */
    public int calculateZoneStrength(CardType zone) {
        int totalStrength;

        // Retrieve the cards in the specified zone
        List<Card> zoneCards = zones.get(zone);

        // Calculate the total strength for the specified zone
        totalStrength = zoneCards.stream().filter(card -> card instanceof PowerCard).mapToInt(card -> ((PowerCard) card).getCardBasePowerRating() + ((PowerCard) card).getCardWeatherModifier() + ((PowerCard) card).getCardMoraleModifier() + ((PowerCard) card).getCardBondModifier()).sum();

        return totalStrength;
    }

    /**
     * Clears all cards and weather effects from the board.
     */
    public void clearBoard() {
        for (List<Card> zone : zones.values()) {
            zone.clear();
        }
    }

    /**
     * Method to clear a specific zone
     */
    public void clearZone(CardType zone) {
        zones.get(zone).clear();
    }

    /**
     * A method to reduce the players health and returns their new health
     *
     * @return the new health for the player, 0 if dead
     */
    public int reduceHealth() {
        return --this.health;
    }

    /**
     * A method to get a card from the top of the deck and places into hand.
     */
    public void drawFromDeck() {
        Card card = deck.drawCardFromDeck();
        if (card == null) {
            System.out.println("Deck is empty. No card can be drawn.");
        } else {
            hand.addCard(card);
        }
    }

    /**
     * Player has signified they have ended their round and cannot play any more cards
     */
    public void passRound() {
        passRound = true;
    }

    /**
     * Gets the boolean if that player has ended their turn
     *
     * @return boolean if player has ended turn.
     */
    public boolean hasEndedRound() {
        return passRound;
    }

    /**
     * toString() method to provide a table representation of the player - Testing purposes.
     *
     * @return A string representing the player
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(name).append("\n");
        sb.append("Board Total Strength: ").append(calculatePlayerStrength()).append("\n");

        for (CardType type : CardType.values()) {
            sb.append(type.getName()).append(": ");
            List<Card> zoneCards = zones.get(type);
            if (!zoneCards.isEmpty()) {
                sb.append(zoneCards.stream().map(Card::toString).collect(Collectors.joining(", ")));
            } else {
                sb.append("None");
            }
            sb.append("Weather: ");
            List<Card> weatherCards = zones.get(CardType.WEATHER);
            if (weatherCards != null && !weatherCards.isEmpty()) {
                sb.append(weatherCards.stream().map(Card::toString).collect(Collectors.joining(", ")));
            } else {
                sb.append("None");
            }
            sb.append("\n");
        }

        sb.append("Hand:\n").append(hand.toString());

        return sb.toString();
    }

}