package minigames.client.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import minigames.client.snake.ui.SnakeGame;
import minigames.client.snake.ui.SnakeGraphic;
import minigames.client.snake.ui.Apple;

/**
* This class contains unit tests for the Apple class, which is part of the Snake game.
* The tests cover the initialisation of the Apple object, the application of its effects on the Snake,
* and checking that the Apple object is associated with an image.
*/

public class AppleTest {

   private Apple apple;
   private SnakeGame game;
   private SnakeGraphic snake;

   /**
    * Initializes the Apple instance before each test is run.
    */
   @BeforeEach
   public void setUp() {

       // Initialize the Apple instance before each test
       apple = new Apple(23, 0);
       game = mock(SnakeGame.class);
       snake = mock(SnakeGraphic.class);
       
   }

   /**
    * Tests that the Apple object is initialised with the correct coordinates.
    * This test checks if the X and Y coordinates of the Apple are set as expected.
    */
   @Test
   public void appleInitialisationTest() {

       // Test the apple initialises correctly
       assertEquals(23, apple.getX());
       assertEquals(0, apple.getY());
   }

   /**
    * Tests that the effect of the Apple on the Snake is applied correctly.
    * Specifically, it checks that the length of the Snake increases by one after consuming the Apple.
    */
   @Test
   public void appleApplyEffectTest() {

        // apple should not yet be eaten
        assertFalse(apple.isEaten());

        apple.applyEffect(snake, game);

        verify(snake, times(1)).grow();
        assertTrue(apple.isEaten());
   }

   /**
    * Tests that the Apple object has an image associated with it.
    * This test ensures that the Apple has a non-null image, which is required for rendering the Apple in the game.
    */
//    @Test
//    public void appleImageTest() {

//        // Test that the apple has image associated
//        assertNotNull(apple.getImage());
//    }
}
