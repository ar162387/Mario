package minigames.client.geowars.gameobjects.enemies;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.gameobjects.Wall;
import minigames.client.geowars.gameobjects.projectiles.Projectile;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.gameobjects.Player;
import minigames.client.geowars.util.Vector2D;
import minigames.client.geowars.util.DeltaTime;
import minigames.client.geowars.*;
import java.awt.Graphics;
import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;

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
 * LightBlueEnemy type follows player movement slowly.
 */
public class LightBlueEnemy extends Enemy {

    // LightBlueEnemy Attributes
    public static final int DEFAULT_WIDTH = 18;
    private static final int DEFAULT_HEIGHT = 18;
    private static final int DEFAULT_HEALTH = 1;
    private static final double DEFAULT_SPEED = 50.0;
    private static final double DEFAULT_SCORE = 15.0;

    // Constructor with default values
    public LightBlueEnemy(GeoWars engine, Vector2D position, double rotation) {
        super(engine, position, // Position
                rotation, // Rotation
                DEFAULT_WIDTH, DEFAULT_HEIGHT, // Dimensions for Collider
                DEFAULT_HEALTH, DEFAULT_SPEED, DEFAULT_SCORE);

        drawing = Drawing.getDrawing(Drawing.LIGHT_BLUE_ENEMY, this);
    }
    /* ------------------------ Draw ------------------------ */

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
                Color baseColor = Drawing.LIGHT_BLUE_ENEMY_COLOR;
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

    /* -------------------- Enemy Movement -------------------- */

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

}
