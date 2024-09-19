package minigames.client.bomberman;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;


/**
 * The Enemy class represents an enemy in the game.
 * Contributors: Daniel Gooden - dgooden@myune.edu.au
 *               Lixang Li - lli32@myune.edu.au
 */

public class Enemy implements GameCharacter {
    private double x, y;
    private int lives = 1;
    private double speed = 1;
    private ImageView enemyShape = BombermanGraphics.getInstance().getTileSprite(TileType.ENEMY); //TODO this will be animated and null checked
    private Direction direction = Direction.DOWN;

    // Invulnerability variables
    private long lastHitTime = 0;
    private boolean isInvulnerable = false;
    private static final long INVULNERABILITY_DURATION = 2_000_000_000L; // 2 seconds

    private MovementStrategy ai = new SeekPlayerStrategy();

    private Integer points = 100; // Worth how many points
    /**
     * Constructor
     * @param x
     * @param y
     */
    public Enemy (int x, int y) {
        this.x = x * GameConstants.TILE_SIZE;
        this.y = y * GameConstants.TILE_SIZE;

        updatePosition();
    }

    /**
     * Constructor
     * @param x
     * @param y
     * @param lives
     */

    public Enemy(int x, int y, int lives) {
        this.x = x * GameConstants.TILE_SIZE;
        this.y = y * GameConstants.TILE_SIZE;
        this.lives = lives;

        updatePosition();
    }

    /**
     * Constructor
     * @param x
     * @param y
     * @param lives
     * @param speed
     */

    public Enemy(int x, int y, int lives, int speed) {
        this.x = x * GameConstants.TILE_SIZE;
        this.y = y * GameConstants.TILE_SIZE;
        this.lives = lives;
        this.speed = speed;

        updatePosition();
    }
    /**
     * get ai movement strategy
     */
    public MovementStrategy getMovementStrategy() {

        return ai;
    }
    /**
     * Set ai movement strategy
     */
    public void setMovementStrategy(MovementStrategy ai) {
        this.ai = ai;
    }

    /**
     *  Gets the speed of this enemy
     *  Used for checking collision boxes at the moment
     * @return Double enemy speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Move the enemy
     * @param dx
     * @param dy
     */

    public void move(int dx, int dy) {
        if (dx > 0) {
            setDirection(Direction.RIGHT);
        } else if (dx < 0) {
            setDirection(Direction.LEFT);
        } else if (dy > 0) {
            setDirection(Direction.DOWN);
        } else if (dy < 0) {
            setDirection(Direction.UP);
        }

        this.x += dx * speed;
        this.y += dy * speed;
        updatePosition();
    }


    /**
     * Get the x position of the enemy
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y position of the enemy
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Set the position of the enemy
     * @param x
     * @param y
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updatePosition();
    }

    /**
     * Get the amount of points for the enemy kill
     */

    public Integer getPoints() {
        return points;
    }

    /**
     * Sets the points for the enemy
     * @param points
     */
    public void setPoints(Integer points) {
        this.points = points;
    }
    /**
     * Update the position of the enemy
     */
    public void updatePosition() {
        try {
            enemyShape.setX(x);
            enemyShape.setY(y);
        } catch (Exception e) {
            System.err.println("Error while updating graphic for Enemies" + e.getMessage());
            e.printStackTrace(); // Optional: print the stack trace

        }
    }

    /**
     * Update the enemy
     */
    public void update(long now) {
        // Update the enemy state
        if (isInvulnerable && (now - lastHitTime > INVULNERABILITY_DURATION)) {
            isInvulnerable = false;
        }
    }

    /**
     * Method for when an enemy is hit by a bomb
     * @param now
     */
    public void hit(long now) {
        if (!isInvulnerable) {
            Sound.getInstance().playRandomEnemySound();

            decrementLives();
            isInvulnerable = true;
            lastHitTime = now;
        }
    }

    /**
     * Get the direction of the enemy
     * @return direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set the direction of the enemy
     * @param direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the graphic of the enemy
     * @return enemyShape
     */
    public Node getGraphic() {
        return enemyShape;
    }

    /**
     * Get the lives of the enemy
     * @return lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Set the lives of the enemy
     * @param lives
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Decrement the lives of the enemy
     */
    public void decrementLives() {
        DebugManager.getInstance().log("Enemy hit");
        lives--;
    }

    /**
     * Handle the death of the enemy
     * @param onFinished
     */
    public void handleDeath(Runnable onFinished) {
        onFinished.run();
    }

    /** Draws a rectangular node for collision checking
     * Implements Player.getBounds
     * @return Node Rectangle
     */
    public Node getBounds() {
        double gX = getX();
        double gY = getY();

        // size reduction
        final double sizeReduction = GameConstants.ENEMY_SIZE_REDUCTION; // Adjust this value as needed

        double offsetX = (GameConstants.TILE_SIZE - (GameConstants.TILE_SIZE - sizeReduction)) / 2;
        double offsetY = GameConstants.TILE_SIZE - (GameConstants.TILE_SIZE - sizeReduction);

        // Return the bounding box with the corrected offsets and reduced size
        return new Rectangle(gX + offsetX, gY + offsetY,
                GameConstants.TILE_SIZE - sizeReduction,
                GameConstants.TILE_SIZE - sizeReduction);
    }

    /**
     * Set the graphic imageview sprite for the enemy
     * @param imageview
     */
    public final void setGraphic(ImageView imageview) {
        this.enemyShape = imageview;
    }
}
