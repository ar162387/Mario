package minigames.gwent.cards;

import minigames.gwent.cards.abilities.Ability;


/**
 * PowerCard is a specific card type. This card is playable, and may be further specialised into either ranged or melee (initial concept) types.
 * The card extends the AbstractedCard abstract class.
 */
public class PowerCard extends AbstractedCard {
    // Card type, power rating, faction, and event-on-play attributes
    private int cardBasePowerRating;
    private int cardWeatherModifier;
    private int cardMoraleModifier;
    private int cardBondModifier;

    /**
     * PowerCard Constructor
     * @param name The cards name
     * @param description A description of the card
     * @param identifier An identifier for the card
     * @param type The cards type (melee, ranged, etc)
     * @param power The cards base power score
     * @param modifier The cards power modifier
     * @param faction The cards faction
     * @param ability The cards ability
     */
    public PowerCard(String name, String description, String identifier, CardType type, int power, int weathermodifier, int moralemodifier, int bondmodifier, String faction, Ability ability) {
        super(name, description, identifier, type, ability, faction);
        this.cardBasePowerRating = power;
        this.cardWeatherModifier = weathermodifier;
        this.cardMoraleModifier = moralemodifier;
        this.cardBondModifier = bondmodifier;
    }


    /**
     * Card power rating getter
     * @return The cards power rating
     */
    public int getCardBasePowerRating() {
        return this.cardBasePowerRating;
    }

    /**
     * Card weather modifier getter
     * @return The cards weather modifier
     */
    public int getCardWeatherModifier() {
        return this.cardWeatherModifier;
    }

    /**
     * Card morale modifier getter
     * @return The cards morale modifier
     */
    public int getCardMoraleModifier() {
        return this.cardMoraleModifier;
    }

    /**
     * Card bond modifier getter
     * @return The cards bond modifier
     */
    public int getCardBondModifier() {
        return this.cardBondModifier;
    }

    /**
     * Card power rating setter
     * @param power The new power rating of the card
     */
    public void setBaseCardPowerRating(int power) {
        this.cardBasePowerRating = power;
    }

    /**
     * Card power modifier setter
     * @param modifier The new power modifier of the card
     */
    public void setCardWeatherModifier(int weathermodifier) {
        this.cardWeatherModifier = weathermodifier;
    }

    /**
     * Card morale modifier setter
     * @param modifier The new morale modifier of the card
     */
    public void setCardMoraleModifier(int moralemodifier) {
        this.cardMoraleModifier = moralemodifier;
    }

    /**
     * Card bond modifier setter
     * @param modifier The new bond modifier of the card
     */
    public void setCardBondModifier(int bondmodifier) {
        this.cardBondModifier = bondmodifier;
    }

    // A placeholder method, used for instntiation testing.
    public void test() {
        System.out.println("Placeholder");
    }

    /**
     * Returns a string repr of the PowerCard - Testing Purposes
     * @return A string of this PowerCard
     */
    @Override
    public String toString() {
        return String.format("%s: %s (%d) (%d) [%s]",
                super.getName(),
                super.getDescription(),
                this.cardBasePowerRating,
                this.cardWeatherModifier,
                super.getFaction());
    }
}