package minigames.client.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

public class TileTest {

    private static final Logger logger = LogManager.getLogger(TileTest.class);
    private GameController gameController;

    /**
     * This test is only to test if the test suite runs correctly
     */
    @Test
    public void testTestSuiteRuns() {
        logger.info("Dummy test to show the test suite runs");
        assertTrue(true);
    }

    //----- ctor test -----//

    @Test
    public void initializeTileTest(){
        logger.info("Test to see if tile is created correctly by default.");
        //Initialize a tile with default properties
        Tile tile = new Tile(0, 0, null);
        assertFalse(tile.isCleared());
        assertFalse(tile.isMine());
        assertEquals(State.NONE, tile.getState());
        assertEquals(0, tile.toInt());
    }

    //--------- test how the tile is cleared ---------//
    @Test
    public void clearTileTest(){
        logger.info("Test to see how tile is cleared.");
        //Initialize a tile with default properties
        Tile tile = new Tile(0, 0, null);
        assertFalse(tile.isCleared());
        // Clear and set the number of mines around as 3
        tile.clear(3);
        assertTrue(tile.isCleared());
        assertFalse(tile.isMine());
        assertEquals(State.NONE, tile.getState());
        // CLEARED_BIT (32) + 3 mines = 35
        assertEquals(35, tile.toInt());

        // The maximum allowed number is 8
        assertThrows(IllegalArgumentException.class, 
            ()->tile.clear(9),
            "Throw is expected for invalid number of mines");        

        assertThrows(IllegalArgumentException.class, 
            ()->tile.clear(-1),
            "Throw is expected for invalid number of mines");        
    }

    //--------- test how the tile mine functions work ---------//
    @Test
    public void minedTileTest(){
        logger.info("Test to see how tile is mined.");
        //Initialize a tile with default properties
        Tile tile = new Tile(0, 0, null);
        assertFalse(tile.isMine());
        tile.setMine();
        assertTrue(tile.isMine());
        assertEquals(State.NONE, tile.getState());
        // MINE_BIT (16)
        assertEquals(16, tile.toInt());

        tile.removeMine();
        assertFalse(tile.isMine());
        assertEquals(State.NONE, tile.getState());
        assertEquals(0, tile.toInt());
    }


    //--------- test how the tile status works ---------//
    @Test
    public void statusTileTest(){
        logger.info("Test to see how tile status works.");
        //Initialize a tile with default properties
        Tile tile = new Tile(0, 0, null);
        assertEquals(State.NONE, tile.getState());
        assertEquals(0, tile.toInt());

        tile.setNextState();
        assertEquals(State.FLAGGED, tile.getState());
        // FLAGGED_BIT (64)
        assertEquals(64, tile.toInt());

        tile.setNextState();
        assertEquals(State.QUESTION, tile.getState());
        // QUESTION_BIT (128)
        assertEquals(128, tile.toInt());

        tile.setNextState();
        assertEquals(State.NONE, tile.getState());
        assertEquals(0, tile.toInt());
    }

    //--------- test how the int conversion works ---------//
    @Test
    public void intConversionTileTest(){
        logger.info("Test to see how tile int conversion works.");
        //Initialize a tile with default properties
        Tile tile = new Tile(0, 0, null);
        assertEquals(0, tile.toInt());

        // 8 mines, Mine (16), Cleared (32), Flagged (64) 
        tile.fromInt(8 + 16 + 32 + 64);
        assertTrue(tile.isMine());
        assertTrue(tile.isCleared());
        assertEquals(State.FLAGGED, tile.getState());

        // 8 mines, Mine (16), Cleared (32), Flagged (64), Question (128)
        tile.fromInt(8 + 16 + 32 + 64 + 128);
        // Impossible to be Flagged and Question at the same time
        assertEquals(8 + 16 + 32 + 64, tile.toInt());
    }

    // TODO: rendering tests

}
