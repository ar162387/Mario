package minigames.client.mario;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * The Herb class represents a health-increasing item in the game.
 * It is used for rendering and tracking the state of the herb.
 */
public class Herb {
    private int x, y, width, height;
    private boolean active;
    private BufferedImage herbImage; // Image for the herb

    /**
     * Constructs a new Herb object with specified position and size.
     *
     * @param x      the x-coordinate of the herb
     * @param y      the y-coordinate of the herb
     * @param width  the width of the herb
     * @param height the height of the herb
     * @param active whether the herb is active
     */
    public Herb(int x, int y, int width, int height, boolean active) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = active;
        loadHerbImage();
    }

    /**
     * Loads the herb image sprite.
     */
    private void loadHerbImage() {
        try {
            herbImage = ImageIO.read(getClass().getResourceAsStream("/Images/mario/herb.png"));
        } catch (IOException e) {
            System.err.println("Error loading herb image: " + e.getMessage());
        }
    }

    /**
     * Draws the herb on the screen.
     *
     * @param g the Graphics object used for drawing
     */
    public void draw(Graphics g) {
        if (!active) {
            return; // Don't draw if the herb is not active
        }

        // Draw the herb image or a red rectangle if the image is not loaded
        if (herbImage != null) {
            g.drawImage(herbImage, x, y, width, height, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height); // Fallback if image fails to load
        }
    }

    /**
     * Sets whether the herb is active.
     *
     * @param active true if the herb is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the herb is active.
     *
     * @return true if the herb is active, false otherwise
     */
    public boolean isActive() {
        return active;
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
     * Gets the y-coordinate of the herb.
     *
     * @return the y-coordinate of the herb
     */
    public int getY() {
        return y;
    }

    /**
     * Makes the herb fall down towards the ground.
     */
    public void fall() {
        if (y < 425) { // Ground coordinate
            y += 2; // Adjust this value for the speed of falling down
        }
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
