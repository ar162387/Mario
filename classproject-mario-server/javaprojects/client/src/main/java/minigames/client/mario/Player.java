package minigames.client.mario;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * The Player class represents the player character in the Mario UI.
 * It handles rendering and updating the player's position
 * based on data received from the server.
 */
public class Player {

    private int x;      // x-coordinate of the player
    private int y;      // y-coordinate of the player
    private int width = 32;  // width of the player
    private int height = 64; // height of the player
    private String direction;  // direction the player is facing (left or right)
    private boolean onGround;  // whether the player is on the ground
    private BufferedImage marioIdleL, marioIdleR;  // Sprites for Mario's directions

    private BufferedImage[] marioWalkLeft, marioWalkRight;  // Arrays for walking animation sprites
    private int animationIndex = 0;  // Current animation frame
    private int animationDelay = 5;  // Delay for animation speed
    private int animationCounter = 0;  // Counter for controlling animation speed

    /**
     * Constructs a new Player for rendering with an initial position and size.
     *
     * @param x      The x-coordinate of the player's initial position.
     * @param y      The y-coordinate of the player's initial position.
     * @param width  The width of the player.
     * @param height The height of the player.
     */
    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = "idleR";  // Default direction
        this.onGround = true;  // By default, player starts on the ground
        loadPlayerImages();
    }

    /**
     * Loads the Player image sprites for different directions.
     */
    private void loadPlayerImages() {
        try {
            marioIdleL = ImageIO.read(getClass().getResourceAsStream("/images/mario/marioIdleL.png"));  // Mario facing left
            marioIdleR = ImageIO.read(getClass().getResourceAsStream("/images/mario/marioIdleR.png"));  // Mario facing right

            // Load walking sprites for left direction
            marioWalkLeft = new BufferedImage[] {
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_left_1.png")),
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_left_2.png")),
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_left_3.png"))
            };

            // Load walking sprites for right direction
            marioWalkRight = new BufferedImage[] {
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_right_1.png")),
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_right_2.png")),
                    ImageIO.read(getClass().getResourceAsStream("/images/mario/mario_walk_right_3.png"))
            };

        } catch (IOException e) {
            System.err.println("Error loading Player images: " + e.getMessage());
        }
    }


    /**
     * Draws the player on the screen using the provided Graphics object.
     * The player is drawn as a Mario image representing its current position.
     *
     * @param g The Graphics object used for drawing the player.
     */
    public void draw(Graphics g) {
        BufferedImage currentImage = null;

        switch (direction) {
            case "right":
                currentImage = getWalkingSprite(marioWalkRight);
                break;

            case "left":
                currentImage = getWalkingSprite(marioWalkLeft);
                break;

            case "idleR":
                currentImage = marioIdleR;  // Use idle right sprite
                break;

            case "idleL":
                currentImage = marioIdleL;  // Use idle left sprite
                break;
            case "up":
                currentImage = marioIdleR;
                break;
            case "down":
                // For simplicity, use the right idle image for up/down.
                currentImage = marioIdleR;
                break;
        }

        // Draw the current image if available, otherwise a fallback green rectangle
        if (currentImage != null) {
            g.drawImage(currentImage, getX(), getY(), width, height, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(getX(), getY(), width, height);  // Draw a rectangle as a fallback
        }
    }

    private BufferedImage getWalkingSprite(BufferedImage[] walkSprites) {
        if (walkSprites == null) return null;

        // Control animation speed
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            animationIndex = (animationIndex + 1) % walkSprites.length;
        }

        return walkSprites[animationIndex];
    }



    /**
     * Updates the player's position based on the new coordinates received from the server.
     *
     * @param x The new x-coordinate of the player.
     * @param y The new y-coordinate of the player.
     * @param onGround Whether the player is on the ground.
     * @param direction The new direction the player is facing.
     */
    public void updatePosition(int x, int y, boolean onGround, String direction) {
        this.x = x;
        this.y = y;
        this.onGround = onGround;  // Update the player's ground status
        this.direction = direction;  // Update the player's direction
        System.out.println("Client: Player updated to position: x=" + x + ", y=" + y + ", direction=" + direction);
    }

    // Getter and setter methods for position and state

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
