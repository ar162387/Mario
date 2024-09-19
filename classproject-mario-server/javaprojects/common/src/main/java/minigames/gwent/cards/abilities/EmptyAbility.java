package minigames.gwent.cards.abilities;

import minigames.gwent.GameState;


public class EmptyAbility implements Ability {

    @Override
    public void activate(GameState gamestate) {
        // Do nothing
    }

    @Override
    public void deactivate(GameState gamestate) {
        // Do nothing
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "This card has no ability";
    }
}
