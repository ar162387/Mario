package minigames.client.bomberman;


import java.util.*;
import java.awt.Point;
import java.util.stream.Collectors;
/**
 Procedural Level Development for (Bomberman) / other 2D games
 Creates a 2D level for bomberman like games

 Contributers: Lixang Li  - lli32@myune.edu.au
 Daniel Gooden - dgooden@myune.edu.au

 Sprites sourced by:
 Original Bomb Party sprite sheet by Matt Hackett of Lost Decade Games,
 expanded by Cem Kalyoncu and /usr/share.


 Point is a class from the java.awt package used to represent a location in a two-dimensional
 (2D) space. It has x and y coordinates, both of which are typically represented as integers.

 Returns a HashMap with Point(x,y) -> TileType enum

 // Create a new level with specified parameters
 int height = 10;
 int width = 15;
 int enemies = 5;
 boolean border = true;
 boolean pillars = true;
 int percentDestructible = 50;

 Level level = new Level(height, width, enemies, border, pillars, percentDestructible);

 // Retrieve the tile map for the game
 Map<Point, Tile> tileMap = level.getTileMap();

 // Print the level in ASCII format
 System.out.println(level.printAscii());


 */
public class Level {
    private final int BOMB_RADIUS = 3;
    private final int HEIGHT;
    private final int WIDTH;
    private final int PERCENT_DESTRUCTIBLE;

    private HashSet<Point> playerStartPositions;
    private Map<Point,TileType> level;
    private Map<Point, Tile> tileMap;
    private UnionFind unionFindGrid;
    private static Map<TileType, Tile> tilePrototypes = new HashMap<>();

    /**
     * Different constructors per 2D game
     * For Bomberman game only
     * @param height the height of the level
     * @param width the width of the level
     * @param enemies the number of enemies to place
     * @param border whether to add border walls
     * @param pillars whether to add pillars
     * @param percentDestructible percentage of destructible walls
     */
    public Level(int height, int width, int enemies, boolean border, boolean pillars, int percentDestructible) {
        this.HEIGHT = height;
        this.WIDTH = width;
        this.level = generateEmptyLevel(HEIGHT, WIDTH);
        if (percentDestructible > 90) {
            PERCENT_DESTRUCTIBLE = 90; // Hardcap max filler percent to 90%
            System.out.println("Percent destructible filler parameter too high. Capped at 90%");
        } else {
            PERCENT_DESTRUCTIBLE = percentDestructible;
        }

        if (border) {
            addBorderWalls();
        }

        if (pillars) {
            addPillars();
        }

        List<Point> candidatePositions = new ArrayList<>();
        int maxAttempts = 100; // Prevent infinite loop
        int attempts = 0;

        while (candidatePositions.isEmpty() && attempts < maxAttempts) {
            Map<Point, TileType> tempLevel = new HashMap<>(level); // Create a copy of level

            if (PERCENT_DESTRUCTIBLE > 0) {
                tempLevel = addDestructibleFiller(tempLevel, PERCENT_DESTRUCTIBLE);
            }

            if (enemies > 0) {
                tempLevel = addEnemies(tempLevel, enemies);
            }

            // Will check to see if player is placeable under certain conditions
            candidatePositions = findSuitablePlayerPositions(tempLevel);

            System.out.println("Canidates to place player characters are: " + candidatePositions); //FIXME
            if (!candidatePositions.isEmpty()) {
                level = tempLevel; // Save the successful configuration
            }

            attempts++;
        }

        if (candidatePositions.isEmpty()) {
            throw new IllegalStateException("Failed to generate a valid level configuration after " + maxAttempts + " attempts.");
        }

        level = addPlayer(candidatePositions);

        // Make a tileMap for tiling purposes
        // This is the adapter
        buildTilePrototypes();
        tileMap = new HashMap<>();
        level.forEach((key,tileType) -> {
            System.out.println(key);
            System.out.println(tileType.toString());
            tileMap.put(key, createTile(tileType));
        });
    }

    /**
     * Constructor for the LevelFactory
     */
    public Level(Map<Point, TileType> pointTileTypeMap , int height, int width, HashSet<Point> playerStartPositions) {
        this.HEIGHT = height; //
        this.WIDTH = width; //
        this.PERCENT_DESTRUCTIBLE = 0; // Mocking it because it is not used
        // Add the map
        this.level = pointTileTypeMap;

        this.playerStartPositions = playerStartPositions;

        // Make a tileMap for tiling purposes
        // This is the adapter
        buildTilePrototypes();
        tileMap = new HashMap<>();
        level.forEach((key,tileType) ->
                tileMap.put(key, createTile(tileType)));
    }

