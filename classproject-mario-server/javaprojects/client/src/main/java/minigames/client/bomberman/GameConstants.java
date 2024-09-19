package minigames.client.bomberman;

public class GameConstants {
    public static final int TILE_SIZE = 48;
    public static final double PLAYER_BOUNDING_BOX_WIDTH = TILE_SIZE * 0.66;
    public static final double PLAYER_BOUNDING_BOX_HEIGHT = TILE_SIZE * 0.4;
    public static final int BOARD_NO_OF_ROWS = 11;

    public static final int BOARD_NO_OF_COLUMNS = 13;
    public static final double ENEMY_SIZE_REDUCTION = 5; // EDIT: Changed to 5 to fix diagonal bomb hits for enemies

}
