package minigames.client.snake;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Component;
import java.awt.event.KeyEvent;

import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.SnakeKeyAdapter;

/**
 * This class contains unit tests for the SnakeKeyAdapter class, which handles 
 * key events to control the direction of the snake in the Snake game. The tests check 
 * that the correct direction is set on the SnakeGraphic when arrow keys are pressed.
 */
class SnakeKeyAdapterTest {

    private SnakeKeyAdapter snakeKeyAdapter;
    private SnakeGraphic snakeGraphic;

    /**
     * Sets up the test environment by creating a mock SnakeGraphic
     * and initialising the SnakeKeyAdapter with it before each test.
     */
    @BeforeEach
    void setUp() {

        // Mock the SnakeGraphic instance
        snakeGraphic = Mockito.mock(SnakeGraphic.class);

        // Initialise the SnakeKeyAdapter with the mocked SnakeGraphic
        snakeKeyAdapter = new SnakeKeyAdapter(snakeGraphic);
    }

    /**
     * Tests that pressing the up arrow key correctly sets the snake's direction to 'U' (up).
     */
    @Test
    void testKeyUp() {

        KeyEvent keyEvent = new KeyEvent(
            Mockito.mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_UP,
            KeyEvent.CHAR_UNDEFINED,
            KeyEvent.KEY_LOCATION_STANDARD
        );

        snakeKeyAdapter.keyPressed(keyEvent);
        Mockito.verify(snakeGraphic).setDirection('U');
    }

    /**
     * Tests that pressing the down arrow key correctly sets the snake's direction to 'D' (down).
     */
    @Test
    void testKeyDown() {

        KeyEvent keyEvent = new KeyEvent(
            Mockito.mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_DOWN,
            KeyEvent.CHAR_UNDEFINED,
            KeyEvent.KEY_LOCATION_STANDARD
        );

        snakeKeyAdapter.keyPressed(keyEvent);
        Mockito.verify(snakeGraphic).setDirection('D');
    }

    /**
     * Tests that pressing the left arrow key correctly sets the snake's direction to 'L' (left).
     */
    @Test
    void testKeyLeft() {

        KeyEvent keyEvent = new KeyEvent(
            Mockito.mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_LEFT,
            KeyEvent.CHAR_UNDEFINED,
            KeyEvent.KEY_LOCATION_STANDARD
        );

        snakeKeyAdapter.keyPressed(keyEvent);
        Mockito.verify(snakeGraphic).setDirection('L');
    }

    /**
     * Tests that pressing the right arrow key correctly sets the snake's direction to 'R' (right).
     */
    @Test
    void testKeyRight() {

        KeyEvent keyEvent = new KeyEvent(
            Mockito.mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_RIGHT,
            KeyEvent.CHAR_UNDEFINED,
            KeyEvent.KEY_LOCATION_STANDARD
        );

        snakeKeyAdapter.keyPressed(keyEvent);
        Mockito.verify(snakeGraphic).setDirection('R');
    }
}
