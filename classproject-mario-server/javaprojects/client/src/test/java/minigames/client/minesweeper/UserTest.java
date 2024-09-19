package minigames.client.minesweeper;

import io.vertx.ext.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
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
     * Tests the get and set username methods
     */
    @Test
    public void getAndSetUserNameTest(){
        logger.info("Tests the get and set username methods");
        user user = new user("Jake", 100);
        assertEquals(user.getUserName(), "Jake");
        user.setUserName("Bob");
        assertEquals(user.getUserName(), "Bob");
    }

    /**
     * Tests the get and set score methods
     */
    @Test
    public void getAndSetScoreTest(){
        logger.info("Tests the get and set score methods");
        user user = new user("Jake", 100);
        assertEquals(user.getScore(), 100);
        user.setScore(200);
        assertEquals(user.getScore(), 200);
    }
}
