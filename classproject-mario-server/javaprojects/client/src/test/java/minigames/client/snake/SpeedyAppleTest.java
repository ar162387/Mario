package minigames.client.snake;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import minigames.client.snake.ui.SpeedyApple;
import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;

/**
 * This class contains unit tests for the SpeedyApple class, which represents a speedy apple 
 * in the Snake game. The tests cover the initialisation of the speedy apple, the effect it applies to the snake, 
 * and checking that it has an associated image.
 */
class SpeedyAppleTest {

    private SpeedyApple speedyApple;
    private SnakeGame game;
    private SnakeGraphic snake;

    @BeforeEach
    void setUp() {

        game = mock(SnakeGame.class);
        snake = mock(SnakeGraphic.class);   
        speedyApple = new SpeedyApple(23, 0, game);
    }

    /**
     * Tests that the SpeedyApple is initialised with the correct coordinates.
     * Checks tha the X and Y coordinates of the speedy apple are set as expected.
     */
    @Test
    public void speedyAppleInitialisation() {
        
        // Test the speedyApple initialises correctly
        assertEquals(23, speedyApple.getX());
        assertEquals(0, speedyApple.getY());
    }

    /**
     * Tests that the effect of the speedyApple are applied to the snake.
     */
    @Test
    public void speedyAppleApplyEffectTest() {

        assertFalse(speedyApple.isEaten());

        speedyApple.applyEffect(snake, game);

        verify(game).setSpeedEffect("fast");
        verify(game).setSpeedEffectMovesRemaining(15);
        assertTrue(speedyApple.isEaten());
    }


    /**
     * Tests that the SpeedyApple has an image associated with it.
     * Checks that the image is not null, which is necessary for rendering the speedy apple on the gameboard
     */
    @Test
    public void speedyAppleImageTest() {

        // Test that the speedy apple has an image associated
        assertNotNull(speedyApple.getImage());
    }
}