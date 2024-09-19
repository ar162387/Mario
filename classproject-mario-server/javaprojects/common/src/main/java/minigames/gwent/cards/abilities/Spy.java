package minigames.gwent.cards.abilities;

import minigames.gwent.GameState;


public class Spy implements Ability {

    /**
     * Called when the ability is activated
     * Draw 2 cards from your deck and place them in your hand
     * Add card to opponents board
     */
    @Override
    public void activate(GameState gamestate) {
        System.out.println("Spy ability activated");
        gamestate.getCurrentPlayer().drawFromDeck();
        gamestate.getCurrentPlayer().drawFromDeck();
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
        return "Spy";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "Draw 2 cards from your deck and place them in your hand";
    }
    
}
