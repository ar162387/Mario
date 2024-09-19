package minigames.client.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(5, 5, 3,1.5,1); // Edit: 10/9 Changed constructor - Li
    }

    @Test
    void testInitialization() {
        assertEquals(5 * GameConstants.TILE_SIZE, player.getX());
        assertEquals(5 * GameConstants.TILE_SIZE, player.getY());
        assertEquals(3, player.getLives());
        assertEquals(Direction.DOWN, player.getDirection());
    }

    @Test
    void testMove() {
        double initialX = player.getX();
        double initialY = player.getY();

        player.move(1, 0);
        assertEquals(initialX + 1.5, player.getX(), 0.001);
        assertEquals(initialY, player.getY(), 0.001);
//        assertEquals(Direction.RIGHT, player.getDirection()); FIXME @Dan something weird here im not sure how we going to test this

        player.move(0, 1);
        assertEquals(initialX + 1.5, player.getX(), 0.001);
        assertEquals(initialY + 1.5, player.getY(), 0.001);
        assertEquals(Direction.DOWN, player.getDirection());
    }

    @Test
    void testSetPosition() {
        player.setPosition(10, 10);
        assertEquals(10 * GameConstants.TILE_SIZE, player.getX());
        assertEquals(10 * GameConstants.TILE_SIZE, player.getY());
    }

    @Test
    void testLives() {
        assertEquals(3, player.getLives());
        player.decrementLives();
        assertEquals(2, player.getLives());
        player.setLives(5);
        assertEquals(5, player.getLives());
    }

    @Test
    void testHandleDeath() {
        boolean[] deathHandled = {false};
        player.handleDeath(() -> deathHandled[0] = true);
        assertTrue(deathHandled[0]);
    }

    @Test
    void testGetDirection() {
        assertEquals(Direction.DOWN, player.getDirection());
        player.move(1, 0);
        // assertEquals(Direction.RIGHT, player.getDirection()); Same again
    }

    @Test
    void testUpdate() {
        // This method is currently empty in the implementation
        long now = System.currentTimeMillis();
        player.update(now);
        // No assertions needed as the method doesn't do anything
    }
}