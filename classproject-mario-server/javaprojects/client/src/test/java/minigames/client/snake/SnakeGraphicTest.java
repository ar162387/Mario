package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.client.snake.ui.SnakeGraphic;

/**
 * This class contains unit tests for the SnakeGraphic class, which represents the snake's 
 * graphical representation and movement logic in the Snake game. The tests cover initial setup, movement 
 * in all directions, growing the snake, and validating direction changes.
 */
class SnakeGraphicTest {

    private SnakeGraphic snakeGraphic;
    private final int boardWidth = 560;
    private final int boardHeight = 600;
    private final int cellSize = 23;
    private final int initialBodyParts = 6;

    /**
     * Sets up the test environment by initialising the SnakeGraphic instance 
     * before each test with specific board dimensions, cell size, and initial body parts.
     */
    @BeforeEach
    void setUp() {
        snakeGraphic = new SnakeGraphic(boardWidth, boardHeight, cellSize, initialBodyParts);
    }

    /**
     * Tests the initial setup of the snake. Checks that the initial number of body parts 
     * and direction are correctly set, and that the snake's initial positions are calculated as expected.
     */
    @Test
    void initialSnakeSetupTest() {

        assertEquals(initialBodyParts, snakeGraphic.getBodyParts());
        assertEquals('R', snakeGraphic.getDirection());
    
        // Starting X position for the head (segment 0)
        int startingX = 138;
    
        // Check initial positions
        for (int i = 0; i < initialBodyParts; i++) {
            int expectedX = startingX - (i * cellSize);
            assertEquals(expectedX, snakeGraphic.getX(i));
            assertEquals(0, snakeGraphic.getY(i));
        }
    }

    /**
     * Tests the movement of the snake to the right. Verifies that the snake's head moves to the right 
     * by one cell size and that the Y coordinate remains unchanged.
     */
    @Test
    void moveRightTest() {

        int initialX = snakeGraphic.getX(0);
        snakeGraphic.move();

        // Verify that the head has moved right
        assertEquals(initialX + cellSize, snakeGraphic.getX(0));
        assertEquals(0, snakeGraphic.getY(0));
    }

    /**
     * Tests the movement of the snake upward. First, it moves the snake down and then right, 
     * before changing direction to up. Verifies that the snake's head moves up by one cell size.
     */
    @Test
    void moveUpTest() {

        // Move down first to make sure the snake has moved
        snakeGraphic.setDirection('D');
        snakeGraphic.move(); // Move down
    
        // Move right to set up the scenario
        snakeGraphic.setDirection('R');
        snakeGraphic.move(); // Move right
        
        // Change direction to up
        snakeGraphic.setDirection('U');
    
        // Capture initial Y position
        int initialY = snakeGraphic.getY(0);
        
        // Move up
        snakeGraphic.move();
        
        // Verify that the head has moved up
        assertEquals(initialY - cellSize, snakeGraphic.getY(0));
        // Ensure X position remains the same
        assertEquals(snakeGraphic.getX(1), snakeGraphic.getX(0));
    }
    
    /**
     * Tests the movement of the snake downward. Checks that the snake's head moves down by one cell size 
     * and that the X coordinate remains unchanged.
     */
    @Test
    void moveDownTest() {

        snakeGraphic.move(); // setting hasMoved to true

        snakeGraphic.setDirection('D');
        int initialY = snakeGraphic.getY(0);
        int initialX = snakeGraphic.getX(0);
        
        snakeGraphic.move(); // Move down
    
        // Check that snake head moved down
        assertEquals(initialY + cellSize, snakeGraphic.getY(0));
        assertEquals(initialX, snakeGraphic.getX(0));
    }

    /**
     * Tests the movement of the snake to the left. Checks that the snake's head moves to the left 
     * by one cell size and that the Y coordinate remains unchanged.
     */
    @Test
    void moveLeftTest() {

        // getting snake in position to start test
        snakeGraphic.move();
        snakeGraphic.setDirection('D');
        snakeGraphic.move();

        // setting direct to 'L' and getting current X and Y values before moving
        snakeGraphic.setDirection('L');
        int initialX = snakeGraphic.getX(0);
        int initialY = snakeGraphic.getY(0);
        snakeGraphic.move();

        // Verify that the head has moved left
        assertEquals(initialX - cellSize, snakeGraphic.getX(0));
        assertEquals(initialY, snakeGraphic.getY(0));
    }

    /**
     * Tests the grow functionality of the snake. Checks that the snake's body parts count 
     * increases by one and that the new segment is placed correctly in the game.
     */
    @Test
    void growTest() {

        int previousBodyParts = snakeGraphic.getBodyParts();
        snakeGraphic.grow();

        assertEquals(previousBodyParts + 1, snakeGraphic.getBodyParts());

        // Verify that the new segment is placed correctly
        assertEquals(snakeGraphic.getX(previousBodyParts - 1), snakeGraphic.getX(previousBodyParts));
        assertEquals(snakeGraphic.getY(previousBodyParts - 1), snakeGraphic.getY(previousBodyParts));
    }

    /**
     * Tests that an invalid direction change (attempting to reverse direction) does not change 
     * the snake's current direction. Checks that the direction remains unchanged after an invalid change.
     */
    @Test
    void invalidDirectionChangeTest() {

        snakeGraphic.move(); // Move once to ensure hasMoved is true
        
        snakeGraphic.setDirection('D'); // Change to down
        snakeGraphic.move(); // Move down
    
        // At this point, the direction should be 'L'
        assertEquals('D', snakeGraphic.getDirection());
    
        snakeGraphic.setDirection('U'); // Try to reverse direction (should fail)
        snakeGraphic.move(); // Attempt to move up
    
        // The direction should still be 'D' after trying to reverse
        assertEquals('D', snakeGraphic.getDirection());
    }

    /**
     * Tests that a valid direction change is correctly applied to the snake. 
     * Checks that the direction is updated and that the snake moves in the new direction.
     */
    @Test
    void validDirectionChangeTest() {

        snakeGraphic.move(); // Move once to ensure hasMoved is true
    
        snakeGraphic.setDirection('U'); // Change to up
        snakeGraphic.move(); // Move again
    
        assertEquals('U', snakeGraphic.getDirection());
    }
}
