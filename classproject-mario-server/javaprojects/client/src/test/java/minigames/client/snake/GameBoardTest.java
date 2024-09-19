package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import minigames.client.snake.ui.Apple;
import minigames.client.snake.ui.Fruit;
import minigames.client.snake.ui.GameBoard;
import minigames.client.snake.ui.PoisonedApple;
import minigames.client.snake.ui.SlowApple;
import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.SpeedyApple;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class contains unit tests for the GameBoard class, which is responsible for managing
 * the game board in the Snake game. The tests cover the initialisation of the GameBoard, fruit spawning,
 * drawing the snake, grid, and fruits as well as checking the apple-eating logic
 */
class GameBoardTest {

    private GameBoard gameBoard;
    private SnakeGame snakeGame;
    private SnakeGraphic snake;

    /**
     * Sets up the test environment by creating mock objects for SnakeGame and SnakeGraphic
     * and initialising the GameBoard instance before each test
     */
    @BeforeEach
    void setUp() {

        snakeGame = Mockito.mock(SnakeGame.class);
        snake = Mockito.mock(SnakeGraphic.class);
        gameBoard = Mockito.spy(new GameBoard(snakeGame, snake));

        // Mock the background image loading
        Mockito.doNothing().when(gameBoard).loadBackgroundImage();
        gameBoard.backgroundImage = Mockito.mock(Image.class); // Mock the background image to prevent NPE
    }

    /**
     * Tests that the GameBoard initialises the SnakeGame and SnakeGraphic correctly
     */
    @Test
    void gameBoardInitializationTest() {

        assertNotNull(gameBoard.snakeGame);
        assertNotNull(gameBoard.getSnake());

    }

    /**
     * Tests that the background image is loaded successfully in the GameBoard
     */
    @Test
    void loadBackgroundImageTest() {

        assertNotNull(gameBoard.backgroundImage);
    }

    /**
     * Tests the spawning of fruits on the game board. Checks that an Apple is spawned normally.
     */
    @Test
    void spawnFruitTest() {

        gameBoard.spawnFruit();

        // Check that an apple is spawned
        Fruit apple = gameBoard.getCurrentApple();
        assertNotNull(apple);
        assertTrue(apple instanceof Apple);
    }

    /**
     * Tests the logic for handling when an apple is eaten. Checks that the number of apples eaten increments
     * and that a new apple is spawned.
     */
    @Test
    void testAppleEaten() {
        // Create an apple to add to the game board
        Fruit mockApple = mock(Apple.class);

        // Add the apple to the apples list
        gameBoard.getApples().add(mockApple);

        // Set initial applesEaten count
        int initialApplesEaten = gameBoard.applesEaten;

        // Call the appleEaten method
        gameBoard.appleEaten(mockApple);

        // Verify that the apple has been removed from the apples list
        assertFalse(gameBoard.getApples().contains(mockApple));

        // Verify that the applesEaten counter has incremented
        assertEquals(initialApplesEaten + 1, gameBoard.applesEaten);
    }


