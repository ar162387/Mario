package minigames.client.geowars.gameobjects.enemies;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.Player;
import minigames.client.geowars.gameobjects.Wall;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.util.Vector2D;
import minigames.client.geowars.util.DeltaTime;
import minigames.client.geowars.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;

/**
 * Enemy subclass for GeoWars.
 * Superclass that specific enemy types should extend.
 * Contains basic attributes and methods that all enemies should have.
 * Health (default = 1), Score (default = 15), etc.
 * Has a Collider, which should interact with walls, the player, and
 * projectiles.
 * Has logic to dictate movement.
 * 
 * PinkEnemy type follows player and splits into two smaller enemies on death.
 */
public class PinkEnemy extends Enemy {

    // PinkEnemy Attributes
    public static final double DEFAULT_WIDTH = 18;
    private static final double DEFAULT_HEIGHT = 18;
    private static final int DEFAULT_HEALTH = 1;
    private static final double DEFAULT_SPEED = 75.0;
    private static final double DEFAULT_SCORE = 15.0;

    // Constructor with default values
    public PinkEnemy(GeoWars engine, Vector2D position, double rotation) {
        super(engine, position, // Position
                rotation, // Rotation
                DEFAULT_WIDTH, DEFAULT_HEIGHT, // Dimensions for Collider
                DEFAULT_HEALTH, DEFAULT_SPEED, DEFAULT_SCORE);

        drawing = Drawing.getDrawing(Drawing.PINK_ENEMY, this);
    }
    /* ------------------------ Draw ------------------------ */

    /**
     * Draws PinkEnemy to screen.
     * 
     * @param Graphics g - Java Awt Graphics
     * @return boolean value
     * 
     */
    @Override
    public boolean draw(Graphics g) {
        if (g != null && drawing != null) {
            if (!movementStarted) {
                Color baseColor = Drawing.PINK_ENEMY_COLOR;
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

    /* ------------------- Enemy Movement ------------------- */

    /**
     * Controls enemy movement
     * 
     * PinkEnemy is a fast moving enemy that tracks player movement and splits into
     * multiple SmallPinkEnemy's after being destoryed.
     * 
     * @param none
     * @return none
     */
    @Override
    public void enemyMovement() {
        if (movementStarted) {
            GeoWars engine = this.getEngine();
            LevelManager levelManager = LevelManager.getInstance(engine, null);
            Player player = levelManager.getPlayer();

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

    /* ----------------- Collision Detection ----------------- */

    /**
     * Handles collision detection between PinkEnemy and Projectile
     * PinkEnemy splits into multiple SmallPinkEnemy's after being destroyed.
     * 
     * @param none
     * @return void
     */
    @Override
    public void enemyProjectileCollision() {
        // Spawn one or two instances of SmallPinkEnemy when PinkEnemy is destroyed
        int numInstances = (int) Math.floor(Math.random() * 2 + 1);
        Vector2D currentPosition = this.getPosition();
        for (int i = 0; i < numInstances; i++) {
            SmallPinkEnemy e = new SmallPinkEnemy(engine, currentPosition, this.getRotation());
            levelManager.addEnemy(e);
        }

        super.enemyProjectileCollision();
    }
}
