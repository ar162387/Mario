package minigames.client.bomberman;

/**
 * The Direction enum represents the four cardinal directions.
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 *               Lixang Li - lli32@myune.edu.au
 * For use when determining the sprite to display for the player and enemies.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;
    @Override
    public String toString() {
        return switch (this) {
            case UP -> "Up";
            case DOWN -> "Down";
            case LEFT -> "Left";
            case RIGHT -> "Right";
        };
    }
}
