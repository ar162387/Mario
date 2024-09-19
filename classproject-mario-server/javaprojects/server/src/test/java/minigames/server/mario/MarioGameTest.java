package minigames.server.mario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarioGameTest {

    private MarioGame game;
    private Player player;

    @BeforeEach
    void setUp() {
        game = new MarioGame("TestGame");
        player = new Player("Mario", 50, 475, 32, 64, 4);
        game.joinGame("Mario");
    }

    @Test
    void testJoinGame() {
        String[] playerNames = game.getPlayerNames();
        assertTrue(playerNames.length > 0);
        assertEquals("Mario", playerNames[0]);
    }

    @Test
    void testEnemySpawn() {
        // Ensure at least one enemy is spawned
        game.updateGame();
        // Check for enemies in the game
        // Depending on the implementation, assert expected number of enemies
    }

    @Test
    void testPlayerMovement() {
        game.processMovementCommand(player, "right");
        // Verify player's new position
    }
}