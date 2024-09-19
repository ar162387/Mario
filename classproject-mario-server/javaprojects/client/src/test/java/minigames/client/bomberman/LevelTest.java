package minigames.client.bomberman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

class LevelTest {

    private Level level;

    @BeforeEach
    void setUp() {
        // create level
        level = new Level(10, 10, 2, true, true, 20);
    }

    @Test
    void testGetTileType() {
        // borders test
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(0, 0)));
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(0, 5)));
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(9, 9)));

        // pillars
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(2, 2)));
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(4, 4)));

        // inner tiles (these might be EMPTY, DESTRUCTIBLE_WALL, or ENEMY)
        TileType innerTile = level.getTileType(new Point(1, 1));
        assertTrue(innerTile == TileType.EMPTY || innerTile == TileType.DESTRUCTIBLE_WALL || innerTile == TileType.ENEMY || innerTile == TileType.PLAYER);
            // Fixed chance of failure to build once in a while HOTFIX
        // player position (assuming there's only one player)
        boolean playerFound = false;
        playerFound = level.getLevel().values().stream().anyMatch(tileType -> tileType == TileType.PLAYER);
        assertTrue(playerFound, "Player should be present in the level");
    }

    @Test
    void testGetTileTypeOutOfBounds() {
        assertNull(level.getTileType(new Point(-1, -1)));
        assertNull(level.getTileType(new Point(10, 10)));
    }

    @Test
    void testLevelDimensions() {
        assertEquals(10, level.getHEIGHT());
        assertEquals(10, level.getWIDTH());
    }

    @Test
    void testEnemyCount() {
        int enemyCount = 0;
        for (int y = 0; y < level.getHEIGHT(); y++) {
            for (int x = 0; x < level.getWIDTH(); x++) {
                if (level.getTileType(new Point(x, y)) == TileType.ENEMY) {
                    enemyCount++;
                }
            }
        }
        assertEquals(2, enemyCount);
    }

    @Test
    void testPrintAscii() {
        String asciiMap = level.printAscii();
        assertNotNull(asciiMap);
        String[] lines = asciiMap.split("\n");
        assertEquals(10, lines.length);
        assertEquals(10, lines[0].length());
    }
    @Test
    public void testReplacePlayerAndEnemyWithEmpty() {
        Map<Point, TileType> newLevel = level.getLevel();
        newLevel.put(new Point(0, 0), TileType.PLAYER);
        newLevel.put(new Point(1, 0), TileType.ENEMY);
        newLevel.put(new Point(2, 0), TileType.INDESTRUCTIBLE_WALL);
        newLevel.put(new Point(3, 0), TileType.DESTRUCTIBLE_WALL);
        newLevel.put(new Point(4, 0), TileType.EMPTY);

        Map<Point, TileType> updatedLevel = level.replacePlayerAndEnemyWithEmpty(newLevel); //

        // Assert: Verify that Player and Enemy tiles are replaced with Empty, and others remain unchanged
        assertEquals(TileType.EMPTY, updatedLevel.get(new Point(0, 0)));
        assertEquals(TileType.EMPTY, updatedLevel.get(new Point(1, 0)));
        assertEquals(TileType.INDESTRUCTIBLE_WALL, updatedLevel.get(new Point(2, 0)));
        assertEquals(TileType.DESTRUCTIBLE_WALL, updatedLevel.get(new Point(3, 0)));
        assertEquals(TileType.EMPTY, updatedLevel.get(new Point(4, 0)));
    }
    @Test
    void testCreateUnionFind2D() {
        Map<Point, TileType> levelMap = new HashMap<>();
        levelMap.put(new Point(0, 0), TileType.INDESTRUCTIBLE_WALL);
        levelMap.put(new Point(0, 1), TileType.EMPTY);
        levelMap.put(new Point(1, 0), TileType.ENEMY);
        levelMap.put(new Point(1, 1), TileType.PLAYER);
        levelMap.put(new Point(2, 0), TileType.DESTRUCTIBLE_WALL);
        levelMap.put(new Point(2, 1), TileType.EMPTY);
        levelMap.put(new Point(3, 0), TileType.EMPTY);
        levelMap.put(new Point(3, 1), TileType.ENEMY);
        levelMap.put(new Point(4, 0), TileType.INDESTRUCTIBLE_WALL);
        levelMap.put(new Point(4, 1), TileType.EMPTY);

        UnionFind unionGrid = Level.createUnionFind2D(levelMap);

        assertTrue(unionGrid.isConnected(new Point(0, 1), new Point(1, 1)), "Points (0, 1) and (1, 1) should be connected");
        assertTrue(unionGrid.isConnected(new Point(1, 0), new Point(1, 1)), "Points (1, 0) and (1, 1) should be connected");
        assertTrue(unionGrid.isConnected(new Point(2, 1), new Point(3, 1)), "Points (2, 1) and (3, 1) should be connected");
        assertFalse(unionGrid.isConnected(new Point(0, 0), new Point(2, 0)), "Points (0, 0) and (2, 0) should not be connected");
        assertFalse(unionGrid.isConnected(new Point(0, 0), new Point(4, 0)), "Points (0, 0) and (4, 0) should not be connected");
    }

    @Test
    void testHasEnemyOneDiagonalAway() {
        Map<Point, TileType> levelMap = new HashMap<>();

        // Setting up the level map
        levelMap.put(new Point(3, 5), TileType.ENEMY); // Top-left diagonal
        levelMap.put(new Point(5, 5), TileType.EMPTY); // Top-right diagonal
        levelMap.put(new Point(3, 3), TileType.EMPTY); // Bottom-left diagonal
        levelMap.put(new Point(5, 3), TileType.EMPTY); // Bottom-right diagonal

        Point currentPosition = new Point(4, 4);
        assertTrue(level.hasEnemyOneDiagonalAway(levelMap, currentPosition), "Should find an enemy one diagonal away.");

        // Test when no enemy is present
        levelMap.put(new Point(3, 5), TileType.EMPTY); // Change to EMPTY
        assertFalse(level.hasEnemyOneDiagonalAway(levelMap, currentPosition), "Should not find an enemy one diagonal away.");
    }
}