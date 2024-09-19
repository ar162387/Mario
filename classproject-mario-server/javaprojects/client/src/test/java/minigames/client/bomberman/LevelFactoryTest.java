package minigames.client.bomberman;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class LevelFactoryTest {

    private LevelFactory levelFactory = LevelFactory.getInstance();

    @Test
    @DisplayName("Test for Ascii level creation")
    void testGetLevelFromAscii() {
        // Given
        String asciiMap =
                "########\n" +
                        "#..P...#\n" +
                        "#..X...#\n" +
                        "#...E..#\n" +
                        "########";

        // When
        Level level = levelFactory.getLevelFromAscii(asciiMap);


        // Check specific tiles
        assertEquals(TileType.INDESTRUCTIBLE_WALL, level.getTileType(new Point(0, 0)),"0,0 should be indestructible wall");
        assertEquals(TileType.PLAYER, level.getTileType(new Point(3, 1)), "3,1 should be player");
        assertEquals(TileType.DESTRUCTIBLE_WALL, level.getTileType(new Point(3, 2)),"3,2 should be destructible_wall");

        //check level and levelFactory work together
        List<String> expectedLines = Arrays.asList(asciiMap.split("\n"));
        List<String> actualLines = Arrays.asList(level.printAscii().split("\n"));

        assertLinesMatch(expectedLines, actualLines,"AsciiMap and level.printAscii should be equal"); // EDIT: It is hard to compare multilines of strings
    }
}