    /**
     * Tests the drawing of the snake on the game board.
     * Checks that the snake graphics are correctly drawn on the Graphics object.
     */
    @Test
    void drawSnakeTest() {

        Graphics g = Mockito.mock(Graphics.class);

        // Set up the mock snake behavior
        Mockito.when(snake.getBodyParts()).thenReturn(3);
        Mockito.when(snake.getX(0)).thenReturn(50);
        Mockito.when(snake.getY(0)).thenReturn(50);
        Mockito.when(snake.getDirection()).thenReturn('R');
        Mockito.when(snakeGame.isRunning()).thenReturn(true);

        // Call drawSnake() method
        gameBoard.drawSnake(g);

        // Verify that the snake graphics are drawn correctly
        Mockito.verify(g, Mockito.times(3)).drawImage(Mockito.any(Image.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(gameBoard));
    }

    /**
     * Tests the drawing of the grid on the game board.
     * Checks that the grid lines are correctly drawn on the Graphics object.
     */
    @Test
    void drawGridTest() {

        Graphics g = Mockito.mock(Graphics.class);

        gameBoard.drawGrid(g);

        // Check that lines were drawn for the grid
        Mockito.verify(g, Mockito.atLeastOnce()).drawLine(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    /**
     * Tests the drawing of a fruit on the game board.
     * Checks that the fruit image is correctly drawn on the Graphics object.
     */
    @Test
    void drawFruitTest() {

        Graphics g = Mockito.mock(Graphics.class);
        Fruit fruit = Mockito.mock(Fruit.class);
        Mockito.when(fruit.getImage()).thenReturn(Mockito.mock(Image.class));

        gameBoard.drawFruit(g, fruit);

        // Check that the fruit image is drawn
        Mockito.verify(g).drawImage(Mockito.any(Image.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(gameBoard));
    }

    /**
     * Tests the painting of the game board's components, including the background, grid, snake, and fruits.
     * TEST CURRENTLY DISABLED - IN PROGRESS
     */
    @Disabled
    @Test
    void paintComponentTest() {

        Graphics g = Mockito.mock(Graphics.class);

        // Set up fruits to avoid NullPointerException
        Fruit apple = Mockito.mock(Fruit.class);
        Fruit poisonedApple = Mockito.mock(Fruit.class);

        // Ensure the mocked fruits have an image
        Mockito.when(apple.getImage()).thenReturn(Mockito.mock(Image.class));
        Mockito.when(poisonedApple.getImage()).thenReturn(Mockito.mock(Image.class));

        // Set the fruits in the gameBoard
        gameBoard.currentApple = apple;
        gameBoard.currentPoisonedApple = poisonedApple;

        // Add gameBoard to a JFrame and pack to set the size
        JFrame frame = new JFrame();
        frame.add(gameBoard);
        frame.pack();  // Causes the layout manager to layout the components

        // Invoke paintComponent
        gameBoard.paintComponent(g);

        // Check that background image was drawn
        Mockito.verify(g, Mockito.times(1)).drawImage(Mockito.any(Image.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.eq(gameBoard));
    }

    /*
     * Tests that Apple objects are created successfully and added to the List<Fruit> apples variable.
     */
    @Test
    void spawnAppleTest() {

        // Run the method
        gameBoard.spawnApple();

        // Check that an apple was added to the apples list
        List<Fruit> apples = gameBoard.getApples();
        assertEquals(1, apples.size());
        assertTrue(apples.get(0) instanceof Apple);
    }

    /*
     * Tests that Poisoned Apple objects are created successfully and added to the List<Fruit> powerupFruits variable.
     */
    @Test
    void spawnPoisonedAppleTest() {

        // Run the method
        gameBoard.spawnPoisonedApple();

        // Check that a poisoned apple was added to the powerupFruits list
        List<Fruit> powerupFruits = gameBoard.getPowerupFruits();
        assertEquals(1, powerupFruits.size());
        assertTrue(powerupFruits.get(0) instanceof PoisonedApple);
    }

    /*
     * Tests that Speedy Apple objects are created successfully and added to the List<Fruit> powerupFruits variable.
     */
    @Test
    void spawnSpeedyAppleTest() throws Exception {

        // Run the method
        gameBoard.spawnSpeedyApple();

        // Check that a speedy apple was added to the powerupFruits list
        List<Fruit> powerupFruits = gameBoard.getPowerupFruits();
        assertEquals(1, powerupFruits.size());
        assertTrue(powerupFruits.get(0) instanceof SpeedyApple);
    }

    /*
     * Tests that Slow Apple objects are created successfully and added to the List<Fruit> powerupFruits variable.
     */
    @Test
    void spawnSlowAppleTest() throws Exception {

        // Run the method
        gameBoard.spawnSlowApple();

        // Check that a slow apple was added to the powerupFruits list
        List<Fruit> powerupFruits = gameBoard.getPowerupFruits();
        assertEquals(1, powerupFruits.size());
        assertTrue(powerupFruits.get(0) instanceof SlowApple);
    }

    /*
     * Tests that Fruit objects that are added to the PowerUpFruit List are removed after the appropriate
     * number of moves.
     */
    @Test
    void removeOldFruitsTest() {

        // Simulate adding a power-up fruit with a timer
        Fruit mockFruit = mock(Fruit.class);
        gameBoard.getPowerupFruits().add(mockFruit);
        gameBoard.getPowerupFruitsTimers().put(mockFruit, gameBoard.getSnakeMoves());

        // Simulate 30 moves passing
        gameBoard.snakeMoves = 30;

        // Run the method to remove old fruits
        gameBoard.removeOldFruits();

        // Check that the fruit has been removed
        assertTrue(gameBoard.getPowerupFruits().isEmpty());
        assertFalse(gameBoard.getPowerupFruitsTimers().containsKey(mockFruit));
    }
}
