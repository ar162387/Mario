package minigames.client.bomberman;

import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import javax.sound.sampled.DataLine;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The player object for someone who is playing the game
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 */

public class Player implements GameCharacter {
    private double x;
    private double y;
    private int lives;
    private double speed;
    private int power;
    private final ImageView graphic = new ImageView();
    private Direction direction = Direction.DOWN;
    private Direction previousDirection = Direction.DOWN;
    private boolean isWalking = false;
    private int animationFrame = 0;
    private long lastAnimationTime = 0;

    private ArrayList<ImageView> southFrames;
    private ArrayList<ImageView> northFrames;
    private ArrayList<ImageView> eastFrames;
    private ArrayList<ImageView> westFrames;

    // Invulnerability fields
    private long lastHitTime = 0;
    private boolean isInvulnerable = false;
    private static final long INVULNERABILITY_DURATION = 2_000_000_000L; // 2 seconds
    private static final long FLASH_INTERVAL = 50_000_000L; // 50 milliseconds
    private boolean isVisible = true;


    /**
     * Constructor
     * @param x
     * @param y
     * @param lives
     */
    public Player(int x, int y, int lives, double speed, int power) {
        this.x = x * GameConstants.TILE_SIZE;
        this.y = y * GameConstants.TILE_SIZE;
        this.lives = lives;
        this.speed = speed;
        this.power = power;

        loadAnimations();
        updateGraphic();
        updatePosition();
    }

