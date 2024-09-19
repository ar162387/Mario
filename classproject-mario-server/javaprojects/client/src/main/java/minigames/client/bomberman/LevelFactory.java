package minigames.client.bomberman;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Used for creating set maps for multiplayer and testing I guess
 *
 * Usage Example :
 *
 *
 * return LevelFactory.getInstance().getLevelFromAscii(LevelFactory.MultiplayerMaps.PILLARS_AND_BOXES.getAscii());
 */
public class LevelFactory {
    private static LevelFactory instance;

    /**
     * Enums for prototype multiplayer maps
     */
    enum MultiplayerMaps {
        EMPTY, BOXES, PILLARS_AND_BOXES, ZIG_ZAG, SPIRAL;

        /**
         * Returns an ascii representation of a set multiplayer map
         * @return Multiline String of ascii
         */
        public String getAscii() {
            return switch (this) {
                case EMPTY ->
                        "#############\n" +
                        "#P.........P#\n" +
                        "#...........#\n" +
                        "#...XXXXX...#\n" +
                        "#...XXXXX...#\n" +
                        "#...XXXXX...#\n" +
                        "#...XXXXX...#\n" +
                        "#...XXXXX...#\n" +
                        "#...........#\n" +
                        "#P.........P#\n" +
                        "#############\n";
                case BOXES ->
                        "#############\n" +
                        "#P.X.X.X.X.P#\n" +
                        "#.X.X.X.X.X.#\n" +
                        "#X.X.X.X.X.X#\n" +
                        "#.X.X.X.X.X.#\n" +
                        "#X.X.X.X.X.X#\n" +
                        "#.X.X.X.X.X.#\n" +
                        "#X.X.X.X.X.X#\n" +
                        "#.X.X.X.X.X.#\n" +
                        "#P.X.X.X.X.P#\n" +
                        "#############\n";
                case PILLARS_AND_BOXES ->
                        "#############\n" +
                        "#P.X.X.X.X.P#\n" +
                        "#.#.#.#.#.#.#\n" +
                        "#X.X...X.X.X#\n" +
                        "#.#.#.#.#.#.#\n" +
                        "#X.X...X.X.X#\n" +
                        "#.#.#.#.#.#.#\n" +
                        "#X.X...X.X.X#\n" +
                        "#.#.#.#.#.#.#\n" +
                        "#P.X.X.X.X.P#\n" +
                        "#############\n";
                case ZIG_ZAG ->
                        "#############\n" +
                        "#.X.#P#.#.X.#\n" +
                        "#.#X#.#.#X#.#\n" +
                        "#X#.#.X.#.#X#\n" +
                        "#.#.#X#X#.#.#\n" +
                        "P...X...X...P\n" +
                        "#.#.#X#X#.#.#\n" +
                        "#X#.#.X.#.#X#\n" +
                        "#.#X#.#.#X#.#\n" +
                        "#.X.#.#P#.X.#\n" +
                        "#############\n";
                case SPIRAL ->
                        "#############\n" +
                        "#P.XXXXXXX.P#\n" +
                        "#.#X##X##X#.#\n" +
                        "#X#.....#.XX#\n" +
                        "#XX.#X#.X.#X#\n" +
                        "#X#.X.#X#.XX#\n" +
                        "#X#.#.#.#.XX#\n" +
                        "#XX.#.....XX#\n" +
                        "#.#X##X##X#.#\n" +
                        "#P.XXXXXXX.P#\n" +
                        "#############\n";
            };
        }
    }
    /**
     * Lazy implementation not thread safe
     * @return instance or create an instance
     */
    public static LevelFactory getInstance() {
        if (instance == null) {
            instance = new LevelFactory();
        }
        return instance;
    }

    /**
     * Give it an ascii formatted level it will return the level from that ascii
     * @param ascii String use \n for new row.
     * @return Level class
     */
    public Level getLevelFromAscii(String ascii) {
        HashMap<Point,TileType> newLevel = new HashMap<>();
        // get height of ascii
        int height = ascii.isEmpty() ? 0 : ascii.split("\n").length;

        // Get width of ascii (assuming all rows have the same width)
        int width = height > 0 ? ascii.split("\n")[0].length() : 0;

        // Get player positions
        HashSet<Point> playerPositions = new HashSet<>();
        // Parse the ASCII map and populate the HashMap
        String[] rows = ascii.split("\n");
        for (int y = 0; y < height; y++) {
            String row = rows[y];
            for (int x = 0; x < width; x++) {
                char tileChar = row.charAt(x);
                TileType tileType = TileType.fromChar(tileChar);
                if (tileType == TileType.PLAYER) playerPositions.add(new Point(x,y));
                newLevel.put(new Point(x, y), tileType);

            }
        }

        return new Level(newLevel, height, width, playerPositions);
    }
    /** private constructor
     */
    private LevelFactory(){
    }

}
