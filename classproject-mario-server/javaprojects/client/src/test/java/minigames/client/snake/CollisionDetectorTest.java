package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import minigames.client.snake.ui.*;
import minigames.client.snake.util.CollisionDetector;


/**
 * This class contains unit tests for the CollisionDetector class, which handles collision detection 
 * for the Snake game. The tests cover scenarios where the snake collides with walls, itself or a fruit.
 */
class CollisionDetectorTest {

    private SnakeGraphic snake;
    private CollisionDetector collisionDetector;
    private final int boardWidth = 560;
    private final int gridHeight = 14;
    private final int cellSize = 23;

    /**
     * Sets up the mock SnakeGraphic and initialises the CollisionDetector before each test.
     */
    @BeforeEach
    void setUp() {
        
        snake = Mockito.mock(SnakeGraphic.class);
        collisionDetector = new CollisionDetector(snake, boardWidth, gridHeight, cellSize);
    }

    /**
     * Tests that a collision with the left wall is correctly detected.
     * The snake's X coordinate is set to -1 to simulate a collision with the left boundary.
     */
    @Test
    void leftWallCollisionTest() {

        Mockito.when(snake.getX(0)).thenReturn(-1);
        Mockito.when(snake.getY(0)).thenReturn(50);

        boolean result = collisionDetector.checkWallCollision();
        assertTrue(result);
    }

    /**
     * Tests that a collision with the right wall is correctly detected.
     * The snake's X coordinate is set to the board width to simulate a collision with the right boundary.
     */
    @Test
    void rightWallCollisionTest() {

        Mockito.when(snake.getX(0)).thenReturn(boardWidth);
        Mockito.when(snake.getY(0)).thenReturn(50);

        boolean result = collisionDetector.checkWallCollision();
        assertTrue(result);
    }

    /**
     * Tests that a collision with the top wall is correctly detected.
     * The snake's Y coordinate is set to -1 to simulate a collision with the top boundary.
     */
    @Test
    void topWallCollisionTest() {

        Mockito.when(snake.getX(0)).thenReturn(50);
        Mockito.when(snake.getY(0)).thenReturn(-1);

        boolean result = collisionDetector.checkWallCollision();
        assertTrue(result);
    }

    /**
     * Tests that a collision with the bottom wall is correctly detected.
     * The snake's Y coordinate is set to the calculated bottom boundary to simulate a collision.
     */
    @Test
    void bottomWallCollisionGTest() {

        Mockito.when(snake.getX(0)).thenReturn(50);
        Mockito.when(snake.getY(0)).thenReturn(cellSize * gridHeight);

        boolean result = collisionDetector.checkWallCollision();
        assertTrue(result);
    }

    /**
     * Tests that a collision with itself is correctly detected.
     * The snake's head coordinates are set to overlap with one of its body parts to simulate self-collision.
     */
    @Test
    void selfCollisionTest() {

        Mockito.when(snake.getX(0)).thenReturn(50);
        Mockito.when(snake.getY(0)).thenReturn(50);
        Mockito.when(snake.getBodyParts()).thenReturn(3);
        Mockito.when(snake.getX(1)).thenReturn(50);
        Mockito.when(snake.getY(1)).thenReturn(50);
        Mockito.when(snake.getX(2)).thenReturn(30);
        Mockito.when(snake.getY(2)).thenReturn(50);

        boolean result = collisionDetector.checkSelfCollision();
        assertTrue(result);
    }


    /**
     * Tests that a collision with a fruit is correctly detected.
     * The snake's head coordinates are set to overlap with the fruit's coordinates to simulate a collision.
     */
    @Test
    void checkFruitCollisionTest() {

        // Set up snake's head position
        when(snake.getX(0)).thenReturn(100);
        when(snake.getY(0)).thenReturn(100);

        // Create a fruit at the same position as the snake's head
        Fruit mockFruit = mock(Fruit.class);
        when(mockFruit.getX()).thenReturn(100);
        when(mockFruit.getY()).thenReturn(100);

        // Create a list of fruits
        List<Fruit> fruits = new ArrayList<>();
        fruits.add(mockFruit);

        // Call the checkFruitCollision method
        Fruit collidedFruit = collisionDetector.checkFruitCollision(fruits);

        // Verify that the fruit was detected as collided
        assertEquals(mockFruit, collidedFruit);
    }
}
