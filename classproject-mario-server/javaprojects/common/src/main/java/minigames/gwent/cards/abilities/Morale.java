package minigames.gwent.cards.abilities;

import java.util.List;

import minigames.gwent.GameState;
import minigames.gwent.cards.Card;
import minigames.gwent.cards.CardType;
import minigames.gwent.cards.PowerCard;




public class Morale implements Ability {

    

    /**
     * Called when the ability is activated
     * Increases the strength modifier of all the units in the row by the number of units with this ability
     */
    @Override
    public void activate(GameState gamestate) {
        List<Card> meleeCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.MELEE);
        List<Card> rangedCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.RANGE);
        List<Card> siegeCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.SIEGE);

        int meleeMoraleCount = 0;
        int rangedMoraleCount = 0;
        int siegeMoraleCount = 0;

        // Count the number of cards with Morale ability in each zone
        for (Card card : meleeCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale && ((PowerCard) card).getCardType() == CardType.MELEE) {
                meleeMoraleCount++;
            }
        }

        for (Card card : rangedCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale && ((PowerCard) card).getCardType() == CardType.RANGE) {
                rangedMoraleCount++;
            }
        }

        for (Card card : siegeCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale && ((PowerCard) card).getCardType() == CardType.SIEGE) {
                siegeMoraleCount++;
            }
        }

        // Set the morale modifier for each card in melee zone
        for (Card card : meleeCards) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(meleeMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(meleeMoraleCount);
                }
            }
        }

        // Set the morale modifier for each card in ranged zone
        for (Card card : rangedCards ) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(rangedMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(rangedMoraleCount);
                }
            }
        }

        // Set the morale modifier for each card in siege zone
        for (Card card : siegeCards) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(siegeMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(siegeMoraleCount);
                }
            }
        }
    }

    /**
     * Called when the ability is deactivated
     * Resets the strength modifier of all the units in the row
     */
    @Override
    public void deactivate(GameState gamestate) {
        List<Card> meleeCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.MELEE);
        List<Card> rangedCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.RANGE);
        List<Card> siegeCards = gamestate.getCurrentPlayer().getCardsInZone(CardType.SIEGE);

        int meleeMoraleCount = 0;
        int rangedMoraleCount = 0;
        int siegeMoraleCount = 0;

        // Count the number of cards with Morale ability in each zone
        for (Card card : meleeCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale) {
                meleeMoraleCount++;
            }
        }

        for (Card card : rangedCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale) {
                rangedMoraleCount++;
            }
        }

        for (Card card : siegeCards) {
            if (card instanceof PowerCard && ((PowerCard) card).getAbility() instanceof Morale) {
                siegeMoraleCount++;
            }
        }

        // Decrement the morale count for each zone
        if (meleeMoraleCount > 0) {
            meleeMoraleCount--;
        }
        if (rangedMoraleCount > 0) {
            rangedMoraleCount--;
        }
        if (siegeMoraleCount > 0) {
            siegeMoraleCount--;
        }

        // Update the morale modifier for each card in melee zone
        for (Card card : meleeCards) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(meleeMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(meleeMoraleCount);
                }
            }
        }

        // Update the morale modifier for each card in ranged zone
        for (Card card : rangedCards) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(rangedMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(rangedMoraleCount);
                }
            }
        }

        // Update the morale modifier for each card in siege zone
        for (Card card : siegeCards) {
            if (card instanceof PowerCard) {
                if (((PowerCard) card).getAbility() instanceof Morale) {
                    ((PowerCard) card).setCardMoraleModifier(siegeMoraleCount - 1);
                } else {
                    ((PowerCard) card).setCardMoraleModifier(siegeMoraleCount);
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
        return "Morale Boost";
    }

    /**
     * Returns the description of the ability
     * @return The description of the ability
     */
    @Override
    public String getDescription() {
        return "Increases the strength modifier of all the units in the row by 1";
    }
    
}
