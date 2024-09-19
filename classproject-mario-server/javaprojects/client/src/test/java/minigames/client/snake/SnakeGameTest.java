package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;
import java.io.IOException;

import minigames.client.MinigameNetworkClient;
import minigames.client.snake.util.CollisionDetector;
import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.GameBoard;

import javax.swing.*;

/**
* This class contains unit tests for the SnakeGame class, which is the main game logic controller
* for the Snake game. The tests cover the initialisation of the game, starting the game, handling game over,
* and processing game actions.
*/
class SnakeGameTest {

    private SnakeGame snakeGame;
    private GameBoard mockGameBoard;
    private SnakeGraphic mockSnake;
    private CollisionDetector mockCollisionDetector;
    private MinigameNetworkClient mockMnClient;
    private Timer mockTimer;

   /**
    * Sets up the test environment by initialising the SnakeGame instance
    * and adding it to a JFrame before each test. Handles any IOException
    * that might occur during the setup.
    */
   @BeforeEach
    void setUp() throws IOException {

        mockMnClient = mock(MinigameNetworkClient.class);
        mockSnake = mock(SnakeGraphic.class);
        mockCollisionDetector = mock(CollisionDetector.class);
        mockGameBoard = mock(GameBoard.class);
        mockTimer = mock(Timer.class);

        snakeGame = spy(new SnakeGame(mockMnClient, "Player"));
        snakeGame.snake = mockSnake;
        snakeGame.collisionDetector = mockCollisionDetector;
        snakeGame.gameBoard = mockGameBoard;
        snakeGame.timer = mockTimer;
    }

   /**
    * Tests the initialisation of the SnakeGame to ensure that it starts
    * with the correct board dimensions and that the game object is properly created.
    */
   @Test
   void testInitialisation() {

       // Check game starts with correct board constants
       assertEquals(560, snakeGame.getBoardWidth());
       assertEquals(600, snakeGame.getBoardHeight());

       // CHeck game obj gets created
       assertNotNull(snakeGame);
   }

   /**
    * Tests the startGame method to check that the game starts correctly
    * and that the running state is set to true.
    */
   @Test
   void testGameStart() {

       snakeGame.startGame();

       // running should be set to true
       assertTrue(snakeGame.isRunning());
   }

   /**
    * Tests the gameOver method to check that the game correctly handles
    * the game-over state and that the game is no longer running.
    */
    @Test
    void testGameOver() {

        // Call gameOver method
        snakeGame.gameOver();

        // Verify that the game board is removed from the panel
        verify(snakeGame, times(1)).remove(mockGameBoard);

        // Verify that the game over menu is displayed
        assertNotNull(snakeGame.getComponent(0)); // Ensure GameOverMenu is added
    }

   /**
    * Tests the actionPerformed method to ensure that the game correctly processes
    * ction events - for example, those triggered by a timer to move the snake.
    */
   @Test
    void testActionPerformed() throws IOException {

        // Simulate the snake moving
        ActionEvent mockEvent = mock(ActionEvent.class);
        when(snakeGame.isRunning()).thenReturn(true);

        // Call actionPerformed method
        snakeGame.actionPerformed(mockEvent);

        // Check that the snake moved
        verify(mockSnake, times(1)).move();

        // Check that the game board processed a snake move
        verify(mockGameBoard, times(1)).snakeMoved();

        // Check for collisions
        verify(mockCollisionDetector, times(1)).checkSelfCollision();
        verify(mockCollisionDetector, times(1)).checkWallCollision();
    }

    /**
     * Tests the setSpeedEffect method in the SnakeGame class and ensures that the correct
     * delay is set for the game timer based on the speed effect applied. This tests for all
     * three speed effects - fast, slow and normal.
     */
   @Test
    void testSetSpeedEffect() {

        // Fast speed
        snakeGame.setSpeedEffect("fast");
        verify(snakeGame.timer).setDelay(100);

        // Slow speed
        snakeGame.setSpeedEffect("slow");
        verify(snakeGame.timer).setDelay(400);

        // Normal speed
        snakeGame.setSpeedEffect("normal");
        verify(snakeGame.timer).setDelay(200);
    }

    /**
     * Tests the updateAchievementsPanel method in the SnakeGameClass and ensures that the
     * method correctly updates the achievements panel.
     */
    @Test
    void testUpdateAchievementsPanel() {

        JPanel newAchievementsPanel = new JPanel();

        // Call the updateAchievementsPanel method
        snakeGame.updateAchievementsPanel(newAchievementsPanel);

        // Check that the new achievements panel is added
        assertEquals(newAchievementsPanel, snakeGame.getAchievementsPanel());
    }
}
