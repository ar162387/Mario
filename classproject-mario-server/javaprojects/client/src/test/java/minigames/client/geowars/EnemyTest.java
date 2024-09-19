package minigames.client.geowars;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.gameobjects.enemies.*;
import minigames.client.geowars.gameobjects.projectiles.Projectile;
import minigames.client.geowars.gameobjects.Wall;
import minigames.client.geowars.util.Vector2D;
import minigames.client.geowars.util.DeltaTime;

import java.awt.Graphics;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;

/**
 * EnemyTest class
 */
public class EnemyTest {

    // Mock Enemy class for testing purposes
    static class TestEnemy extends Enemy {

        // Constructor for TestEnemy
        public TestEnemy(GeoWars engine, Vector2D position, double rotation, int width, int height, int health,
                double speed, double score) {
            super(engine, position, rotation, width, height, health, speed, score);
        }

        @Override
        public void enemyMovement() {
        }
    }

    // Initialise Enemy
    private GeoWars engine;
    private LevelManager levelManager;
    private Vector2D position;
    private Enemy enemy;

    @BeforeEach
    void setup() {
        engine = mock(GeoWars.class);
        levelManager = mock(LevelManager.class);
        position = new Vector2D(100, 100);
        enemy = new TestEnemy(engine, position, 0.0, 18, 18, 5, 50.0, 15.0);
    }

    @Test
    void testHealth() {
        // Test getHealth()
        assertFalse(enemy.getHealth() == 10, "Initial health should be 5.");

        // Test getHealth() again.
        assertTrue(enemy.getHealth() == 5, "Initial health should be 5.");

        // Test setHealth() by decrementing health by 1.
        enemy.setHealth(1);
        assertTrue(enemy.getHealth() == 4, "Enemy health should now be 4.");
    }

    @Test
    void testScore() {
        // Test getScore()
        assertFalse(enemy.getScore() == 30.0, "Score should be 15.0");
        assertTrue(enemy.getScore() == 15.0, "Score should be 15.0");
    }

    @Test
    void testEnemyMovement() {
        // Get current position
        Vector2D currentPosition = enemy.getPosition();

        // Update position
        enemy.enemyMovement();

        // Save new position
        Vector2D newPosition = enemy.getPosition();

        // Test that position has been updated
        assertNotEquals(currentPosition == newPosition, "Enemy position should have been updated in enemyMovement()");
    }

    @Test
    void testEnemyWallCollision() {
        // Get Enemy Collider
        ArrayList<Collider> enemyColliders = enemy.getColliders();
        assertTrue(enemyColliders.size() == 1, "TestEnemy should only have one Collider.");
        Collider enemyCollider = enemyColliders.get(0);

        // Create new Wall object and get it's Collider
        Wall wall = new Wall(engine, new Vector2D(50, 50), 0.0, 10, 10);
        ArrayList<Collider> wallColliders = wall.getColliders();
        assertTrue(wallColliders.size() == 1, "Wall should only have one Collider");
        Collider wallCollider = wallColliders.get(0);

        // Test if enemy and wall are colliding
        assertFalse(enemyCollider.isColliding(wallCollider), "Enemy and Wall should not be colliding.");

        // Update enemy position so it is colliding with the wall
        enemy.setPosition(new Vector2D(50, 50));

        // Re-test if enemy and wall are colliding
        assertTrue(enemyCollider.isColliding(wallCollider), "Enemy should now be colliding with Wall");
    }

    @Test
    void testEnemyProjectileCollision() {
        // Get Enemy Collider
        ArrayList<Collider> enemyColliders = enemy.getColliders();
        assertTrue(enemyColliders.size() == 1, "TestEnemy should only have one Collider.");
        Collider enemyCollider = enemyColliders.get(0);

        // Create new Projectile object and get it's Collider
        Projectile projectile = new Projectile(engine, new Vector2D(10.0, 10.0), new Vector2D(5.0, 5.0));
        ArrayList<Collider> projectileColliders = projectile.getColliders();
        assertTrue(projectileColliders.size() == 1, "Projectile should only have one Collider.");
        Collider projectileCollider = projectileColliders.get(0);

        // Test if Enemy and Projectile are colliding
        assertFalse(enemyCollider.isColliding(projectileCollider), "Enemy and Projectile should not be colliding");

        // Update enemy position so it is colliding with Projectile
        enemy.setPosition(new Vector2D(10, 10));

        // Re-test if Enemy and Projectile are colliding
        assertTrue(enemyCollider.isColliding(projectileCollider), "Enemy should now be colliding with Projectile");
    }
}