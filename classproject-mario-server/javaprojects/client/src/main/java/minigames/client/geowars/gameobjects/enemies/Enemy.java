package minigames.client.geowars.gameobjects.enemies;

import java.awt.Graphics;

import minigames.client.geowars.*;
import minigames.client.geowars.colliders.Collider;
import minigames.client.geowars.colliders.Collision;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.util.Vector2D;
import minigames.client.geowars.util.DeltaTime;
import minigames.client.geowars.scenes.Level;
import minigames.client.geowars.gameobjects.projectiles.Projectile;
import minigames.client.geowars.rendering.Drawing;

/**
 * Enemy class for GeoWars.
 * Superclass that specific enemy types should extend.
 * Contains basic attributes and methods that all enemies should have.
 * Health (default = 1), Score (default = 15), etc.
 * Has a Collider, which should interact with walls, the player, and
 * projectiles.
 * Has logic to dictate movement.
 */
// TODO: Refactor to align with class structure design.
public abstract class Enemy extends GameObject {

    // Basic Enemy Attributes (simple enemy will move in straight line)
    /*
     * I can't see any reason why we would want double precision on health or score.
     * Why take the
     * risk of having some floating point error when we just need an integer?
     */
    protected int health;
    protected double speed;
    protected double score;
    protected double rotation;
    protected Vector2D direction;
    protected Vector2D velocity;

    protected double creationTime; // Time enemy was created in seconds
    protected double movementDelay = 1.0; // Time after creation that enemy becomes active.
    protected boolean movementStarted;

    protected LevelManager levelManager;

    public final static int PURPLE = 0;
    public final static int LIGHT_BLUE = 1;
    public final static int PINK = 2;
    public final static int GREEN = 3;

    // Constructor
    public Enemy(GeoWars engine, Vector2D position, double rotation, double width, double height, int health,
            double speed, double score) {
        super(engine, position);
        this.rotation = rotation;
        this.health = health;
        this.speed = speed;
        this.score = score;
        this.direction = new Vector2D();
        this.velocity = new Vector2D();
        this.movementStarted = false;
        this.creationTime = DeltaTime.levelTime();

        Collider mainCollider = new Collider(this, width, height, false);
        addCollider(mainCollider);

        if (LevelManager.isInstanceNull()) {
            System.err.println("LevelManager instance is null");
        } else {
            this.levelManager = LevelManager.getInstance(null, null);
        }
    }

    @Override
    public void start() {
        super.start();

        // Disable colliders until movement starts.
        for (Collider c : colliders) {
            c.setEnabled(false);
        }
    }

    /* ------------------- Getters and Setters ------------------- */

    /**
     * Gets enemies current health
     *
     * @return double value containing the enemies current health
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Gets the score received from defeating the enemy
     * 
     * @return double containing the score received
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Update enemy health
     *
     * @param damage - double value containing damage experienced.
     */
    public void setHealth(int damage) {
        this.health = this.health - damage;
    }

    /* ------------------- Enemy Movement ------------------- */
    /**
     * Update enemy position every game tick
     * 
     * @param none
     * @return void
     */
    @Override
    public void update() {

        if (!movementStarted) {
            boolean pastMovementStarted = movementStarted;
            movementStarted = checkMovementStarted();

            // If this is the first frame where movement is active, turn on the colliders.
            if (movementStarted && !pastMovementStarted) {
                for (Collider c : colliders) {
                    c.setEnabled(true);
                }
            }
        }

        this.enemyMovement();
    }

    /**
     * Checks if 1s has passed since enemy spawned.
     * 
     * @param none
     * @return boolean movementStarted - true if 1s has passed since spawn, false
     *         otherwise.
     */
    public boolean checkMovementStarted() {
        double now = DeltaTime.levelTime();
        if (now - this.creationTime > movementDelay) {
            return true;
        }
        return false;
    }

    /**
     * Enemy movement is different for each enemy type, hence logic is handled in
     * subclasses.
     * 
     * @param none
     * @return void
     */
    public abstract void enemyMovement();

    /* ---------------- Collision Detection ---------------- */

    /**
     * Handles collision detection between Enemy object and any other GameObject.
     * 
     * @param Collision c - Collision instance containing current Enemy instance and
     *                  other GameObject instance
     * @return void
     */
    @Override
    public void onCollision(Collision c) {
        Collider otherCollider = c.getOtherCollider();
        Collider thisCollider = c.getThisCollider();
        if (!otherCollider.isTrigger() && !thisCollider.isTrigger()) {

            GameObject thisObject = this;
            GameObject otherObject = otherCollider.getParent();

            // Enemy-Player
            if (otherObject instanceof Player) {
                this.enemyPlayerCollision();
            }
            // Enemy-Projectile
            else if (otherObject instanceof Projectile) {
                this.enemyProjectileCollision();
            }
            // Enemy-Wall
            else if (otherObject instanceof Wall) {
                this.enemyWallCollision(thisObject, otherObject);
            }
            // Enemy-Exploder
            else if (otherObject instanceof PlayerExplosion) {
                this.destroy();
            }
        }
    }

    /* -------------- Handle Collision Events -------------- */

    /**
     * Controls Enemy-Wall collisions.
     * Basic functionality is to kill relevant velocity.
     * Some enemy types will override this functionality.
     * 
     * @param GameObject thisObject - GameObject from super. References current
     *                   instance of Enemy object
     * @param GameObject otherObject - GameObject from super. References current
     *                   instance of Wall object
     * @return none
     */
    public void enemyWallCollision(GameObject thisObject, GameObject otherObject) {
        double enemyX = this.getCurrentX();
        double enemyY = this.getCurrentY();

        double[] pos = new double[2];
        this.position.get(pos);

        double[] vel = new double[2];
        this.velocity.get(vel);

        if (otherObject instanceof Wall) {
            Wall wall = (Wall) otherObject;
            double wallThickness = wall.getWidth();
            double wallX = wall.getCurrentX();
            double wallY = wall.getCurrentY();

            double dx = Math.abs(enemyX - wallX);
            double dy = Math.abs(enemyY - wallY);

            // Close to vertical wall, kill x velocity.
            if (dx < dy) {
                this.velocity.set(0, vel[1]);
            }
            // Close to horizontal wall, kill y velocity.
            else {
                this.velocity.set(vel[0], 0);
            }
        }
    }

    /**
     * Manages Enemy-Player collision detection
     * This enemy is destroyed.
     * Other effects of this interaction are handled elsewhere.
     * 
     * @param none
     * @return void
     */
    public void enemyPlayerCollision() {
        this.destroy();
    }

    /**
     * Manages Enemy-Projectile collision detection
     * Enemy health must be decreased by Projectile damage value, at this stage just
     * one for simplicity, and if health runs out, destroy the Enemy objec.
     * 
     * @param none
     * @return void
     */
    public void enemyProjectileCollision() {
        this.setHealth(1);
        if (this.getHealth() <= 0) {
            this.levelManager.enemyDefeated(this);
            this.destroy();
        }
    }

}