    /**
     * Load the player animations for different directions
     */
    private void loadAnimations() {
        BombermanGraphics graphics = BombermanGraphics.getInstance();
        southFrames = new ArrayList<>();
        southFrames.add(graphics.getTileSprite(TileType.PLAYER_SOUTH));
        southFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_SOUTH1));
        southFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_SOUTH2));

        northFrames = new ArrayList<>();
        northFrames.add(graphics.getTileSprite(TileType.PLAYER_NORTH));
        northFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_NORTH1));
        northFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_NORTH2));

        eastFrames = new ArrayList<>();
        eastFrames.add(graphics.getTileSprite(TileType.PLAYER_EAST));
        eastFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_EAST1));
        eastFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_EAST2));
        eastFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_EAST3));

        westFrames = new ArrayList<>();
        westFrames.add(graphics.getTileSprite(TileType.PLAYER_WEST));
        westFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_WEST1));
        westFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_WEST2));
        westFrames.add(graphics.getTileSprite(TileType.PLAYER_WALK_WEST3));
    }
    
    /**
     * Method to move the player around
     * @param dx
     * @param dy
     */
    public void move(int dx, int dy) {
        this.x += dx * speed;
        this.y += dy * speed;

        updateGraphic();
        updatePosition();
    }

    /**
     * Updates the player's graphic based on the current state
     */
    private void updateGraphic() {
        if (direction != previousDirection) {
            animationFrame = 0; // Reset to the first frame
            previousDirection = direction; // Update the previous direction
        }
        if (isWalking) {
            long now = System.nanoTime();
            if (now - lastAnimationTime >= 200_000_000) { // 200ms per frame
                lastAnimationTime = now;

                if (direction == Direction.RIGHT || direction == Direction.LEFT) {
                    animationFrame = (animationFrame % 3) + 1; // Cycle through 3 frames
                } else {
                    animationFrame = (animationFrame % 2) + 1; // Cycle through 2 frames
                }
            }
        } else {
            animationFrame = 0; // Standing frame
        }

        try { // Added a try catch block to fix call in test - Li
            switch (direction) {
                case DOWN -> graphic.setImage(southFrames.get(animationFrame).getImage());
                case UP -> graphic.setImage(northFrames.get(animationFrame).getImage());
                case RIGHT -> graphic.setImage(eastFrames.get(animationFrame).getImage());
                case LEFT -> graphic.setImage(westFrames.get(animationFrame).getImage());
            }
        } catch (NullPointerException e) {
            DebugManager.getInstance().logError("NullPointerException occurred: " + e.getMessage());
        }
    }

    public void setIsWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    /**
     * Method for setting a specific position for the player
     * @param x
     * @param y
     */
    public void setPosition(int x, int y) {
        this.x = x * GameConstants.TILE_SIZE;
        this.y = y * GameConstants.TILE_SIZE;
    }

    /**
     * Private method for updating player position
     */
    private void updatePosition() {
        try {
            graphic.setX(x);
            graphic.setY(y);
        } catch (Exception e) {
            System.err.println("Error while updating graphic for player" + e.getMessage());
            e.printStackTrace(); // Optional: print the stack trace
        }
    }

    public Node getBounds() {
        double gX = graphic.getX();
        double gY = graphic.getY();

        // Offset X should center the bounding box horizontally
        double offsetX = (GameConstants.TILE_SIZE - GameConstants.PLAYER_BOUNDING_BOX_WIDTH) / 2;

        // Offset Y should position the bounding box at the feet
        double offsetY = (GameConstants.TILE_SIZE - GameConstants.PLAYER_BOUNDING_BOX_HEIGHT) * 0.9;

        // Return the bounding box with the corrected offsets
        return new Rectangle(gX + offsetX, gY + offsetY,
                GameConstants.PLAYER_BOUNDING_BOX_WIDTH,
                GameConstants.PLAYER_BOUNDING_BOX_HEIGHT);
    }

    /**
     * Used to update the player state
     */
    public void update(long now) {
        updateGraphic(); // Ensure the player's graphic is updated based on the current state

        if (isInvulnerable) {
            if (now - lastHitTime > INVULNERABILITY_DURATION) {
                isInvulnerable = false;
                graphic.setVisible(true); // Ensure the player is visible after invulnerability ends
            } else {
                // Flash the player during the invulnerability period
                if ((now - lastHitTime) / FLASH_INTERVAL % 2 == 0) {
                    graphic.setVisible(true);
                } else {
                    graphic.setVisible(false);
                }
            }
        } else {
            graphic.setVisible(true); // Ensure the player is visible when not invulnerable
        }

        if (!isWalking) {
            animationFrame = 0; // Reset to standing frame if not walking
        }
    }

    /**
     * Method for when a player is hit by an enemy or bomb
     * @param now
     */

    public void hit(long now) {
        if (!isInvulnerable) {
            decrementLives();
            isInvulnerable = true;
            lastHitTime = now;
            isVisible = true;

            // Sound
            Sound.getInstance().playSFX(Sound.Type.BUMP);
        }
    }

    /**
     * Returns the direction the player is facing
     * @return
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Private method for setting the Player direction
     * @param direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Function for getting the player graphic in a JavaFX Node object
     * @return
     */
    public Node getGraphic() {
        return graphic;
    }

    /**
     * Gets the player X position
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the player Y position
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * Get's the player's lives
     * @return
     */
    public int getLives() { return lives; }

    /**
     * Set the player lives
     * @param lives
     */
    public void setLives(int lives) { this.lives = lives; }

    /**
     * Decrement the player's lives
     */
    public void decrementLives() {
        lives--;
        DebugManager.getInstance().log("Player lives remaining: " + lives);
        //TODO Check GAME OVER CONDITION
    }

    /**
     * Handles the death of a player
     * @param onFinished
     */
    public void handleDeath(Runnable onFinished) {


        // show the death animation here
        // for now just run the runnable
        onFinished.run();
    }

    /**
     * Places a bomb on the player's position
     * @return
     */
    public Bomb placeBomb() {
        Node bounds = getBounds();
        Point bombPosition = calculateBombPosition(bounds);

        return new Bomb(bombPosition.getX(), bombPosition.getY(), this.getPower());
    }

    /**
     * Calculates the closest tile to the player
     * TODO: Change this to the direction the player is facing. to make it feel better
     * @param bounds - the player's bounds
     * @return
     */
    private Point calculateBombPosition(Node bounds) {
        Rectangle boundingBox = (Rectangle) bounds;
        // Calculate the center of the bounding box
        double boundingBoxCenterX = boundingBox.getX() + (boundingBox.getWidth() / 2);
        double boundingBoxCenterY = boundingBox.getY() + (boundingBox.getHeight() / 2);

        // Find the tile the bounding box center is currently in
        int tileX = (int) boundingBoxCenterX / GameConstants.TILE_SIZE;
        int tileY = (int) boundingBoxCenterY / GameConstants.TILE_SIZE;

        // Return the tile grid coordinates as a Point object
        return new Point(tileX, tileY);
    }

    /**
     * Represents the power of the player's bombs
     * @return power
     */
    public int getPower() {
        return power;
    }

    /**
     * Allows something to set the power level
     * @param power - the power of the player's bombs
     */

    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Allows speed to be set externally when player is instantiated
     * @param speed - the speed the player runs
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
