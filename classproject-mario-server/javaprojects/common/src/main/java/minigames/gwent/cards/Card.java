package minigames.gwent.cards;

import minigames.gwent.cards.abilities.Ability;

// Implementation of the Card Interface
public interface Card {
    String getName();
    String getDescription();
    String getIdentifier();
    Ability getAbility();
    CardType getCardType();
    String getFaction();
}
