package minigames.client.mario;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * The Enemy class represents a lightweight version of the server-side Enemy.
 * It is used for rendering and basic state tracking.
 */
public class Enemy {
    private int x, y, width, height;
    private boolean active;
    private BufferedImage goombaL, goombaR, goombaD;  // Images for the animation, including defeated sprite
    private boolean showingLeft;  // Flag to track which image is currently being shown
    private long lastSwitchTime;  // Time of the last image switch
    private int animationInterval = 500; // Switch images every 500 milliseconds

    /**
     * Constructs a new Enemy object with specified position and size.
     *
     * @param x      the x-coordinate of the enemy
     * @param y      the y-coordinate of the enemy
     * @param width  the width of the enemy
     * @param height the height of the enemy
     * @param active whether the enemy is active
     */
    public Enemy(int x, int y, int width, int height, boolean active) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = active;
        this.showingLeft = true; // Start by showing the left image
        this.lastSwitchTime = System.currentTimeMillis(); // Initialize the last switch time
        loadEnemyImages();
    }

    /**
     * Loads the enemy image sprites.
     */
    private void loadEnemyImages() {
        try {
            goombaL = ImageIO.read(getClass().getResourceAsStream("/images/mario/goomba-l.png"));
            goombaR = ImageIO.read(getClass().getResourceAsStream("/images/mario/goomba-r.png"));
            goombaD = ImageIO.read(getClass().getResourceAsStream("/images/mario/goomba-d.png")); // Load defeated image
        } catch (IOException e) {
            System.err.println("Error loading goomba images: " + e.getMessage());
        }
    }

    /**
     * Updates the position of the enemy.
     *
     * @param x the new x-coordinate of the enemy
     * @param y the new y-coordinate of the enemy
     */
    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Draws the enemy on the screen and animates between two images.
     *
     * @param g the Graphics object used for drawing
     */
    public void draw(Graphics g) {
        BufferedImage currentImage;

        if (!active) {
            // If the enemy is defeated, show the defeated sprite
            currentImage = goombaD;
        } else {
            // Otherwise, animate between the left and right sprites
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSwitchTime > animationInterval) {
                showingLeft = !showingLeft;  // Switch between left and right images
                lastSwitchTime = currentTime;  // Reset the last switch time
            }
            currentImage = showingLeft ? goombaL : goombaR;
        }

        // Draw the appropriate image
        if (currentImage != null) {
     //       g.drawImage(currentImage, x, y, width, height, null);
            g.drawImage(currentImage, x, y + 55, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);  // Draw a red rectangle as a fallback
        }
    }

    /**
     * Sets whether the enemy is active.
     *
     * @param active true if the enemy is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the enemy is active.
     *
     * @return true if the enemy is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
