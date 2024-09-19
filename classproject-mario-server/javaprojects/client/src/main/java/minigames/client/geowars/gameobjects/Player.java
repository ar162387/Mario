package minigames.client.geowars.gameobjects;

import java.awt.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.colliders.Collision;
import minigames.client.geowars.gameobjects.projectiles.Projectile;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.gameobjects.enemies.Enemy;
import minigames.client.geowars.util.*;

/**
 * Player class for GeoWars.
 * Represents the player of the game.
 * Has logic/attributes to dictate movement and orientation.
 * Can shoot Projectiles.
 * Has a Collider, which should interact with walls and enemies.
 * Has a PlayerController, that should handle input from the user.
 */
public class Player extends GameObject {
    // VARIABLES

    private static final Logger logger = LogManager.getLogger(Player.class);
    public double maxSpeed = 5;
    private Vector2D velocity;
    private InputManager inputManager;
    private PlayerController pController;
    private LevelManager levelManager;
    private int lives;
    private boolean isDead;

    public static final double PLAYER_SIZE = 18;

    // CONSTRUCTORS

    /*
     * Update constructor to use inherited position and rotation attributes.
     */
    /**
     * Player Constructor.
     *
     * @param name - String value containing the players name.
     */
    public Player(GeoWars engine, double startX, double startY, int lives) {
        super(engine, new Vector2D(startX, startY));
        this.lives = lives;
        this.isDead = false;
        this.velocity = new Vector2D(0, 0);
        logger.info("Adding key bindings");

        Collider mainCollider = new Collider(this, PLAYER_SIZE, PLAYER_SIZE, false);
        addCollider(mainCollider);

        this.pController = new PlayerController(engine, this);

        if (InputManager.isInstanceNull()) {
            logger.error("InputManager instance is null");
        } else {
            this.inputManager = InputManager.getInstance(null, null);
        }

        this.drawing = Drawing.getDrawing(Drawing.PLAYER, this);
    }

    @Override
    public void start() {
        super.start();

        if (LevelManager.isInstanceNull()) {
            logger.error("LevelManager instance is null");
        } else {
            this.levelManager = LevelManager.getInstance(null, null);
        }
    }

    @Override
    public void update() {

        if (!isDead) {
            updateUserMovement();

            updateShooting();
        }

        updateNonUserMovement();
    }

    private void updateUserMovement() {
        // updating character postion
        if (this.pController.getUp() && !this.pController.getDown()) { // only triggers movement if down is not
                                                                       // pressed
            this.velocity.add(moveUp());
        }
        if (this.pController.getDown() && !this.pController.getUp()) { // only triggers movement if up is not
                                                                       // pressed
            this.velocity.add(moveDown());
        }
        if (this.pController.getLeft() && !this.pController.getRight()) { // only triggers movement if right is not
                                                                          // pressed
            this.velocity.add(moveLeft());
        }
        if (this.pController.getRight() && !this.pController.getLeft()) { // only triggers movement if left is not
                                                                          // pressed
            this.velocity.add(moveRight());
        }
    }

    private void updateNonUserMovement() {
        /**
         * Starts to reduce the velocity of movement of the Y axis.
         * When both up and down are not pressed.
         * OR
         * This occurs when both up and down are pressed at the same time
         */
        if ((!this.pController.getUp() && !this.pController.getDown())
                || (this.pController.getUp() && this.pController.getDown())) {
            this.velocity.sub(decelerateY());
        }
        /**
         * Starts to reduce the velocity of movement of the Y axis.
         * This occurs when both left and right are pressed at the same time
         * OR
         * When both left and right are not pressed.
         */
        if ((!this.pController.getLeft() && !this.pController.getRight())
                || (this.pController.getLeft() && this.pController.getRight())) {
            this.velocity.sub(decelerateX());
        }
        this.velocity.clamp(-1 * maxSpeed, maxSpeed); // sets the max velocity the player can reach
        this.getPosition().add(this.velocity); // updates the player location
    }

    private void updateShooting() {
        Point relativeLocation = inputManager.getRelativeLocation();
        this.rotate(relativeLocation);

        if (pController.getPress()) {
            pController.addTimeSinceBullet(DeltaTime.delta());
            double fireRate = pController.getFireRate();
            if (pController.getTimeSinceBullet() >= 1 / fireRate) {
                this.shoot(relativeLocation);
                pController.addTimeSinceBullet(-1 / fireRate);
            }
        }
    }

    private void lockMovement() {
        // killVelocity();
        // rotation = 0 - Math.PI / 2;
    }

    public Vector2D moveX(double externalForce, double setSpeed) {
        return new Vector2D((externalForce * setSpeed), 0);
    }

    public Vector2D moveY(double externalForce, double setSpeed) {
        return new Vector2D(0, (externalForce * setSpeed));
    }

    public Vector2D moveUp() {
        Vector2D calAccelY = moveY(this.pController.getExternalForce(), this.pController.getSpeed());
        calAccelY.negate();
        calAccelY.scale(DeltaTime.delta());
        return new Vector2D(calAccelY);
    }

    public Vector2D moveDown() {
        Vector2D calAccelY = moveY(this.pController.getExternalForce(), this.pController.getSpeed());
        calAccelY.scale(DeltaTime.delta());
        return new Vector2D(calAccelY);
    }

    public Vector2D moveLeft() {
        Vector2D calAccelX = moveX(this.pController.getExternalForce(), this.pController.getSpeed());
        calAccelX.negate();
        calAccelX.scale(DeltaTime.delta());
        return new Vector2D(calAccelX);
    }

    public Vector2D moveRight() {
        Vector2D calAccelX = moveX(this.pController.getExternalForce(), this.pController.getSpeed());
        calAccelX.scale(DeltaTime.delta());
        return new Vector2D(calAccelX);
    }

