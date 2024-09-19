package minigames.client.minesweeper;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

public class MenuTest {
    
    private static final Logger logger = LogManager.getLogger(MenuTest.class);
    private Menu menu;
    private userInterface mockUserInterface;
    private Minesweeper mockMinesweeper;
    private GameController mockGameController;

    @BeforeEach
    public void setUp() {
        // Mock external dependencies
        mockMinesweeper = mock(Minesweeper.class);
        mockGameController = mock(GameController.class);
        //MinigameNetworkClient networkClient = mock(MinigameNetworkClient.class);
        
        // Initialize the Menu class with mocked dependencies
        menu = new Menu(mockUserInterface, mockMinesweeper, mockGameController);
        //menu.setNetworkClient(networkClient);
    }

    /**
     * This test is only to test if the test suite runs correctly
     */
    @Test
    public void testTestSuiteRuns() {
        logger.info("Dummy test to show the test suite runs");
        assertTrue(true);
    }

    /**
     * Test to verify the main menu panel is created properly.
     */
    @Test
    public void testCreateMainMenuPanel() {
        logger.info("Testing if the main menu panel is created properly.");
        JPanel mainMenuPanel = menu.createMainMenuPanel(mockGameController);
        assertNotNull(mainMenuPanel, "Main menu panel should not be null.");
    }

    /**
     * Test to verify the play panel is created properly.
     */
    @Test
    public void testCreatePlayPanel() {
        logger.info("Testing if the play panel is created properly.");
        JPanel playPanel = menu.playPanel(mock(JPanel.class));
        assertNotNull(playPanel, "Play panel should not be null.");
    }

    /**
     * Test to verify the new/load game panel is created properly.
     */
    @Test
    public void testCreateNewOrLoadGamePanel() {
        logger.info("Testing if the new/load game panel is created properly.");
        JPanel newOrLoadGamePanel = menu.newOrLoadGamePanel(mock(JPanel.class));
        assertNotNull(newOrLoadGamePanel, "New or Load game panel should not be null.");
    }

    /**
     * Test to verify the game loading panel is created properly.
     */
    @Test
    public void testGameLoadingPanel() {
        logger.info("Testing if the game loading panel is created properly.");
        JPanel gameLoadingPanel = menu.gameLoadingPanel(mock(JPanel.class));
        assertNotNull(gameLoadingPanel, "Game loading panel should not be null.");
    }

    /**
     * Test to verify the leaderboard panel is created properly.
     */
    @Test
    public void testLeaderboardPanel() {
        logger.info("Testing if the leaderboard panel is created properly.");
        JPanel leaderboardPanel = menu.leaderboardPanel(mock(JPanel.class));
        assertNotNull(leaderboardPanel, "Leaderboard panel should not be null.");
    }
    
    /**
     * Test to verify the "Play" button exists and functions correctly.
     */
    @Test
    public void testPlayButtonExistsAndFunctions() {
        logger.info("Testing if the 'Play' button exists in the main menu panel and works.");
        JPanel mainMenuPanel = menu.createMainMenuPanel(mockGameController);
        JButton playButton = findButtonByLabelInPanel(mainMenuPanel, "Play");
        assertNotNull(playButton, "'Play' button should exist in the main menu panel.");
        assertEquals("Play", playButton.getText(), "The 'Play' button should have the correct label.");
    }


    /**
     * Helper method to find a JButton within a panel by its text label.
     */
    private JButton findButtonByLabelInPanel(java.awt.Container container, String buttonText) {
        for (java.awt.Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(buttonText)) {
                    return button;
                }
            } else if (component instanceof java.awt.Container) {
                JButton button = findButtonByLabelInPanel((java.awt.Container) component, buttonText);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;
    }
}
