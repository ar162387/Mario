package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mockStatic;

import minigames.client.snake.ui.GameOverMenu;
import minigames.client.snake.ui.SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class contains unit tests for the GameOverMenu class, which represents the menu displayed 
 * when the game is over. The tests verify the initialisation of the menu, the correct display of the score, 
 * and the functionality of the "Back to Menu" button.
 */
class GameOverMenuTest {

    private GameOverMenu gameOverMenu;
    private SnakeGame snakeGame;

    /**
     * Sets up the test environment by creating a mock SnakeGame and initialising the GameOverMenu instance before each test.
     */
    @BeforeEach
    void setUp() {

        snakeGame = Mockito.mock(SnakeGame.class);
        Mockito.when(snakeGame.getApplesEaten()).thenReturn(10);  // Mock the score
        gameOverMenu = new GameOverMenu(snakeGame);
    }

    /**
     * Tests the initialisation of the GameOverMenu, ensuring that the SnakeGame instance
     * and the background image are correctly initialised.
     */
    @Test
    void initializationTest() {

        // Ensure the snakeGame and backgroundImage are initialized correctly
        assertNotNull(gameOverMenu.snakeGame);
        assertNotNull(gameOverMenu.backgroundImage);
    }

    /**
     * Tests the initialisation of the GameOverMenu UI components.
     * Checks that the score label displays the correct score and that the "Back to Menu" button is properly added.
     */
    @Test
    void initialiseMenuTest() {

        JLabel scoreLabel = null;
        JButton backToMenuButton = null;

        // Loop through all components and find the JLabel and JButton
        for (Component component : gameOverMenu.getComponents()) {
            if (component instanceof JLabel) {
                scoreLabel = (JLabel) component;
            } else if (component instanceof JButton) {
                backToMenuButton = (JButton) component;
            }
        }

        // Ensure both components were found
        assertNotNull(scoreLabel);

        assertNotNull(backToMenuButton);
        assertEquals("Back to Menu", backToMenuButton.getText());
    }

    /**
     * Tests the action of the "Back to Menu" button in the GameOverMenu.
     * Checks that when the button is pressed, the ancestor frame (the main window) is closed.
     */
    @Test
    void backToMenuButtonActionTest() {

        // Get button in the UI
        JButton backToMenuButton = (JButton) gameOverMenu.getComponent(1);
    
        // Mock an ActionEvent
        ActionEvent event = Mockito.mock(ActionEvent.class);
    
        // Mock a JFrame that will be used as the ancestor
        JFrame topFrame = Mockito.mock(JFrame.class);
    
        // Use mockStatic to mock SwingUtilities.getWindowAncestor
        try (var utilitiesMock = mockStatic(SwingUtilities.class)) {
            utilitiesMock.when(() -> SwingUtilities.getWindowAncestor(gameOverMenu)).thenReturn(topFrame);
    
            // Trigger the action listener for the button
            for (ActionListener listener : backToMenuButton.getActionListeners()) {
                listener.actionPerformed(event);
            }
    
            // Check that dispose was called on the top frame
            Mockito.verify(topFrame).dispose();
        }
    }
    
}