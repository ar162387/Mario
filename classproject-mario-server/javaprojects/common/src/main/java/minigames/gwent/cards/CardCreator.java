package minigames.gwent.cards;

import io.vertx.core.json.JsonObject;
import minigames.gwent.cards.abilities.Ability;
import minigames.gwent.cards.abilities.AbilityCreator;

public class CardCreator {
	public static Card createCardFromJson(JsonObject jsonCard) {
		String name = jsonCard.getString("name");
		String description = jsonCard.getString("description");
		String identifier = jsonCard.getString("identifier");
		String type = jsonCard.getString("type");
		String faction = jsonCard.getString("faction");
		String abilityName = jsonCard.getString("ability");
		Ability ability = AbilityCreator.createAbility(abilityName);

		if (type.equals("WEATHER")) {
			return new WeatherCard(name, description, identifier, CardType.valueOf(type), ability, faction);
		} else {
			int power = jsonCard.getInteger("power");
			int weathermodifier = 0;
			int moralemodifier = 0;
			int bondmodifier = 0;
			return new PowerCard(name, description, identifier, CardType.valueOf(type), power, weathermodifier, moralemodifier, bondmodifier, faction, ability);
		}
	}

}
