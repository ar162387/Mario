package minigames.gwent.cards;

import minigames.gwent.cards.abilities.Ability;

/**
 * Implementation of the AbstractCard abstract class.
 * Basis for all implementable Card sub-types.
 * Implements shared attributes and setters+getters required by all cards. 
 */
public abstract class AbstractedCard implements Card {
    
    // Card name, description, and identifier attributes
    protected String cardName;
    protected String cardDescription;
    protected String cardIdentifier;
    protected Ability ability;
    protected CardType type;
    protected String faction;

    /**
     * Constructor
     * @param name The cards name
     * @param description A description of the card
     * @param identifier An identifier for the card
     * @param ability The cards ability
     * @param type The cards type
     * @param faction The cards faction
     */
    public AbstractedCard(String name, String description, String identifier, CardType type, Ability ability, String faction) {
        this.cardName = name;
        this.cardDescription = description;
        this.cardIdentifier = identifier;
        this.ability = ability;
        this.type = type;
        this.faction = faction;
    }

    /**
     * Card name getter
     * @return The cards name
     */
    @Override
    public String getName() {
        return this.cardName;
    }

    /**
     * Card description getter
     * @return The cards description
     */
    @Override
    public String getDescription() {
        return this.cardDescription;
    }

    /**
     * Card identifier getter
     * @return The cards identifier
     */
    @Override
    public String getIdentifier(){
        return this.cardIdentifier;
    }

    /**
     * Card type getter
     * @return The cards type
     */
    @Override
    public CardType getCardType() {
        return this.type;
    }
    
    /**
     * Card ability getter
     * @return The cards ability
     */
    @Override
    public Ability getAbility() {
        return this.ability;
    }

    /**
     * Card faction getter
     * @return The cards faction
     */
    @Override
    public String getFaction() {
        return this.faction;
    }

    // Card name setter
    public void setName(String name) {
        this.cardName = name;
    }

    // Card description setter
    public void setDescription(String description) {
        this.cardDescription = description;
    }

    // Card identifier setter
    public void setIdentifier(String identifier) {
        this.cardIdentifier = identifier;
    }

    // Card type setter
    public void setType(CardType type) {
        this.type = type;
    }

    // Card ability setter
    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    // Card faction setter
    public void setFaction(String faction) {
        this.faction = faction;
    }
}