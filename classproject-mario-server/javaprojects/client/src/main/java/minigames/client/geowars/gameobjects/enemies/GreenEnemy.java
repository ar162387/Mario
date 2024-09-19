package minigames.client.geowars.gameobjects.enemies;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.scenes.Level;
import minigames.client.geowars.gameobjects.Wall;
import minigames.client.geowars.gameobjects.projectiles.Projectile;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.gameobjects.Player;
import minigames.client.geowars.util.Vector2D;
import minigames.client.geowars.util.DeltaTime;
import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.colliders.Collision;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.time.Instant;
import java.time.Duration;

/**
 * Enemy class for GeoWars.
 * Superclass that specific enemy types should extend.
 * Contains basic attributes and methods that all enemies should have.
 * Health (default = 1), Score (default = 15), etc.
 * Has a Collider, which should interact with walls, the player, and
 * projectiles.
 * Has logic to dictate movement.
 * 
 * 
 * GreenEnemy is a fast moving enemy that avoids incoming projectiles
 */
public class GreenEnemy extends Enemy {

    // GreenEnemy Attributes
    public static final int DEFAULT_WIDTH = 18;
    private static final int DEFAULT_HEIGHT = 18;
    private static final int DEFAULT_HEALTH = 1;
    private static final double DEFAULT_SPEED = 100.0;
    private static final double DEFAULT_SCORE = 15.0;

    private static final double AVOIDANCE_RANGE = 100.0;
    private boolean isDodging;
    private Vector2D dodgeDirection;
    private static Duration DODGE_DURATION = Duration.ofMillis(200);
    private static Duration DODGE_COOLDOWN = Duration.ofMillis(200);
    private static Instant dodgeStartTime;

    // Constructor with default values
    public GreenEnemy(GeoWars engine, Vector2D position, double rotation) {
        super(engine, position, // Position
                rotation, // Rotation
                DEFAULT_WIDTH, DEFAULT_HEIGHT, // Dimensions for Collider
                DEFAULT_HEALTH, DEFAULT_SPEED, DEFAULT_SCORE);
        this.isDodging = false;
        this.dodgeStartTime = Instant.now();

        // Add avoidance collider
        Collider avoidanceCollider = new Collider(this, DEFAULT_WIDTH + AVOIDANCE_RANGE,
                DEFAULT_HEIGHT + AVOIDANCE_RANGE, true);
        addCollider(avoidanceCollider);

        // Add Drawing
        drawing = Drawing.getDrawing(Drawing.GREEN_ENEMY, this);
    }

    /* ----------------------- Draw ------------------------ */

    /**
     * Draws LightBlueEnemy to screen.
     * 
     * @param Graphics g - Java Awt Graphics
     * @return boolean value
     * 
     */
    @Override
    public boolean draw(Graphics g) {
        if (g != null && drawing != null) {
            if (!movementStarted) {
                Color baseColor = Drawing.GREEN_ENEMY_COLOR;
                int red = baseColor.getRed();
                int green = baseColor.getGreen();
                int blue = baseColor.getBlue();
                int alpha = (int) (255 * (DeltaTime.levelTime() - creationTime) / movementDelay);
                alpha = Math.clamp(alpha, 0, 255);
                Color color = new Color(red, green, blue, alpha);
                drawing.draw(g, 1, 0, color);
            } else {
                drawing.draw(g, 1, 0, null);
            }
        }
        return true;
    }

    /* -------------------- Movement ---------------------- */

