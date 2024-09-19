package minigames.gwent.cards.abilities;

import java.util.List;

import minigames.gwent.GameState;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;

/**
 * Weather ability affecting melee units
 */
public class Frost implements Ability {
    

    /**
     * Called when the ability is activated
     * Sets the strength of all melee units to 1
     * @param gamestate The gamestate to activate the ability on
     */
    @Override
    public void activate(GameState gamestate) {
        List<Card> player1Board = gamestate.getPlayerOne().getCardsInZone(CardType.MELEE);
        List<Card> player2Board = gamestate.getPlayerTwo().getCardsInZone(CardType.MELEE);

        for (Card card : player1Board) {
            if (!card.getAbility().getName().equals("Hero")) {
                ((PowerCard) card).setCardWeatherModifier(1 - ((PowerCard) card).getCardBasePowerRating()); 
            }  
        }

        for (Card card : player2Board) {
            if (!card.getAbility().getName().equals("Hero")) {
                ((PowerCard) card).setCardWeatherModifier(1 - ((PowerCard) card).getCardBasePowerRating());   
            }
        }
    }

    /**
     * Called when the ability is deactivated
     * Sets the strength of all melee units to their base strength
     * @param gamestate The gamestate to deactivate the ability on
     */
    @Override
    public void deactivate(GameState gamestate) {
        List<Card> player1Board = gamestate.getPlayerOne().getCardsInZone(CardType.MELEE);
        List<Card> player2Board = gamestate.getPlayerTwo().getCardsInZone(CardType.MELEE);

        for (Card card : player1Board) {
            ((PowerCard) card).setCardWeatherModifier(0);   
        }

        for (Card card : player2Board) {
            ((PowerCard) card).setCardWeatherModifier(0);   

        }
    }


    /**
     * Returns the name of the ability
     * @return The name of the ability
     */
    @Override
    public String getName() {
        return "Biting Frost";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "Sets the strength of all melee units to 1";
    }
}