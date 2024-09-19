package minigames.gwent.cards.abilities;

import minigames.gwent.GameState;

/**
 * An ability that can be activated on a players side of the board
 */public interface Ability {
     
    /**
    * Called when the ability is activated
    * @param gamestate The gamestate to activate the ability on
    */
    public void activate(GameState gamestate);


    /**
     * Called when the ability is deactivated
     * @param gamestate The gamestate to deactivate the ability on
     */
    public void deactivate(GameState gamestate);

    /**
     * Returns the name of the ability
     * @return The name of the ability
     */
    public String getName();

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    public String getDescription();


}
