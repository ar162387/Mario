package minigames.client.geowars.gameobjects.projectiles;

import java.awt.Graphics;

import minigames.client.geowars.gameobjects.enemies.Enemy;
import minigames.client.geowars.rendering.Drawing;
import minigames.client.geowars.*;
import minigames.client.geowars.colliders.*;
import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.util.*;

/**
 * Projectile class for GeoWars.
 * Superclass that specific projectile types should extend. (Eventually)
 * Contains basic attributes and methods that all projectiles should have.
 * Has logic to dictate movement.
 * Has a Collider, which should interact with walls and enemies.
 */
public class Projectile extends GameObject {

    private Vector2D velocity;
    private final int speed = 1200;

    public static final double DEFAULT_WIDTH = 5;
    private static final double DEFAULT_HEIGHT = 5;

    /**
     * Projectile constructor
     * 
     * @param xPos    the projectile's x position
     * @param yPos    the projectile's y position
     * @param xTarget the target x position for the projectile
     * @param yTarget the target y position for the projectile
     */
    public Projectile(GeoWars engine, Vector2D position, Vector2D target) {
        super(engine, position);
        target.sub(position);
        Vector2D direction = target;
        direction.normalize();
        direction.scale(speed);
        this.velocity = direction;

        Collider mainCollider = new FastCollider(this, false);
        this.addCollider(mainCollider);

        drawing = Drawing.getDrawing(Drawing.PROJECTILE, this);
    }

    /**
     * @return the projectile's velocity
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * @return the projectile's speed in units per second
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Update the location of the projectile using its speed and direction
     */
    @Override
    public void update() {
        pastPosition.set(position);
        Vector2D scaledVelocity = new Vector2D(velocity);
        scaledVelocity.scale(DeltaTime.delta());
        position.add(scaledVelocity);
    }

    /*
     * Implement collision logic.
     */

    @Override
    public boolean draw(Graphics g) {
        if (g != null && drawing != null) {
            drawing.draw(g, 1, 0, null);
        }
        return true;
    }

    @Override
    public void onCollision(Collision c) {
        Collider otherCollider = c.getOtherCollider();

        if (!otherCollider.isTrigger()) {

            GameObject otherObject = otherCollider.getParent();

            if (otherObject instanceof Enemy || otherObject instanceof Wall) {
                this.destroy();
            }
        }
    }
}
