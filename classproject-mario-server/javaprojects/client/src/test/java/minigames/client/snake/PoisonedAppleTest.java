package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.PoisonedApple;

/**
 * This class contains unit tests for the PoisonedApple class, which represents a poisoned apple 
 * in the Snake game. The tests cover the initialisation of the poisoned apple, the effect it applies to the snake, 
 * and checking that it has an associated image.
 */
class PoisonedAppleTest {

    private PoisonedApple poisonedApple;
    private SnakeGraphic snake;
    private SnakeGame game;

    /**
     * Sets up the test environment by initializing a PoisonedApple instance and mocking 
     * the SnakeGraphic and SnakeGame objects before each test.
     */
    @BeforeEach
    void setUp() {
        poisonedApple = new PoisonedApple(23, 0);
        snake = Mockito.mock(SnakeGraphic.class);
        game = Mockito.mock(SnakeGame.class);
    }

    /**
     * Tests that the PoisonedApple is initialised with the correct coordinates.
     * Checks that the X and Y coordinates of the poisoned apple are set as expected.
     */
    @Test
    public void poisonedAppleInitialisationTest() {

        // Test the apple initialises correctly
        assertEquals(23, poisonedApple.getX());
        assertEquals(0, poisonedApple.getY());
    }

    /**
     * Tests the effect of the PoisonedApple when applied to the snake.
     * Checks that the game ends (gameOver is called) and that the poisoned apple is marked as eaten.
     */
    @Test
    public void poisonedAppleApplyEffectTest() {

        poisonedApple.applyEffect(snake, game);

        Mockito.verify(game).gameOver();

        assertTrue(poisonedApple.isEaten());

    }

    /**
     * Tests that the PoisonedApple has an image associated with it.
     * Checks that the image is not null, which is necessary for rendering the poisoned apple on the gameboard.
     */
    @Test
    public void poisonedAppleImageTest() {

        // Test that the apple has image associated
        assertNotNull(poisonedApple.getImage());
    }
}