    //API FUNCTIONS

    /**
     * For multiplayer TODO gets a set of player start positions @ Dan
     */
    public HashSet<Point> getPlayerStartPositions() {
        return playerStartPositions;
    }
    /**
      * Returns a tileMap Hashmap with a Point(x,y) -> tile
      *
      */
     public Map<Point, Tile> getTileMap() {
        return tileMap; // Make a copy for tiling
    }
    /**
     Returns a Hashmap with a Point(x,y) -> tile
     */
    public Map<Point, TileType> getLevel() {
        return new HashMap<>(level); // Make a copy of the level
    }


    /**
     * Retrieves the type of tile at the specified point.
     */
    public TileType getTileType(Point point) {
        return level.get(point);
    }

    /**
     * Gets the height of the level.
     */
    public int getHEIGHT() {
        return HEIGHT;
    }

    /**
     * Gets the width of the level.
     */
    public int getWIDTH() {
        return WIDTH;
    }

    /**
     * Retrieves the tile at the specified point in the tile map.
     */
    public Tile getTile(Point point) {
        return tileMap.get(point);
    }

    /**
     * Search and finds the player location when creating a level * @return
     */
    public Point getPlayerLocation() {
        return level.entrySet().stream()
                .filter(tileType -> tileType.getValue() == TileType.PLAYER)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    /** Returns a list of Enemy objects
     * Search and finds the enemies location
     *  Creates the enemies then adds them to a list
     */
    public List<Enemy> getEnemies() {
        return level.entrySet().stream()
                .filter(tileType -> tileType.getValue() == TileType.ENEMY)
                .map(Map.Entry::getKey) // map to key
                .map(point -> DifficultyManager.getInstance().getEnemy(point.x, point.y))//TODO change lives to more than one
                .collect(Collectors.toList()); // collect list
    }

    /**
     * a ascii representation of the map
     * @return Stringh ascii println
     */
    public String printAscii() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Point point = new Point(x, y);
                TileType tile = level.getOrDefault(point, TileType.EMPTY);
                sb.append(tile.toString());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    /**
     * Replaces all Player and Enemy tiles with Empty tiles in the level map to fix empty tile and rendering issue.
     *
     * @param level The current level map
     * @return The updated level map with Player and Enemy tiles replaced by Empty tiles
     */
    public Map<Point, TileType> replacePlayerAndEnemyWithEmpty(Map<Point, TileType> level) {
        level.replaceAll((key, value) ->
                (value == TileType.PLAYER || value == TileType.ENEMY) ? TileType.EMPTY : value
        );
        return level;
    }
    // HELPER FUNCTIONS

    /**
     * Factory pattern for building a hashmap of Tiles used in creating a level
     */

    private static void buildTilePrototypes() {
        tilePrototypes.put(TileType.EMPTY, new Tile(TileType.EMPTY, false, false, true, BombermanGraphics.getInstance().getTileSprite(TileType.EMPTY)));
        tilePrototypes.put(TileType.DESTRUCTIBLE_WALL, new Tile(TileType.DESTRUCTIBLE_WALL, true, false, false, BombermanGraphics.getInstance().getTileSprite(TileType.DESTRUCTIBLE_WALL)));
        tilePrototypes.put(TileType.INDESTRUCTIBLE_WALL, new Tile(TileType.INDESTRUCTIBLE_WALL, false, false, false, BombermanGraphics.getInstance().getTileSprite(TileType.INDESTRUCTIBLE_WALL)));
    }

    /**
     * Creates a new tile from the tilePrototypes
     * Default is to return an empty tile if it cannot be found
     * @param tileType
     * @return
     */
    private Tile createTile(TileType tileType) {
        return Optional.ofNullable(tilePrototypes.get(tileType))
                .map(Tile::copy)
                .orElseGet(() -> tilePrototypes.get(TileType.EMPTY).copy());
    }

    /**
     * Checks if the tile at the given point is empty.
     *
     * @param thisLevel
     * @param point     The point to check.
     * @return true if the tile is empty, false otherwise.
     */
    private boolean isEmptyTile(Map<Point, TileType> thisLevel, Point point) {
        TileType tile = thisLevel.get(point);
        return tile == TileType.EMPTY;
    }

    /**
     * Counts the number of empty tiles in the level.
     *
     * @return The number of empty tiles.
     */
    private int countEmptyTiles() {
        int count = 0;

        for (TileType tile : level.values()) {
            if (tile == TileType.EMPTY) {
                count++;
            }
        }

        return count;
    }

    /**
     * Boolean if its a wall (Including indestructible and destructible)
     * @param level
     * @param p
     * @return
     */

    private boolean isWall(Map<Point, TileType> level, Point p) {
        TileType tile = level.get(p);
        return tile == TileType.INDESTRUCTIBLE_WALL || tile == TileType.DESTRUCTIBLE_WALL;
    }

    /**
     * Checks if its an enemy tile
     * @param level
     * @param p
     * @return
     */
    private boolean isEnemy(Map<Point, TileType> level, Point p) {
        return level.get(p) == TileType.ENEMY;
    }

    /**
     * Checks if the point is in the contrains of the board
     * @param p
     * @return
     */
    private boolean isWithinBounds(Point p) {
        return p.x > 0 && p.x < WIDTH - 1 && p.y > 0 && p.y < HEIGHT - 1;
    }
    /**
     * Gets a list of all empty tile positions in the level.
     *
     * @param level The current level map
     * @return A list of Points representing empty tile positions
     */
    private List<Point> getEmptyTiles(Map<Point, TileType> level) {
        List<Point> emptyTiles = new ArrayList<>();

        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                Point p = new Point(x, y);
                if (isEmptyTile(level,p)) {
                    emptyTiles.add(p);
                }
            }
        }

