package minigames.server.mario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnemyTest {

    private Enemy enemy;

    @BeforeEach
    void setUp() {
        enemy = new Enemy(100, 100, 50, 50, 10);
    }

    @Test
    void testInitialEnemyState() {
        assertEquals(100, enemy.getX());
        assertEquals(100, enemy.getY());
        assertTrue(enemy.isActive());
    }

    @Test
    void testMoveEnemy() {
        enemy.move();
        // Depending on implementation, assert the expected new position
    }

    @Test
    void testDeactivateEnemy() {
        enemy.deactivate();
        assertFalse(enemy.isActive());
    }
}