    /**
     * Controls enemy movement
     * 
     * LightBlueEnemy is a slow moving enemy that tracks player movement.
     * 
     * @param none
     * @return void
     * 
     */
    @Override
    public void enemyMovement() {
        if (movementStarted) {
            // Get reference to Player
            GeoWars engine = this.getEngine();
            LevelManager levelManager = LevelManager.getInstance(engine, null);
            Player player = levelManager.getPlayer();

            // Add in sideways velocity if dodging
            if (isDodging && !dodgeComplete()) {
                // Determine direction to dodge in
                Vector2D dodgeVelocity = this.getDodgeDirection();

                // Check if new position will send enemy out of bounds
                Vector2D positionCopy = new Vector2D(this.position);
                positionCopy.add(dodgeVelocity);
                double[] pos = new double[2];
                positionCopy.get(pos);

                // Only update position if new position is in bounds
                if (pos[0] >= 62 && pos[0] <= GeoWars.SCREEN_WIDTH - 62 && pos[1] >= 62
                        && pos[1] <= GeoWars.SCREEN_HEIGHT - 62) {
                    this.velocity.set(dodgeVelocity);
                    this.position.add(this.velocity);
                }
            }

            // Otherwise continue with regular player tracking
            Vector2D playerPosition = player.getPosition(); // Get Player position
            Vector2D enemyPosition = this.getPosition(); // Get Enemy position

            // Update direction to point towards Player position
            direction.set(playerPosition);
            direction.sub(enemyPosition);
            direction.normalize();

            // Update velocity based on new direction, then scale by speed and DeltaTime.
            velocity.set(direction);
            velocity.scale(DEFAULT_SPEED * DeltaTime.delta());

            this.position.add(velocity); // Update position
        }
    }

    /* ---------------- Collision Detection ---------------- */

    /**
     * Collision handling for GreenEnemy
     * 
     * @param Collision c - current instance of Collision
     * @return void
     */
    @Override
    public void onCollision(Collision c) {
        super.onCollision(c);
        Collider otherCollider = c.getOtherCollider();
        Collider thisCollider = c.getThisCollider();

        if (thisCollider.isTrigger()) {
            if (otherCollider.getParent() instanceof Projectile) {
                this.setDodgeDirection(c);
                this.dodgeStartTime = Instant.now();
                this.isDodging = true;
            }
        }
    }

    /* ---------------- Dodging Behaviour ---------------- */

    /**
     * Getter method for dodgeDirection
     * 
     * @param none
     * @return Vector2D containing new direction for enemy to evade incoming
     *         projectile
     * 
     */
    public Vector2D getDodgeDirection() {
        return this.dodgeDirection;
    }

    /**
     * Setter method for dodgeDirection
     * 
     * @param none
     * @return void
     * 
     */
    public void setDodgeDirection(Vector2D direction) {
        this.dodgeDirection = direction;
    }

    /**
     * Dodging phase is only active for 200ms after detecting incoming projectile.
     * This method
     * checks if 200ms has passed before resetting isDodging to false so enemy can
     * continue
     * with normal player tracking.
     * 
     * @param none
     * @return boolean complete - indicates whether enough time has passed.
     * 
     */
    public boolean dodgeComplete() {
        boolean complete = Duration.between(dodgeStartTime, Instant.now()).compareTo(DODGE_DURATION) >= 0;
        if (complete) {
            this.isDodging = false;
        }
        return complete;
    }

    /**
     * Determines the direction enemy should move to evade incoming projectile.
     * Method
     * calulates the direction between the projectile and the enemy, then moves in
     * opposite
     * direction.
     * 
     * @param Collision c - current Collision instance between Enemy and Projectile
     * @return void
     * 
     */
    public void setDodgeDirection(Collision c) {
        // Determine direction between Enemy and Projectile
        Vector2D enemyPosition = this.getPosition();
        Vector2D projectilePosition = c.getOtherCollider().getPosition();
        Vector2D directionToProjectile = new Vector2D(projectilePosition);

        // Scale direction to face away from incoming projectile
        directionToProjectile.sub(enemyPosition);
        directionToProjectile.scale(-1);
        directionToProjectile.normalize();
        directionToProjectile.scale(DEFAULT_SPEED * DeltaTime.delta());

        this.setDodgeDirection(directionToProjectile); // Update direction
    }
}
