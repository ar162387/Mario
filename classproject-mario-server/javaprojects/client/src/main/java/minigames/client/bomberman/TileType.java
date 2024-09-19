package minigames.client.bomberman;

/**
 * Represents the different types of tiles that can exist in a Bomberman game.
 *
 *
 */
public enum TileType {
    EMPTY,
    INDESTRUCTIBLE_WALL,
    DESTRUCTIBLE_WALL,
    PLAYER,
    PLAYER_NORTH, PLAYER_SOUTH, PLAYER_EAST, PLAYER_WEST,
    PLAYER_WALK_NORTH1, PLAYER_WALK_NORTH2, PLAYER_WALK_EAST1, PLAYER_WALK_EAST2, PLAYER_WALK_EAST3,
    PLAYER_WALK_SOUTH1, PLAYER_WALK_SOUTH2, PLAYER_WALK_WEST1, PLAYER_WALK_WEST2, PLAYER_WALK_WEST3,
    ENEMY, ENEMY_ARMOUR, ENEMY_WIZARD,
    BOMB1, BOMB2, BOMB3, BOMB4, BOMB5, BOMB6,
    EXPLODE_CENTRE, EXPLODE_HORIZONTAL, EXPLODE_VERTICAL, EXPLODE_NORTH, EXPLODE_EAST, EXPLODE_SOUTH, EXPLODE_WEST,
    EXPLODE_FADE1, EXPLODE_FADE2, EXPLODE_FADE3,
    ;

    @Override
    public String toString() {
        return switch (this) {
            case EMPTY -> ".";
            case INDESTRUCTIBLE_WALL -> "#";
            case DESTRUCTIBLE_WALL -> "X";
            case PLAYER -> "P";
            case ENEMY -> "E";
            default -> "?";
        };
    }

    /**
     * Returns the TileType from the Ascii char
     * @param c character for ascii that represents the tileType
     * @return TileType
     */
    public static TileType fromChar(char c) {
        return switch (c) {
            case '.' -> EMPTY;
            case '#' -> INDESTRUCTIBLE_WALL;
            case 'X' -> DESTRUCTIBLE_WALL;
            case 'P' -> PLAYER;
            case 'E' -> ENEMY;
            default -> EMPTY;
        };
    }
}