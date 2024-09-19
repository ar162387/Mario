package minigames.client.minesweeper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
     * The Purpose of this class is to perform tests on the leaderboard class of minesweeper
     * Tests:
     * getRankTest
     * testShowRanking
     * testLeaderboardPanel
     * Author: Matt Hayes
*/

public class LeaderboardTest {
    private static final Logger logger = LogManager.getLogger(GridTest.class);
    private GameController gameController;
    private Leaderboard leaderboard;
    private Minesweeper minesweeper;
    private user[] users;
    
    /**
     * Set up objects
     */
    @BeforeEach
    public void setUp() {
   	 user[] users = {
            new user("AAA", 100),
            new user("BBB", 90),
            new user("CCC", 80),
            new user("DDD", 70),
            new user("EEE", 60),
            new user("FFF", 50),
            new user("GGG", 40),
            new user("HHH", 30),
            new user("III", 20),
            new user("JJJ", 10)
        };
    
		  gameController = new GameController(minesweeper);
        leaderboard = new Leaderboard(users, users.length, gameController);

    }
    
	 
	 /**
     * Test if a user can be inserted into the leaderboard
     */
    @Test
    public void getRankTest() {
        logger.info("Test to see if the function getRank works");
        
        user[] position = leaderboard.getPosition();

        // Assert that "AAA" with score 100 is in position 0
        assertEquals(100, position[0].getScore());
        assertEquals("AAA", position[0].getUserName());

        // Assert that "JJJ" with score 10 is in position 9
        assertEquals(10, position[9].getScore());
        assertEquals("JJJ", position[9].getUserName());

        leaderboard.getRank(new user("test", 11));
        
        position = leaderboard.getPosition();

        // Assert that "test" with score 11 is in position 9
        assertEquals(11, position[9].getScore());
        assertEquals("test", position[9].getUserName());
    }

    /**
     * Check if the ranks are in the correct spot
     */
    @Test
	 public void testShowRanking() {
	 	  logger.info("Test to see if ranking table works");
	 	  
	 	  user[] position = leaderboard.getPosition();
	 	  
        // Get the ranking panel
        JPanel rankingPanel = leaderboard.showRanking();
		  assertNotNull(rankingPanel, "Ranking panel should not be null");
		  
        // Assert that the ranking panel contains the correct number of components (labels)
        assertEquals(position.length, rankingPanel.getComponentCount(), "Ranking panel should have the same number of labels as users");

        // Verify each label text matches the expected user and score
        for (int i = 0; i < position.length; i++) {
            JLabel label = (JLabel) ((JPanel) rankingPanel.getComponent(i)).getComponent(0);
            String expectedText = (i + 1) + ". " + position[i].getUserName() + " - " + position[i].getScore();
            assertEquals(expectedText, label.getText(), "Label text should match the expected user ranking");
        }
		}
		
	 /**
     * Check creation of leaderboard panel
     */
    @Test
	 public void testLeaderboardPanel() {
	 	  logger.info("Test to see if the leaderboard panel works");
	 	  
        // Get the leaderboard panel
        JPanel leaderboardPanel = leaderboard.createLeaderboard();
		  assertNotNull(leaderboardPanel, "leaderboard panel should not be null");

		}
}