package minigames.gwent.cards.abilities;

import minigames.gwent.GameState;

public class Hero implements Ability {
    
    /**
     * Called when the ability is activated
     * Sets a cards strength and modifier to be unchangable by any other effects
     */
    @Override
    public void activate(GameState gamestate) {
        
    }

    /**
     * Called when the ability is deactivated
     * This ability does not need to be deactivated
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
        return "Hero";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "This unit is unaffected by weather or other effects";
    }
         
}
