package minigames.client.geowars;

import minigames.client.geowars.*;
import minigames.client.geowars.gameobjects.enemies.*;
import minigames.client.geowars.scenes.*;
import minigames.client.geowars.util.Vector2D;

import minigames.client.geowars.gameobjects.Player;
import minigames.client.geowars.util.DeltaTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

/**
 * Test class for LevelManager.
 */
public class LevelManagerTest {

    private LevelManager levelManager;
    private GeoWars engine;
    private Player player;
    private Level level;

    /**
     * Sets up the test environment by mocking the necessary dependencies and
     * getting the LevelManager instance.
     */
    @BeforeEach
    void setup() {
        // Mock the engine and other dependencies
        engine = mock(GeoWars.class);
        player = mock(Player.class);
        level = mock(Level.class);

        // Use getInstance instead of direct instantiation
        levelManager = LevelManager.getInstance(engine, level);

        // Set up mock behaviour for player and level
        when(level.getPlayerStartPosition()).thenReturn(new Vector2D(100, 100));
        when(player.getLives()).thenReturn(3);
    }

    /**
     * Tests the pause functionality in LevelManager.
     */
    @Test
    @Disabled
    void testPauseLevel() {
        // Pause the level
        levelManager.setPause(true);

        // Verify the level's pause state is set to true
        verify(level).setPause(true);
    }

    /**
     * Tests the game over condition in LevelManager.
     */
    @Test
    @Disabled
    void testGameOver() {
        // Simulate the player running out of lives
        when(player.getLives()).thenReturn(0);

        // Simulate player death
        levelManager.playerDied();

        // Verify that the gameOver flag is set
        assertTrue(levelManager.getPlayer().getLives() == 0);
    }

    /**
     * Tests the cleanup process in LevelManager.
     */
    @Test
    @Disabled
    void testCleanup() {
        // Mock enemies and add them to the level
        Enemy enemy = mock(Enemy.class);
        levelManager.addEnemy(enemy);

        // Cleanup the level
        levelManager.cleanup();

        // Verify that the player and enemies are destroyed
        verify(player).destroy();
        verify(enemy).destroy();

        // Verify the level is null after cleanup
        assertNull(levelManager.getCurrentLevel());
    }
}
