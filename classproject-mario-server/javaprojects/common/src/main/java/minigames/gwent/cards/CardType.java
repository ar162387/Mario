package minigames.gwent.cards;

import java.awt.Color;

/**
 * Enum representing the types of card zones in the game Gwent.
 * Each type has a string representation and a description.
 */
public enum CardType {
    MELEE("Melee", "First Card Row", new Color(255, 105, 97)),
    RANGE("Range", "Second Card Row", new Color(119, 221, 119)),
    SIEGE("Siege", "Third Card Row", new Color(135, 206, 250)),
    WEATHER("Weather", "Weather Card Row", new Color(207, 207, 207));

    private final String name;
    private final String description;
    private final Color color;

    /**
     * Constructor for CardType enum.
     *
     * @param name          The string representation of the card type.
     * @param description   A brief description of the card type.
     * @param color         The color associated with the card type.
     */
    CardType(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
    /**
     * Gets the name of the card type
     *
     * @return The string representation.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the card type.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the color of the card type.
     *
     * @return The color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the initial character of the card type.
     *
     * @return The initial character.
     */
    public char getInitial() {
        return name.charAt(0);  // Returns the first character of the name string
    }

    /**
     * Returns the string representation of the card type when calling toString().
     * @return The name of the card type.
     */
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
