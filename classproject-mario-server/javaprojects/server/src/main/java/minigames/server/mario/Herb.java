package minigames.server.mario;

import java.awt.Rectangle;

/**
 * The Herb class represents a health-increasing item in the game.
 * Handles the movement, collision, and state of the herb.
 */
public class Herb {
    private int x, y, width, height;
    private boolean active;
    private boolean collected; // Flag to check if the herb has been taken by the player

    /**
     * Constructs a new Herb object with specified position, size.
     *
     * @param x      the x-coordinate of the herb's starting position
     * @param y      the y-coordinate of the herb's starting position
     * @param width  the width of the herb
     * @param height the height of the herb
     */
    public Herb(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
        this.collected = false; // Initialize as not collected
    }

    /**
     * Updates the herb's position, making it fall down until it reaches the ground.
     */
    public void fall() {
        if (active && !collected) {
            if (y < 480) { // Ground coordinate
                y += 2; // Adjust this value for the speed of falling down
            }
        }
    }

    /**
     * Deactivates the herb once it has been collected.
     */
    public void collect() {
        this.active = false;
        this.collected = true;
        System.out.println("Herb has been collected by the player.");
    }

    /**
     * Returns the bounding rectangle of the herb for collision detection.
     *
     * @return a Rectangle representing the herb's bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Checks if the herb is currently active.
     *
     * @return true if the herb is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the y-coordinate of the herb.
     *
     * @return the y-coordinate of the herb
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the x-coordinate of the herb.
     *
     * @return the x-coordinate of the herb
     */
    public int getX() {
        return x;
    }

    /**
     * Checks if the herb has been collected by the player.
     *
     * @return true if the herb is collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
}
