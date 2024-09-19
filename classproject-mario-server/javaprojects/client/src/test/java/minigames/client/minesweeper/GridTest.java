package minigames.client.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

public class GridTest {

    private static final Logger logger = LogManager.getLogger(GridTest.class);
    private GameController gameController;

    /**
     * This test is only to test if the test suite runs correctly
     */
    @Test
    public void testTestSuiteRuns() {
        logger.info("Dummy test to show the test suite runs");
        assertTrue(true);
    }

    //--------------------Grid.java tests -------------------//

    //-----initializeGrid() function tests-----//

    @Test
    public void initializeGridEasyTest(){
        logger.info("Test to see if the function initializeGrid works with parameter 'easy'.");
        //Initialize a grid with 'easy' difficulty 
        Grid grid = new Grid("easy", gameController, null);
        //Assert that the number of rows, columns and number of mines are correct
        assertEquals(5, grid.getRows());
        assertEquals(5, grid.getCols());
        assertEquals(5, grid.getNumMines());
    }

    @Test
    public void initializeGridMediumTest(){
        logger.info("Test to see if the function initializeGrid works with parameter 'medium'.");
        //Initialize a grid with 'medium' difficulty
        Grid grid = new Grid("medium", gameController, null);
        //Assert that the number of rows, columns and number of mines are correct
        assertEquals(8, grid.getRows());
        assertEquals(8, grid.getCols());
        assertEquals(15, grid.getNumMines());
    }

    @Test
    public void initializeGridHardTest(){
        logger.info("Test to see if the function initializeGrid works with parameter 'hard'.");
        //Initialize a grid with 'hard' difficulty
        Grid grid = new Grid("hard", gameController, null);
        //Assert that the number of rows, columns and number of mines are correct
        assertEquals(18, grid.getRows());
        assertEquals(18, grid.getCols());
        assertEquals(40, grid.getNumMines());
    }


    //-----placeMines() function test-----//
    @Test
    public void placeCorrectNumberOfMinesEasy() {
        logger.info("Test to see if the function placeMines places the correct number of mines on easy difficulty.");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null); 
        //Assert that the correct number of mines are placed
        int mineCount = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getTile(r,c).isMine()) {
                    mineCount++;
                }
            }
        }
        assertEquals(grid.getNumMines(), mineCount);
    }

    @Test
    public void placeCorrectNumberOfMinesMedium() {
        logger.info("Test to see if the function placeMines places the correct number of mines on medium difficulty.");

        //Initialize a grid with 'medium' difficulty
        Grid grid = new Grid("medium", gameController, null); 
        //Assert that the correct number of mines are placed
        int mineCount = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getTile(r,c).isMine()) {
                    mineCount++;
                }
            }
        }
        assertEquals(grid.getNumMines(), mineCount);
    }

    @Test
    public void placeCorrectNumberOfMinesHard() {
        logger.info("Test to see if the function placeMines places the correct number of mines on hard difficulty.");

        //Initialize a grid with 'hard' difficulty
        Grid grid = new Grid("hard", gameController, null); 
        //Assert that the correct number of mines are placed
        int mineCount = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getTile(r,c).isMine()) {
                    mineCount++;
                }
            }
        }
        assertEquals(grid.getNumMines(), mineCount);
    }


    //-------ClearTiles() & clearTile() function tests --------//

    @Test
    public void clearTilesWithAdjacentMines() {
        logger.info("Test to see if the function clearTiles() correctly clears tiles with adjacent mines.");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null); 
        //Clear all mines from the grid
        grid.clearMines();
        //Set a mine to tile (1,1)
        grid.getTile(1, 1).setMine();
        //clear tile (0,1)
        grid.clearTiles(0, 1); 
        //Assert that the tiles around (1,1) are cleared
        assertTrue(grid.getTile(0, 1).isCleared());
        assertFalse(grid.getTile(0, 0).isCleared());
        assertFalse(grid.getTile(0, 2).isCleared());

    }


    @Test
    public void clearTileRecursiveClearing() {
        logger.info("Test to see if the function clearTiles() correctly clears tiles recursively.");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null);
        //Clear all mines from the grid
        grid.clearMines();
        //Set a mine to tile (2,2)
        grid.getTile(2, 2).setMine(); 
        //Clear tile (0,0)
        grid.clearTiles(0, 0);
        // Assert that all tiles except the one with the mine are cleared
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (r == 2 && c == 2) {
                    assertFalse(grid.getTile(r, c).isCleared()); // The mine tile is not cleared
                } else {
                    assertTrue(grid.getTile(r, c).isCleared()); // All others should be cleared
                }
            }
        }
    }


    //---------numOfMines() function tests ---------//

    @Test
    public void numOfMines() {
        logger.info("Test to see if the function numOfMines() calculates the number of mines next to a tile");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null);
        //Clear all mines from the grid
        grid.clearMines();
        //Set a few mines
        grid.getTile(1, 1).setMine();
        grid.getTile(2, 2).setMine();
        grid.getTile(1, 3).setMine();
        //Assert that the number of mines next to a tile is correct
        assertEquals(0, grid.numOfMines(5, 5));
        assertEquals(1, grid.numOfMines(0, 0));
        assertEquals(2, grid.numOfMines(2, 1));
        assertEquals(3, grid.numOfMines(1, 2));

    }

    //---------updateScore() function tests ---------//
    @Test
    public void testUpdateScoreWithNoClearedTiles() {
        logger.info("Test to see if the function updateScore(), calculates the score correctly with no cleared tiles");
        Grid grid = new Grid("easy", gameController, null);

        grid.updateScore();
        assertEquals(0, grid.getScore());
    }

    @Test
    public void testUpdateScoreWithClearedTiles() {
        logger.info("Test to see if the function updateScore(), calculates the score correctly, with 2 cleared tiles next to a mine");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null);
        //Clear all mines from the grid
        grid.clearMines();
        grid.getTile(1, 0).setMine();
        // Manually clear some tiles
        grid.getTile(0, 0).clear(0);
        grid.getTile(0, 1).clear(0);

        grid.updateScore();
        assertEquals(2, grid.getScore());
    }

    @Test
    public void testUpdateScoreDoesNotDoubleCount() {
        logger.info("Test to see if the function updateScore(), calculates the score correctly, without doubling up");
        //Initialize a grid with 'easy' difficulty
        Grid grid = new Grid("easy", gameController, null);
        //Clear all mines from the grid
        grid.clearMines();
        // Manually clear some tiles
        grid.getTile(0, 0).clear(0);
        grid.getTile(0, 1).clear(0);

        grid.updateScore();
        assertEquals(2, grid.getScore());

        // Call updateScore again without clearing more tiles
        grid.updateScore();
        assertEquals(2, grid.getScore()); // Score should remain the same

        grid.getTile(3, 3).clear(0);
        grid.updateScore();
        assertEquals(3, grid.getScore());
    }

}
