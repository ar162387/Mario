package minigames.client.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        enemy = new Enemy(5, 5);
    }

    @Test
    void testInitialization() {
        assertEquals(5 * GameConstants.TILE_SIZE, enemy.getX());
        assertEquals(5 * GameConstants.TILE_SIZE, enemy.getY());
        assertEquals(1, enemy.getLives());
        assertEquals(Direction.DOWN, enemy.getDirection());
    }

    @Test
    void testMove() {
        double initialX = enemy.getX();
        double initialY = enemy.getY();

        enemy.move(1, 0);
        enemy.updatePosition();
        assertEquals(initialX + 1, enemy.getX(), 0.001);
        assertEquals(initialY, enemy.getY(), 0.001);
        assertEquals(Direction.RIGHT, enemy.getDirection());

        enemy.move(0, 1);
        assertEquals(initialX + 1, enemy.getX(), 0.001);
        assertEquals(initialY + 1, enemy.getY(), 0.001);
        assertEquals(Direction.DOWN, enemy.getDirection());
    }

    @Test
    void testSetPosition() {
        enemy.setPosition(10, 10);
        assertEquals(10, enemy.getX());
        assertEquals(10, enemy.getY());
    }

    @Test
    void testLives() {
        assertEquals(1, enemy.getLives());
        enemy.decrementLives();
        assertEquals(0, enemy.getLives());
        enemy.setLives(3);
        assertEquals(3, enemy.getLives());
    }

    @Test
    void testHandleDeath() {
        boolean[] deathHandled = {false};
        enemy.handleDeath(() -> deathHandled[0] = true);
        assertTrue(deathHandled[0]);
    }

    @Test
    void testGetDirection() {
        assertEquals(Direction.DOWN, enemy.getDirection());
        enemy.move(1, 0);
        assertEquals(Direction.RIGHT, enemy.getDirection());
    }

    @Test
    void testUpdate() {
        // This method is currently empty in the implementation
        long now = System.currentTimeMillis();
        enemy.update(now);
        // No assertions needed as the method doesn't do anything
    }

    @Test
    void testConstructorWithLives() {
        Enemy enemyWithLives = new Enemy(3, 3, 2);
        assertEquals(3 * GameConstants.TILE_SIZE, enemyWithLives.getX());
        assertEquals(3 * GameConstants.TILE_SIZE, enemyWithLives.getY());
        assertEquals(2, enemyWithLives.getLives());
    }

    @Test
    void testConstructorWithLivesAndSpeed() {
        Enemy enemyWithLivesAndSpeed = new Enemy(3, 3, 2, 2);
        assertEquals(3 * GameConstants.TILE_SIZE, enemyWithLivesAndSpeed.getX());
        assertEquals(3 * GameConstants.TILE_SIZE, enemyWithLivesAndSpeed.getY());
        assertEquals(2, enemyWithLivesAndSpeed.getLives());
        // We can't test the speed directly as it's private, but we can test its effect
        enemyWithLivesAndSpeed.move(1, 0);
        assertEquals(3 * GameConstants.TILE_SIZE + 2, enemyWithLivesAndSpeed.getX(), 0.001);
    }
}