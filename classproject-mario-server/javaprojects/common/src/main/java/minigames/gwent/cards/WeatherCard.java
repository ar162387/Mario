package minigames.gwent.cards;

import minigames.gwent.cards.abilities.Ability;

public class WeatherCard extends AbstractedCard {

    /**
     * WeatherCard Constructor
     * @param name The cards name
     * @param description A description of the card
     * @param identifier An identifier for the card
     * @param type The cards type. An enum
     * @param Ability The cards ability
     * @param faction The cards faction
     */
    public WeatherCard(String name, String description, String identifier, CardType type, Ability ability, String faction) {
        super(name, description, identifier, type, ability, faction);
    }

}
