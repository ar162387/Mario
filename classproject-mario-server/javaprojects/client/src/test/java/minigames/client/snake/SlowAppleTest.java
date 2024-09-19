package minigames.client.snake;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Image;

import minigames.client.snake.ui.SlowApple;
import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;

/**
 * This class contains unit tests for the SlowApple class, which represents a slow apple 
 * in the Snake game. The tests cover the initialisation of the slow apple, the effect it applies to the snake, 
 * and checking that it has an associated image.
 */
class SlowAppleTest {

    private SlowApple slowApple;
    private SnakeGame game;
    private SnakeGraphic snake;

    @BeforeEach
    void setUp() {

        game = mock(SnakeGame.class);
        snake = mock(SnakeGraphic.class);
        slowApple = new SlowApple(23, 0, game);
    }

    /**
     * Tests that the SlowApple is initialised with the correct coordinates.
     * Checks that the X and Y coordinates of the slow apple are set as expected.
     */
    @Test
    public void slowAppleInitialisation() {

        // Test that the slowApple initalises correctly
        assertEquals(23, slowApple.getX());
        assertEquals(0, slowApple.getY());
    }

    /**
     * Tests that the effects of slowApple are applied to the snake.
     */
    @Test
    public void slowAppleApplyEffectTest() {

        // slow apple should not yet be eaten
        assertFalse(slowApple.isEaten());

        // apple effect to the snake
        slowApple.applyEffect(snake, game);

        // check that slowApple effect is applied and apple marked as eaten
        verify(game).setSpeedEffect("slow");
        verify(game).setSpeedEffectMovesRemaining(15);
        assertTrue(slowApple.isEaten());
    }

    /**
     * Tests that the SlowApple has an image associated with it.
     * Checks that the image is not null, which is necessary for rendering the slow apple on the gameboard
     */
    @Test
    public void slowAppleImageTest() {

        // Test that the slow apple has an image associated
        assertNotNull(slowApple.getImage());
    }
}

