package minigames.gwent.cards.abilities;

public class AbilityCreator {

	public static Ability createAbility(String abilityName) {
		switch (abilityName) {
			case "Rain":
				return new Rain();
			case "Frost":
				return new Frost();
			case "Fog":
				return new Fog();
			case "Clear":
				return new ClearWeather();
			case "Bond":
                return new Bond();
            case "Morale":
                return new Morale();
            case "Spy":
                return new Spy();
            case "Hero":
                return new Hero();
            case "":
                return new EmptyAbility();
			case "Medic":
				return new EmptyAbility();
			default:
				throw new IllegalArgumentException("Unknown ability: " + abilityName);
		}
	}
}
