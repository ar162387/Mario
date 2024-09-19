package minigames.server.mario;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * The Enemy class represents an enemy in the game.
 * Handles the movement, rendering, and collision state of the enemy.
 */
public class Enemy {
    private int x, y, width, height, speed;
    private boolean active;
    private boolean collisionDetected;

    private boolean defeated; // New flag to indicate if the enemy is defeated

    /**
     * Constructs a new Enemy object with specified position, size, and speed.
     *
     * @param x       the x-coordinate of the enemy's starting position
     * @param y       the y-coordinate of the enemy's starting position
     * @param width   the width of the enemy
     * @param height  the height of the enemy
     * @param speed   the speed at which the enemy moves to the left
     */
    public Enemy(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = 415; // Fixed vertical position
        this.width = width;
        this.height = height;
        this.active = true;
        this.collisionDetected = false;
        this.speed = speed; // Speed at which the enemy moves left
        this.defeated = false; // Initialize defeated state

    }

    /**
     * Updates the enemy's position, moving it left across the screen.
     * If the enemy moves off the left side of the screen, it respawns.
     */
    public boolean getDefeated()
    {
        return defeated;
    }

    public int getHeight()
    {
        return this.height;
    }

    /**
     * Moves the enemy based on its state (defeated or active).
     */
    public void move() {
        if (active && !defeated) {
            x -= speed; // Always move left

            // Respawn if the enemy moves off the left side of the screen
            if (x + width < 0) {
                respawn();
            }
        } else if (defeated) {
            moveDown(); // Call moveDown if the enemy is defeated
        }
    }
    public void moveDown() {
        if (y < 850) { // Assuming 600 is below the visible screen area
            y += 5; // Adjust this value for the speed of falling down
        } else {
            defeat(); // Deactivate the enemy once it's out of the screen
            System.out.println("Enemy has fallen below the ground and is deactivated.");
        }
    }

    /**
     * Makes the enemy fall below the ground level when defeated.
     */
    public void defeat() {
        defeated = true; // Mark the enemy as defeated
        collisionDetected = true; // Set collision detected to true
        active = false; // Optionally deactivate further leftward movement
    }

    public void respawn() {
        // Reset position off-screen to the right and randomize speed or position if needed
        x = 1200; // maybe randomize within a range, like new Random().nextInt(200) + 1000
        active = true;
        collisionDetected = false;  // Reset the collision flag
        // System.out.println("Enemy respawned to the right of the screen.");
    }


    /**
     * Deactivates the enemy, stopping its movement and rendering.
     */
    public void deactivate() {
        active = false;
        defeated = false;
    }

    /**
     * Checks if the enemy is currently active.
     *
     * @return true if the enemy is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the bounding rectangle of the enemy for collision detection.
     *
     * @return a Rectangle representing the enemy's bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Gets the y-coordinate of the enemy.
     *
     * @return the y-coordinate of the enemy
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the x-coordinate of the enemy.
     *
     * @return the x-coordinate of the enemy
     */
    public int getX() {
        return x;
    }

    /**
     * Checks if a collision with the player has been detected.
     *
     * @return true if a collision has been detected, false otherwise
     */
    public boolean isCollisionDetected() {
        return collisionDetected;
    }

    /**
     * Sets the collision detection state of the enemy.
     *
     * @param collisionDetected true if a collision is detected, false otherwise
     */
    public void setCollisionDetected(boolean collisionDetected) {
        this.collisionDetected = collisionDetected;
    }
}
