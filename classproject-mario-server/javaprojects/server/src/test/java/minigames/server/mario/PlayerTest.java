package minigames.server.mario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Mario", 50, 475, 32, 64, 4);
    }

    @Test
    void testInitialPlayerState() {
        assertEquals("Mario", player.getName());
        assertEquals(50, player.getX());
        assertEquals(475, player.getY());
        assertEquals(4, player.getHealth());
        assertTrue(player.isOnGround());
    }

    @Test
    void testMoveRight() {
        player.move("right");
        assertEquals(53, player.getX());  // assuming speed is 3
    }

    @Test
    void testMoveLeft() {
        player.move("left");
        assertEquals(47, player.getX());  // assuming speed is 3
    }

    @Test
    void testJump() {
        player.jump();
        assertTrue(player.isJumping());
        assertFalse(player.isOnGround());
    }

    @Test
    void testGravityEffect() {
        player.jump();
        player.jump(); // Continue jumping
        player.jump(); // Continue jumping

        // Simulate some time passing
        player.setY(500);
        assertTrue(player.getY() >= player.getGroundLevel());
        assertFalse(player.isJumping());
        assertTrue(player.isOnGround());
    }

    @Test
    void testHealthDecrease() {
        player.decreaseHealth();
        assertEquals(3, player.getHealth());
    }

    @Test
    void testHealthDecreaseWhileHit() {
        player.setHitFlag(true);
        player.decreaseHealth();
        assertEquals(4, player.getHealth()); // Health should not decrease
    }

    @Test
    void testHitFlagReset() throws InterruptedException {
        player.setHitFlag(true);
        Thread.sleep(2100); // Wait for 2 seconds
        assertFalse(player.isHit());
    }
}