        return emptyTiles;
    }

    /**
     * Chooses a specified number of unique random numbers from a range.
     *
     * @param min The minimum value (inclusive).
     * @param max The maximum value (inclusive).
     * @param count The number of unique random numbers to choose.
     * @return A set of randomly chosen numbers.
     */
    public static Set<Integer> chooseRandomNumbersInRange(int min, int max, int count) {
        if (count > (max - min + 1)) {
            throw new IllegalArgumentException("Count cannot be greater than the range of numbers.");
        }

        Random random = new Random();
        Set<Integer> resultSet = new HashSet<>();

        while (resultSet.size() < count) {
            int randomNumber = random.nextInt((max - min + 1)) + min;
            resultSet.add(randomNumber);
        }

        return resultSet;
    }

    /**
     * Makes a map of only empty tiles
     * @param height
     * @param width
     * @return
     */
    private Map<Point, TileType> generateEmptyLevel(int height, int width) {
        // Initialise the map with empty tiles
        level = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                level.put(new Point(x, y), TileType.EMPTY);
            }
        }
        return level;
    }

    /**
     *
     Add indestructible border walls if required for game
     */
    private Map<Point, TileType> addBorderWalls() {
        for (int y = 0; y < HEIGHT; y++) {
            level.put(new Point(0, y), TileType.INDESTRUCTIBLE_WALL);
            level.put(new Point(WIDTH - 1, y), TileType.INDESTRUCTIBLE_WALL);
        }
        for (int x = 0; x < WIDTH; x++) {
            level.put(new Point(x, 0), TileType.INDESTRUCTIBLE_WALL);
            level.put(new Point(x, HEIGHT - 1), TileType.INDESTRUCTIBLE_WALL);
        }
        return  level;
    }
    /**
     *
     Add indestructible pillars in the pattern if required for game
     ........
     .#.#.#.#
     ........
     .#.#.#.#
     etc.
     */
    private Map<Point, TileType> addPillars() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x % 2 == 0 && y % 2 == 0) {
                    level.put(new Point(x, y), TileType.INDESTRUCTIBLE_WALL);
                }
            }
        }
    return level;
    }

    /**
     * Adds destructible walls randomly to empty tiles in the level.
     *
     * @param percentDestructible The percentage of empty tiles to fill with destructible walls
     * @return The updated level map with added destructible walls
     */
    private Map<Point, TileType> addDestructibleFiller(Map<Point,TileType> levelFill, int percentDestructible) {
        int emptyTileCount = countEmptyTiles();
        int totalDestructibleWallsRequired = emptyTileCount * percentDestructible / 100;

        Set<Integer> randomIndices = chooseRandomNumbersInRange(0, emptyTileCount - 1, totalDestructibleWallsRequired);

        int counter = 0;
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (isEmptyTile(level, new Point(x,y))) {
                    if (randomIndices.contains(counter)) {
                        levelFill.put(new Point(x, y), TileType.DESTRUCTIBLE_WALL);
                    }
                    counter++;
                }
            }
        }

        return levelFill;
    }
    /**
     * Adds a specified number of enemies to random empty locations in the level.
     *
     * @param enemyCount The number of enemies to add
     * @return The updated level map with added enemies
     */
    private Map<Point, TileType> addEnemies(Map<Point,TileType> tempLevel,int enemyCount) {
        List<Point> emptyTiles = getEmptyTiles(tempLevel);

        if (emptyTiles.size() < enemyCount) {
            throw new IllegalStateException("Not enough empty tiles to place all enemies");
        }

        Random random = new Random();
        for (int i = 0; i < enemyCount; i++) {
            if (emptyTiles.isEmpty()) {
                break; // Stop if we run out of empty tiles
            }

            int randomIndex = random.nextInt(emptyTiles.size());
            Point enemyPosition = emptyTiles.remove(randomIndex);
            tempLevel.put(enemyPosition, TileType.ENEMY);
        }

        return tempLevel;
    }
    /**
     * Finds all positions in the level that satisfy the conditions for player placement.
     *  at least 2 empty squares next to them and no empty squares/line of sight to
     *      enemies or //TODO: other players
     *
     * @param tempLevel The current level map
     * @return A list of suitable positions for the player
     */
    private List<Point> findSuitablePlayerPositions(Map<Point, TileType> tempLevel) {

        unionFindGrid = createUnionFind2D(tempLevel);
        List<Point> suitablePositions = new ArrayList<>();

        List<Point> enemyList = tempLevel.entrySet().stream()
                .filter(entry -> entry.getValue() == TileType.ENEMY)
                .map(Map.Entry::getKey)
                .toList();

        tempLevel.forEach((p, tileType) -> {
            if (isEmptyTile(tempLevel, p) &&
                    (hasAtLeastTwoEmptyNeighbors(tempLevel, p) ||
                    hasAdjacentEmptyTiles(tempLevel,p,BOMB_RADIUS)) &&
                    !hasLineOfSightToEnemy(tempLevel, p) &&
                    !hasEnemyOneDiagonalAway(tempLevel, p) &&
                    isCompletable(p,unionFindGrid,enemyList)) {
                suitablePositions.add(p);
            }

        });

        return suitablePositions;
    }

    /**
     * Checks if there is an enemy tile located one diagonal square away
     * from the specified point on the level map.
     *
     * @param tempLevel
     * @param p
     * @return boolean
     */
    boolean hasEnemyOneDiagonalAway(Map<Point, TileType> tempLevel, Point p) {
        List<Point> diagonalPositions = new ArrayList<>();

        // Calculate diagonal positions
        diagonalPositions.add(new Point(p.x - 1, p.y + 1)); // Top-left
        diagonalPositions.add(new Point(p.x + 1, p.y + 1)); // Top-right
        diagonalPositions.add(new Point(p.x - 1, p.y - 1)); // Bottom-left
        diagonalPositions.add(new Point(p.x + 1, p.y - 1)); // Bottom-right

        return diagonalPositions.stream().anyMatch(point -> tempLevel.get(point) == TileType.ENEMY);
    }

    /**
     * Checks if the current position can access a list of points
     *
     * @param p Point
     * @param unionFindGrid UnionFind graph
     * @param objectives List of enemies or items or goals
     * @return
     */
    private boolean isCompletable(Point p, UnionFind unionFindGrid, List<Point> objectives) {
        return objectives.stream().allMatch(objective -> unionFindGrid.isConnected(p, objective));
    }

    /**
     * Checks if a given position has at least two adjacent empty neighboring tiles
     * in specific pairs (up-left, up-right, down-left, down-right).
     *
     * @param level The current level map represented as a Map of Points to TileTypes.
     * @param p The position to check as a Point object.
     * @return true if the position has at least two adjacent empty neighbors, false otherwise.
     */
    private boolean hasAtLeastTwoEmptyNeighbors(Map<Point, TileType> level, Point p) {
        Point up = new Point(p.x + 0, p.y + 1);
        Point right = new Point(p.x + 1, p.y);
        Point down = new Point(p.x, p.y - 1);
        Point left = new Point(p.x - 1, p.y);

        // Check pairs of adjacent empty neighbors
        boolean upLeftEmpty = isEmptyTile(level, up) && isEmptyTile(level, left);
        boolean upRightEmpty = isEmptyTile(level, up) && isEmptyTile(level, right);
        boolean downLeftEmpty = isEmptyTile(level, down) && isEmptyTile(level, left);
        boolean downRightEmpty = isEmptyTile(level, down) && isEmptyTile(level, right);

        // Return true if at least one pair of adjacent neighbors is empty
        return upLeftEmpty || upRightEmpty || downLeftEmpty || downRightEmpty;
    }
    /**
     * Checks if the current position has at least BOMB_RADIUS adjacent empty tiles
     * in straight lines (up, down, left, right).
     *
     * @param level The current level map represented as a Map of Points to TileTypes.
     * @param p The position to check as a Point object.
     * @param BOMB_RADIUS The required number of adjacent empty tiles.
     * @return true if the position has at least BOMB_RADIUS adjacent empty tiles, false otherwise.
     */
    private boolean hasAdjacentEmptyTiles(Map<Point, TileType> level, Point p, int BOMB_RADIUS) {
        int emptyCount = 0;

        emptyCount += countEmptyTilesInDirection(level, p, 0, 1, BOMB_RADIUS);   // Up
        emptyCount += countEmptyTilesInDirection(level, p, 0, -1, BOMB_RADIUS);  // Down
        emptyCount += countEmptyTilesInDirection(level, p, -1, 0, BOMB_RADIUS);  // Left
        emptyCount += countEmptyTilesInDirection(level, p, 1, 0, BOMB_RADIUS);   // Right

        return emptyCount >= BOMB_RADIUS; // Return true if at least BOMB_RADIUS empty tiles are found
    }
    /**
     * Counts the number of adjacent empty tiles in a specified direction.
     *
     * @param level The current level map represented as a Map of Points to TileTypes.
     * @param p The starting position as a Point object.
     * @param dx The change in x direction (1 for right, -1 for left).
     * @param dy The change in y direction (1 for up, -1 for down).
     * @param radius The maximum distance to check for empty tiles.
     * @return The number of empty tiles found in the specified direction.
     */
    private int countEmptyTilesInDirection(Map<Point, TileType> level, Point p, int dx, int dy, int radius) {
        int count = 0;
        for (int i = 1; i <= radius; i++) {
            Point neighbor = new Point(p.x + dx * i, p.y + dy * i);
            if (isEmptyTile(level, neighbor)) {
                count++;
            } else {
                break; // Stop counting if we hit a non-empty tile
            }
        }
        return count;
    }
    /**
     * Checks if a given position has a line of sight to any enemy.
     *
     * @param level The current level map
     * @param p The position to check
     * @return true if the position has a line of sight to an enemy, false otherwise
     */
    private boolean hasLineOfSightToEnemy(Map<Point, TileType> level, Point p) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int maxDistance = 4; // Maximum distance for line of sight

        for (int[] dir : directions) {
            Point current = new Point(p.x + dir[0], p.y + dir[1]);
            int distance = 1; // Start with distance 1 (adjacent tile)

            while (isWithinBounds(current) && !isWall(level, current) && distance <= maxDistance) {
                if (isEnemy(level, current)) {
                    return true;
                }
                current.x += dir[0];
                current.y += dir[1];
                distance++;
            }
        }

        return false;
    }

    /**
     * Adds a player to a random position that satisfies specific conditions.
     * The player must have at least 2 empty squares next to them and no line of sight to enemies.
     * If no suitable position is found, the method modifies the level until a solution is found.
     *
     * @return The updated level map with the player added, and the player's position
     */
    private Map<Point, TileType> addPlayer(List<Point> candidatePositions) {
        Random random = new Random();

        // Found a suitable position, place the player
        Point playerPosition = candidatePositions.get(random.nextInt(candidatePositions.size()));
        level.put(playerPosition, TileType.PLAYER);
        return level;
    }

    /**
     * Make unionfind searchable grid from the level
     * @return UnionFind
     */
    static UnionFind createUnionFind2D(Map<Point, TileType> levelUF) { // Map parameter for testing
        UnionFind unionGrid = new UnionFind();
        ArrayList<Point> adjacentPoints = new ArrayList<>(4);
        levelUF.forEach((point1,tileType) ->  {
            unionGrid.add(point1);


            adjacentPoints.add(new Point(point1.x - 1, point1.y)); // Left
            adjacentPoints.add(new Point(point1.x + 1, point1.y)); // Right
            adjacentPoints.add(new Point(point1.x, point1.y - 1)); // Up
            adjacentPoints.add(new Point(point1.x, point1.y + 1)); // Down

            // Stream filter adjacent points based on the tile type and union them
            adjacentPoints.stream()
                    .filter(point2 -> levelUF.get(point2) != null &&
                            (levelUF.get(point2) == TileType.EMPTY ||
                                    levelUF.get(point2) == TileType.ENEMY ||
                                    levelUF.get(point2) == TileType.PLAYER ||
                                    levelUF.get(point2) == TileType.DESTRUCTIBLE_WALL))
                    .forEach(filteredPoint -> unionGrid.union(point1, filteredPoint));
        });
        return unionGrid;
    }
}