    public Vector2D decelerateX() {
        Vector2D calAccelX = moveX(this.pController.getExternalForce(), this.velocity.x * 0.9);
        calAccelX.scale(DeltaTime.delta());
        return new Vector2D(calAccelX);
    }

    public Vector2D decelerateY() {
        Vector2D calAccelY = moveY(this.pController.getExternalForce(), this.velocity.y * 0.9);
        calAccelY.scale(DeltaTime.delta());
        return new Vector2D(calAccelY);
    }

    /**
     * Sets the velocity to 0 on the X axis, can be used when colliding into wall
     * etc.
     * 
     * @return
     */
    public void killVelocityX() {
        this.velocity.set(0, this.velocity.y);
    }

    /**
     * Sets the velocity to 0 on the Y axis, can be used when colliding into wall
     * etc.
     * 
     * @return
     */
    public void killVelocityY() {
        this.velocity.set(this.velocity.x, 0);
    }

    /**
     * Sets the velocity to 0 on the both axis
     * 
     * @return
     */
    public void killVelocity() {
        killVelocityX();
        killVelocityY();
    }

    /**
     * Shoot a projectile from the player's location to the mouse
     * 
     * @param mouseLocation a Point representing the target location
     */
    public void shoot(Point mouseLocation) {
        // logger.info("Player shooting");
        new Projectile(engine, new Vector2D(getCurrentX(), getCurrentY()),
                new Vector2D(mouseLocation.x, mouseLocation.y));
    }

    @Override
    public void destroy() {
        this.pController.destroy();
        super.destroy();
    }

    @Override
    public boolean draw(Graphics g) {
        if (g != null && drawing != null) {
            drawing.draw(g, 1, rotation, null);
        }
        return true;
    }

    /* ---------------- Collision Detection ---------------- */

    @Override
    public void onCollision(Collision c) {
        Collider otherCollider = c.getOtherCollider();

        if (!otherCollider.isTrigger()) {
            GameObject otherObject = otherCollider.getParent();

            // Player-Enemy
            if (otherObject instanceof Enemy) {
                // System.out.println("Player is colliding with Enemy");
                this.onPlayerHit();
                Sound.getInstance().playSFX(Sound.Type.DEATH);
            }
            // Player-Projectile
            else if (otherObject instanceof Projectile) {
                // This should not be possible, only mentioned for debugging purposes.
                // System.out.println("Player is colliding with Projectile");
            }
            // Player-Wall
            else if (otherObject instanceof Wall) {
                // System.out.println("Player is colliding with Wall");
                this.onWallHit();
            }
        }
    }

    /**
     * Controls Player-Enemy collisions, ensuring Player loses a life.
     * Triggers a new collider to be created, wiping the screen of all enemies over
     * a brief period.
     */
    public void onPlayerHit() {
        levelManager.playerDied();
    }

    /**
     * Controls Player-Wall collisions, ensuring Player remains within the Play
     * Area.
     * 
     * @param none
     * @return none
     */
    public void onWallHit() {
        // Get Player location
        Player player = this;
        double playerX = player.getCurrentX();
        double playerY = player.getCurrentY();

        // Get Player velocity;
        double[] playerVelocity = new double[2];
        velocity.get(playerVelocity);
        double velocityX = playerVelocity[0];
        double velocityY = playerVelocity[1];

        // Define Play Area
        int borderThickness = 10;
        double leftWall = 50 + borderThickness;
        double rightWall = GeoWars.SCREEN_WIDTH - (50 + borderThickness);
        double topWall = 50 + borderThickness;
        double bottomWall = GeoWars.SCREEN_HEIGHT - (50 + borderThickness);
        ;

        // Left or Right Wall collisions
        if (playerX <= leftWall) {
            playerX = leftWall; // Clamp Player to leftWall
            killVelocityX();
        } else if (playerX >= rightWall) {
            playerX = rightWall; // Clamp Player to rightWall
            killVelocityX();
        }

        // Top or Bottom Wall collisions
        if (playerY <= topWall) {
            playerY = topWall; // Clamp Player to topWall
            killVelocityY();
        } else if (playerY >= bottomWall) {
            playerY = bottomWall; // Clamp Player to bottomWall
            killVelocityY();
        }

        // Update new position and velocity values.
        player.setPosition(new Vector2D(playerX, playerY));
    }

    // GETTERS

    /**
     * Gets the current X Coordinate value of the player.
     *
     * @return double value containing the players X Coordinate.
     */
    public double getCurrentX() {
        return this.getPosition().x;
    }

    /**
     * Gets the current Y Coordinate value of the player.
     *
     * @return double value containing the players Y Coordinate.
     */
    public double getCurrentY() {
        return this.getPosition().y;
    }

    /**
     * Gets the current Coordinates of the player.
     *
     * @return double[] value containing the players X & Y Coordinates.
     */
    public Vector2D getCurrentLocation() {
        return this.getPosition();
    }

    /**
     * Gets the current lives of the player.
     * 
     * @return int value containing the players current lives.
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * Gets the current state of the isDead flag.
     * 
     * @return boolean value containing the current state of the isDead flag.
     */
    public boolean isDead() {
        return this.isDead;
    }

    // SETTERS

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    /**
     * Set the player's angle based on a point
     * 
     * @param mouseLocation The location of the mouse, relative to the game window
     */
    public void rotate(Point mouseLocation) {
        rotation = Math.atan2(mouseLocation.y - this.getPosition().y, mouseLocation.x - this.getPosition().x);
    }

    /**
     * Sets the player's lives to the provided value.
     * 
     * @param lives - int value containing the new lives value.
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Sets the isDead flag to the provided value.
     * 
     * @param isDead - boolean value containing the new isDead value.
     */
    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
}