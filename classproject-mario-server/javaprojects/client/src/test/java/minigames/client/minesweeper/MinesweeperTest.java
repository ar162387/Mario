package minigames.client.minesweeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinesweeperTest {
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

    /**
     * Tests 'getDifficulty()' and 'setDifficulty()'
     */
    @Test
    public void setAndGetGameDifficulty(){
        logger.info("Tests 'getDifficulty()' and 'setDifficulty()'");

        Minesweeper minesweeper = new Minesweeper();
        minesweeper.setDifficulty("easy");
        assertTrue(minesweeper.getDifficulty().equalsIgnoreCase("easy"));

        minesweeper.setDifficulty("medium");
        assertTrue(minesweeper.getDifficulty().equalsIgnoreCase("medium"));

        minesweeper.setDifficulty("hard");
        assertTrue(minesweeper.getDifficulty().equalsIgnoreCase("hard"));

    }

    /**
     * Tests changeGameState() and transitionTo() methods
     */
    @Test
    public void gameStateTest(){
        logger.info("Tests changeGameState() and transitionTo() methods");
        Minesweeper minesweeper = new Minesweeper();

        minesweeper.changeGameState(GameState.MENU);
        assertEquals(minesweeper.getGameState(), GameState.MENU);

        minesweeper.changeGameState(GameState.PLAYING);
        assertEquals(minesweeper.getGameState(), GameState.PLAYING);

        minesweeper.changeGameState(GameState.WIN);
        assertEquals(minesweeper.getGameState(), GameState.WIN);

        minesweeper.changeGameState(GameState.LOSE);
        assertEquals(minesweeper.getGameState(), GameState.LOSE);

        minesweeper.changeGameState(GameState.LEADERBOARD);
        assertEquals(minesweeper.getGameState(), GameState.LEADERBOARD);
    }

}
