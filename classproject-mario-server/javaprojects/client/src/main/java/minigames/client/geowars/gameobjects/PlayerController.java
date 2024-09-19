package minigames.client.geowars.gameobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.geowars.*;

/**
 * PlayerController class for GeoWars.
 * Handles input from the user and translates it into actions for the Player.
 */
public class PlayerController extends GameObject {
    private static final Logger logger = LogManager.getLogger(PlayerController.class);
    private Player player;
    private boolean up, down, left, right, press;
    private double timeSinceBullet, speed, externalForce, fireRate;

    /**
     * PlayerController Constructor
     * 
     * @param player
     * @param panel
     */
    public PlayerController(GeoWars engine, Player player) {
        super(engine);
        this.player = player;
        this.externalForce = 2.0;
        this.fireRate = 10;

        if (GameManager.isInstanceNull()) {
            logger.error("GameManager instance is null");
        } else {
            speed = GameManager.getInstance(null, null).getOptions().getPlayerSpeed();
        }

        if (InputManager.isInstanceNull()) {
            logger.error("InputManager instance is null");
        } else {
            InputManager.getInstance(null, null).addPlayerController(this);
        }
    }

    @Override
    public void destroy() {
        if (InputManager.isInstanceNull()) {
            logger.error("InputManager instance is null");
        } else {
            InputManager.getInstance(null, null).releasePlayerController();
        }
        super.destroy();
    }

    // GETTERS
    /**
     * Retrieves the current value of the up boolean variable
     * 
     * @return up - value of the up bool
     */
    public boolean getUp() {
        return this.up;
    }

    /**
     * Retrieves the current value of the down boolean variable
     * 
     * @return down - value of the down bool
     */
    public boolean getDown() {
        return this.down;
    }

    /**
     * Retrieves the current value of the left boolean variable
     * 
     * @return left - value of the left bool
     */
    public boolean getLeft() {
        return this.left;
    }

    /**
     * Retrieves the current value of the right boolean variable
     * 
     * @return right - value of the right bool
     */
    public boolean getRight() {
        return this.right;
    }

    /**
     * Retrieves the current value of the press boolean variable
     * 
     * @return whether the mouse is pressed
     */
    public boolean getPress() {
        return this.press;
    }

    /**
     * Retrieves the time since the last bullet was fired (if mouse was held)
     * 
     * @return the time since the last bullet was fired in seconds
     */
    public double getTimeSinceBullet() {
        return this.timeSinceBullet;
    }

    /**
     * Retrieves the fire rate of the player, in projectiles per second
     * 
     * @return the fire rate of the player
     */
    public double getFireRate() {
        return this.fireRate;
    }

    /**
     * Retrieves the speed set by the player
     * 
     * @return speed which is the player set speed.
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Retrieves the externalForce value.
     * 
     * @return externalForce which a multiplier used by the player movement
     *         calculations.
     */
    public double getExternalForce() {
        return this.externalForce;
    }

    // SETTERS
    /**
     * Sets the up boolean value to the provided state.
     * 
     * @param newUp - new boolean value for up to be set to
     */
    public void setUp(boolean newUp) {
        this.up = newUp;
    }

    /**
     * Sets the down boolean value to the provided state.
     * 
     * @param newDown - new boolean value for down to be set to
     */
    public void setDown(boolean newDown) {
        this.down = newDown;
    }

    /**
     * Sets the left boolean value to the provided state.
     * 
     * @param newLeft - new boolean value for left to be set to
     */
    public void setLeft(boolean newLeft) {
        this.left = newLeft;
    }

    /**
     * Sets the right boolean value to the provided state.
     * 
     * @param newRight - new boolean value for right to be set to
     */
    public void setRight(boolean newRight) {
        this.right = newRight;
    }

    /**
     * Sets the press boolean value to the provided state.
     * 
     * @param newPress - new boolean value for press to be set to
     */
    public void setPress(boolean newPress) {
        this.press = newPress;
    }

    /**
     * Sets the timeSinceBullet value to the provided value
     * 
     * @param newTimeSinceBullet - new double for timeSinceBullet to be set to
     */
    public void setTimeSinceBullet(double newTimeSinceBullet) {
        this.timeSinceBullet = newTimeSinceBullet;
    }

    /**
     * Adds a certain amount of time to timeSinceBullet
     * 
     * @param extraTimeSinceBullet the amount of time to add
     */
    public void addTimeSinceBullet(double extraTimeSinceBullet) {
        this.timeSinceBullet += extraTimeSinceBullet;
    }

    /**
     * Sets the fireRate value to the provided value
     * 
     * @param newFireRate - new double for fireRate to be set to
     */
    public void setFireRate(double newFireRate) {
        this.fireRate = newFireRate;
    }

    /**
     * Updates the externalForce value.
     * 
     * @param newForce - new double value to set the external force to.
     */
    public void setExternalForce(double newForce) {
        this.externalForce = newForce;
    }

    /**
     * Resets the externalForce used by the player
     */
    public void resetExternalForce() {
        this.externalForce = 2.0;
    }

}
