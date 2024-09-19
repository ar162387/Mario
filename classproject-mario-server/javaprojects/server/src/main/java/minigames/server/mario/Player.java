package minigames.server.mario;

import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Player class represents a player in the Mario game. It handles movement, jumping, and player state.
 */
public class Player {

    private String name;  // Name of the player
    private int x, y;     // Player's position
    private int playerWidth = 32;
    private int playerHeight = 64; // Player's size
    private int dx;  // Horizontal movement speed
    private double dy;  // Vertical movement speed
    private boolean jumping;  // Whether the player is jumping
    private boolean onGround;  // Whether the player is on the ground
    private final int jumpStrength;  // Strength of the jump
    private final double gravity;  // Gravity applied to the player
    private final int groundLevel;  // Ground level (y-coordinate)
    private int health = 4;  // Player's current health
    private static final int MAX_HEALTH = 4;  // Maximum health
    private String direction;  // Direction the player is facing (idleR, idleL, left, right)
    private boolean hit = false; // Flag to indicate if the player is hit
    private final double bounceStrength = -8; // Adjust this value for bounce height

    private  int score ;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a new Player object with specified position, size, and initial state.
     */
    public Player(String name, int x, int y, int playerWidth, int playerHeight, int health) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.playerWidth = playerWidth;
        this.playerHeight = playerHeight;
        this.health = health;
        this.jumping = false;
        this.onGround = true;
        this.jumpStrength = -15;
        this.gravity = 1;
     //   this.groundLevel = 475;
        this.groundLevel = 425;
        this.direction = "idleR";  // Initial state is idle facing right
        this.score = 0;
    }

    // Getter and setter methods for player's position and state

    public int getScore(){return this.score;}
    public void setScore(int score){ this.score = score;}
    public void increasescore(){this.score += 25;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPlayerWidth() {
        return playerWidth;
    }

    public void setPlayerWidth(int width) {
        this.playerWidth = width;
    }

    public int getPlayerHeight() {
        return playerHeight;
    }

    public void setPlayerHeight(int height) {
        this.playerHeight = height;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public int getJumpStrength() {
        return jumpStrength;
    }

    public double getGravity() {
        return gravity;
    }

    public int getGroundLevel() {
        return groundLevel;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Moves the player horizontally based on the provided direction.
     */
    public void move(String direction) {
        int screenWidth = 1200;
        int speed = 5;

        switch (direction) {
            case "left":
                dx = -speed;
                setDirection("left");
                break;
            case "right":
                dx = speed;
                setDirection("right");
                break;
            case "up":
                if (onGround) {
                    jump();
                }
                break;
            case "down":
                dy = speed;
                setDirection("down");
       //         while (y < 475) {
                while (y < 475) {
                    y += dy;
                }
                jumping = false;
                onGround = true;
                break;
            default:
                dx = 0;
                setIdleState();
                break;
        }

        // Apply movement
        x += dx;
        constrainWithinScreen();
    }

    /**
     * Sets the player to the correct idle state based on the last direction faced.
     */
    private void setIdleState() {
        if (direction.equals("left") || direction.equals("idleL")) {
            setDirection("idleL");
        } else {
            setDirection("idleR");
        }
    }

    private void constrainWithinScreen() {
        int screenWidth = 1200;
        if (x < 0) {
            x = 0;
        } else if (x > screenWidth - playerWidth) {
            x = screenWidth - playerWidth;
        }
        dx = 0;
    }

    public void jump() {
        if (!jumping && onGround) {
            jumping = true;
            onGround = false;
            dy = jumpStrength;
        }

        if (jumping) {
            dy += gravity;
            y += dy;

            if (y >= groundLevel) {
                y = groundLevel;
                jumping = false;
                onGround = true;
                dy = 0;
            }
        }
    }

    public void applyBounce() {
        this.dy = bounceStrength;
        this.jumping = true;
        this.onGround = false;
    }

    public void resetPlayer() {
        x = 0;
        y = groundLevel;
        health = MAX_HEALTH;
        jumping = false;
        onGround = true;
        direction = "idleR"; // Reset to idle facing right
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, playerWidth, playerHeight);
    }

    public void decreaseHealth() {
        if (!hit) {
            health--;
            setHitFlag(true);
        }
    }

    public void setHitFlag(boolean value) {
        this.hit = value;
        if (value) {
            scheduler.schedule(() -> this.hit = false, 2, TimeUnit.SECONDS);
        }
    }

    public boolean isHit() {
        return hit;
    }

    public void increaseHealth() {
        if(health <= 3) {
            this.health += 1;
        }
    }
}
