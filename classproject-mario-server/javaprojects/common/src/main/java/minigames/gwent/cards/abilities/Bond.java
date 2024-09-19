package minigames.gwent.cards.abilities;

import java.util.ArrayList;
import java.util.List;

import minigames.gwent.GameState;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.PowerCard;

public class Bond implements Ability {
    
    /**
     * Called when the ability is activated
     * for each identical card in the relevant zone, increase the modifier by the base power rating
     */
    @Override
    public void activate(GameState gamestate) {
        // for each identical card in the relevant zone, increase the modifier by the base power rating
        List<Card> powerCards = gamestate.getCurrentPlayer().getPowerCardsOnBoard();
        List<Card> affectedcards = new ArrayList<>();
        for (Card card : powerCards) {
            if (card.getName().equals(this.getName()) && card.getAbility() instanceof Bond) {
                affectedcards.add(card);
            }
            if (affectedcards.size() > 1) {
                for (Card affectedcard : affectedcards) {
                    ((PowerCard) affectedcard).setCardBondModifier(((PowerCard) affectedcard).getCardBasePowerRating() * (int) Math.pow(2, affectedcards.size() - 1));
                }   
            }
        }
    }

    /**
     * Called when the ability is deactivated
     * for each identical card in the relevant zone, decrease the modifier by the base power rating
     */
    @Override
    public void deactivate(GameState gamestate) {
        // for each identical card in the relevant zone, decrease the modifier by the base power rating
        List<Card> powerCards = gamestate.getCurrentPlayer().getPowerCardsOnBoard();
        List<Card> affectedcards = new ArrayList<>();
        for (Card card : powerCards) {
            if (card.getName().equals(this.getName()) && card.getAbility() instanceof Bond) {
                affectedcards.add(card);
            }
            if (affectedcards.size() > 1) {
                for (Card affectedcard : affectedcards) {
                    ((PowerCard) affectedcard).setCardBondModifier(0);
                }   
            }
        }
    }

    /**
     * Returns the name of the ability
     * @return The name of the ability
     */
    @Override
    public String getName() {
        return "Tight Bond";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "Bond with another card of the same name";
    }
    
}
