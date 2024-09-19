package minigames.client.deepfried;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import minigames.client.deepfried.input.Input;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {
    /**
    * Run with ./gradlew :client:test --tests "minigames.client.deepfried.InputTest"
    * Output is in \classproject\javaprojects\client\build\reports\tests\test\index.html
    */

    private Input input;

    @BeforeEach
    void setUp() {
        // initialise input to be tested
        input = new Input();
        // look to setup a hashMap to test input into the Map
    }


    @Test
    void testKeyPressed() {
        Component mockGameWindow = Mockito.mock(Component.class);
                
        // Simulate pressing the 'W' key
        input.keyPressed(new KeyEvent(mockGameWindow, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        input.update();
        assertTrue(input.isKeyPressed(KeyEvent.VK_W), "W key pressed");

        // Simulate pressing the 'A' key
        input.keyPressed(new KeyEvent(mockGameWindow, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A'));
        input.update();
        assertTrue(input.isKeyPressed(KeyEvent.VK_A), "A key pressed");
    }

    @Test
    void testKeyReleased() {
        Component mockGameWindow = Mockito.mock(Component.class);

        // Simulate pressing and then releasing
        input.keyPressed(new KeyEvent(mockGameWindow, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        input.update();
        // ensure button is pressed before its released
        assertTrue(input.isKeyPressed(KeyEvent.VK_W), "W key pressed");
        // release button
        input.keyReleased(new KeyEvent(mockGameWindow, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        input.update();
        assertFalse(input.isKeyPressed(KeyEvent.VK_W), "Key 'W' released.");
    }

    @Test
    void testClearInputStates() {
        Component mockComponent = Mockito.mock(Component.class);

        // Simulate pressing some keys
        input.keyPressed(new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W'));
        assertTrue(input.isKeyPressed(KeyEvent.VK_W), "W key pressed");
        input.keyPressed(new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A'));
        assertTrue(input.isKeyPressed(KeyEvent.VK_A), "A key pressed");

        // Call Input clear method
        input.clear();

        // Check if all keys are released
        assertFalse(input.isKeyPressed(KeyEvent.VK_W), "Key 'W' cleared.");
        assertFalse(input.isKeyPressed(KeyEvent.VK_A), "Key 'A' cleared.");
    }


}
