package minigames.gwent.Deck;

import minigames.gwent.Hand;
import minigames.gwent.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck extends Hand {

    // Deck attributes
    private String deckFaction;
    private String deckName;
    private String deckDescription;
    private String deckIdentifier;
    private List<Card> originalDeck;


    /**
     * Default constructor, initialises am empty deck with nulled attributes
     */
    public Deck() {
        super();
        this.deckFaction = null;
        this.deckName = null;
        this.deckDescription = null;
        this.deckIdentifier = null;
        this.originalDeck = null;

    }

    /**
     * Alternate constructor, initialises a deck with a list of cards and attributes
     * @param initialCards
     * @param deckFaction
     * @param deckName
     * @param deckDescription
     * @param deckUniqueIdentifier
     */
    public Deck(List<? extends Card> initialCards, String deckFaction, String deckName, String deckDescription, String deckUniqueIdentifier){
        super(initialCards);
        this.deckFaction = deckFaction;
        this.deckName = deckName;
        this.deckDescription = deckDescription;
        this.deckIdentifier = deckUniqueIdentifier;
        this.originalDeck = new ArrayList<>(initialCards);
    }


    /**
     * Getter - return the decks faction
     * @return deckFaction
     */
    public String getDeckFaction() {
        return this.deckFaction;
    }

    /**
     * Getter - return decks name
     * @return deckName
     */
    public String getDeckName() {
        return this.deckName;
    }

    /**
     * Getter - return decks description
     * @return deckDescription
     */
    public String getDeckDescription() {
        return this.deckDescription;
    }

    /** 
     * Getter - return decks identifier
     * @return deckIdentifier
     */
    public String getDeckIdentifier() {
        return this.deckIdentifier;
    }

    /**
     * Setter - set the decks faction
     * @param deckFaction
     */
    public void setDeckFaction(String deckFaction) {
        this.deckFaction = deckFaction;
    }

    /**
     * Setter - set the decks name
     * @param deckName
     */
    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    /**
     * Setter - set the decks description
     * @param deckDescription
     */
    public void setDeckDescription(String deckDescription) {
        this.deckDescription = deckDescription;
    }

    /** 
     * Setter - set the deck identifier
     * @param deckIdentifier
     */
    public void setDeckIdentifier(String deckIdentifier) {
        this.deckIdentifier = deckIdentifier;
    }

    /**
     * Shuffle method - shuffles cards into a random order
     */
    public void shuffleDeck(){
        Collections.shuffle(getOriginalCards());
    }

    /**
     * Draw card method - draws a card from the top of the deck (front of the list), removing it from the deck and returning it
     * @return a card
     */
     public Card drawCardFromDeck() {
        List<Card> currentDeck = getOriginalCards();
        
        if (!currentDeck.isEmpty()) {
            String returnCard = currentDeck.get(0).getName();
            return currentDeck.remove(0);

        } else {
            return null;
        }  
     }
     

     /**
      * Reset deck method - resets the deck back to its original values
      */
      public void resetDeck() {
        List<Card> currentDeck = getOriginalCards(); 

        currentDeck.clear(); // Clear the current deck
        currentDeck.addAll(new ArrayList<>(originalDeck)); // Add all cards from the original deck
      }

}
