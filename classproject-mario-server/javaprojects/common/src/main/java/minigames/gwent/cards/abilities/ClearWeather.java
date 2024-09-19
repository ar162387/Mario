package minigames.gwent.cards.abilities;

import minigames.gwent.GameState;
import minigames.gwent.cards.CardType;


public class ClearWeather implements Ability {
    

    /**
     * Called when the ability is activated
     * Sets the strength of all units to their base strength
     */
    @Override
    public void activate(GameState gamestate) {
        
        gamestate.getPlayerOne().getCardsInZone(CardType.WEATHER).forEach(card -> card.getAbility().deactivate(gamestate));
        gamestate.getPlayerTwo().getCardsInZone(CardType.WEATHER).forEach(card -> card.getAbility().deactivate(gamestate));
        
        gamestate.getPlayerOne().clearWeatherEffects();
        gamestate.getPlayerTwo().clearWeatherEffects();

        }

    /**
     * Called when the ability is deactivated
     * Sets the strength of all units to their base strength
     */
    @Override
    public void deactivate(GameState gamestate) {
    }
        
    

    /**
     * Returns the name of the ability
     * @return The name of the ability
     */
    @Override
    public String getName() {
        return "Clear Weather";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "Clears all weather effects";
    }

}
