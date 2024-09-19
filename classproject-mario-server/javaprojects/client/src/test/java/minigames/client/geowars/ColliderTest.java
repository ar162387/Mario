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
public class ColliderTest {

    // Mock Enemy class for testing purposes
    static class TestGameObject extends GameObject {

        // Constructor for TestEnemy
        public TestGameObject(GeoWars engine, Vector2D position, double width, double height) {
            super(engine, position);
            Collider collider = new Collider(this, width, height, false);
            this.addCollider(collider);
        }
    }

    // Initialise Enemy
    private GeoWars engine;
    private Vector2D position;
    private GameObject primaryObject;
    private GameObject secondaryObject;
    private Collider primaryCollider;
    private Collider secondaryCollider;

    @BeforeEach
    void setup() {
        // Initialise mock engine
        engine = mock(GeoWars.class);

        // Set up objects
        primaryObject = new TestGameObject(engine, new Vector2D(100, 100), 18.0, 18.0);
        secondaryObject = new TestGameObject(engine, new Vector2D(500, 500), 10.0, 10.0);

        // Set up colliders
        ArrayList<Collider> primaryColliders = primaryObject.getColliders();
        assertTrue(primaryColliders.size() == 1, "There should only be one Collider instance.");
        primaryCollider = primaryColliders.get(0);

        ArrayList<Collider> secondaryColliders = secondaryObject.getColliders();
        assertTrue(primaryColliders.size() == 1, "There should only be one Collider instance.");
        secondaryCollider = secondaryColliders.get(0);
    }

    @Test
    void testIsColliding() {
        // Test false case
        assertFalse(primaryCollider.isColliding(secondaryCollider), "Colliders should not be colliding.");

        // Update position of primaryCollider
        primaryObject.setPosition(secondaryCollider.getPosition());

        // Re-test for true case
        assertTrue(primaryCollider.isColliding(secondaryCollider), "Colliders should be colliding now.");
    }

    @Test
    void testDimensions() {
        // Test false case
        assertFalse(primaryCollider.getWidth() == 20.0, "Collider width should be 18.0.");
        assertFalse(primaryCollider.getHeight() == 20.0, "Collider height should be 18.0.");

        // Test true case
        assertTrue(primaryCollider.getWidth() == 18.0, "Collider width should be 18.0.");
        assertTrue(primaryCollider.getHeight() == 18.0, "Collider height should be 18.0.");

        // Test set dimensions
        primaryCollider.setWidth(20.0);
        primaryCollider.setHeight(20.0);

        // Test false case
        assertFalse(primaryCollider.getWidth() == 18.0, "Collider width should be 20.0 now.");
        assertFalse(primaryCollider.getHeight() == 18.0, "Collider height should be 20.0 now.");

        // Test true case
        assertTrue(primaryCollider.getWidth() == 20.0, "Collider width should be 20.0 now.");
        assertTrue(primaryCollider.getHeight() == 20.0, "Collider height should be 20.0 now.");
    }

    @Test
    void testGetParent() {
        // Test parent type
        assertTrue(primaryCollider.getParent() instanceof GameObject,
                "Collider parent should be instance of GameObject.");
    }

    @Test
    void testTrigger() {
        // Test false case
        assertFalse(primaryCollider.isTrigger(), "isTrigger should be false.");

        // Update isTrigger
        primaryCollider.setTrigger(true);

        // Test true case
        assertTrue(primaryCollider.isTrigger(), "isTrigger should be true.");
    }

    @Test
    void testEnabled() {
        // Test true case
        assertTrue(primaryCollider.isEnabled(), "isEnabled should be true.");

        // Update isEnabled
        primaryCollider.setEnabled(false);

        // Test false case
        assertFalse(primaryCollider.isEnabled(), "isEnabled should be false.");
    }

    @Test
    void testDestroy() {
        // TO DO:
    }
}