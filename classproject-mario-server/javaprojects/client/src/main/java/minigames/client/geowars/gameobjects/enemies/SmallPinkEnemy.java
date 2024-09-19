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
 * SmallPinkEnemy moves in spiral pattern.
 */
public class SmallPinkEnemy extends Enemy {

    // SmallPinkEnemy Attributes
    public static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;
    private static final int DEFAULT_HEALTH = 1;
    private static final double DEFAULT_SPEED = 50.0;
    private static final double DEFAULT_SCORE = 15.0;

    // Other Attributes
    private double angle;
    private double radius;
    private double angleSpeed;
    private double radiusSpeed;

    // Constructor with default values
    public SmallPinkEnemy(GeoWars engine, Vector2D position, double rotation) {
        super(engine, position, // Position
                rotation, // Rotation
                DEFAULT_WIDTH, DEFAULT_HEIGHT, // Dimensions for Collider
                DEFAULT_HEALTH, DEFAULT_SPEED, DEFAULT_SCORE);
        this.angle = Math.random() * 2 * Math.PI;
        this.radius = 0.1;
        this.angleSpeed = 0.05;
        this.radiusSpeed = 0.05;

        movementDelay = 0.0;

        drawing = Drawing.getDrawing(Drawing.SMALL_PINK_ENEMY, this);
    }
    /* --------------------- Draw ---------------------- */

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

    /* ---------------- Enemy Movement ----------------- */

    /**
     * Controls enemy movement
     * 
     * SmallPinkEnemy moves in a spiral pattern.
     * 
     * @param none
     * @return none
     */
    @Override
    public void enemyMovement() {
        // Increment angle
        this.angle += angleSpeed;
        this.radius += radiusSpeed;

        double x = Math.cos(angle) * radius;
        double y = Math.sin(angle) * radius;

        // Increment direction
        this.direction.set(x, y);
        this.direction.normalize();
        this.velocity.set(direction);
        this.velocity.scale(DEFAULT_SPEED * DeltaTime.delta());
        this.position.add(velocity);
    }

    /* --------------- Collision Detection ---------------- */

    /**
     * Controls Enemy-Wall collsion for SmallPinkEnemy
     * SmallPinkEnemy should reflect off the wall on collision.
     * 
     * @param GameObject thisObject - GameObject from super. References current
     *                   instance of Enemy object
     * @param GameObject otherObject - GameObject from super. References current
     *                   instance of Wall object
     * @return none
     */
    @Override
    public void enemyWallCollision(GameObject thisObject, GameObject otherObject) {
        // Get position of Wall
        double wallX = otherObject.getCurrentX();
        double wallY = otherObject.getCurrentY();

        // Get position of Enemy
        double[] pos = new double[2];
        super.position.get(pos);

        // Get Enemy direction
        double[] dir = new double[2];
        super.direction.get(dir);

        // Calculate difference in positions
        double dx = Math.abs(wallX - pos[0]);
        double dy = Math.abs(wallY - pos[1]);

        // Close to vertical wall
        if (dx < dy) {
            // Left-hand side. Move x-pos a few pixels to the right
            if (wallX < pos[0]) {
                super.position.set(new Vector2D(pos[0] + 1, pos[1]));
            }
            // Right-hand side. Move x-pos a few pixels to the left
            else {
                super.position.set(new Vector2D(pos[0] - 1, pos[1]));
            }
            // Reflect vertically
            super.direction.set(new Vector2D(-dir[0], dir[1]));
        } // Close to horizontal wall
        else {
            // Top side. Move y-pos a few pixels down
            if (wallY < pos[1]) {
                super.position.set(new Vector2D(pos[0], pos[1] + 1));
            }
            // Bottom side. Move y-pos a few pixels up
            else {
                super.position.set(new Vector2D(pos[0], pos[1] - 1));
            }
            // Reflect horizontally
            super.direction.set(new Vector2D(dir[0], -dir[1]));
        }
        super.direction.normalize();
        super.velocity.set(direction);
        super.velocity.scale(DEFAULT_SPEED * DeltaTime.delta());
    }
}
