package minigames.client.geowars.colliders;

import minigames.client.geowars.gameobjects.GameObject;
import minigames.client.geowars.util.Vector2D;

/**
 * Collider class for GeoWars.
 * Handles collision detection between GameObjects.
 * Has a bounding box relative to the parent object's position.
 */
public class Collider {

    // Collider Attributes
    protected GameObject parent;
    /*
     * Right now we are only supporting rightly oriented rectangular colliders, we
     * probably want to be able to support circular colliders as well. Simply done
     * by extending this class into two subclasses each with their bounds defined
     * differently, and methods to check intersection as well.
     */
    private double width, height;
    private boolean isEnabled = true;
    private boolean isTrigger;

    // Constructor
    public Collider(GameObject parent, double width, double height, boolean isTrigger) {
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.isTrigger = isTrigger;

        parent.getEngine().registerCollider(this);
    }

    public Collider(GameObject parent, boolean isTrigger) {
        this.parent = parent;
        width = 0;
        height = 0;
        this.isTrigger = isTrigger;

        parent.getEngine().registerCollider(this);
    }

    /**
     * Checks if the bounding-box of this Collider A is overlapping
     * with the bounding-box of another Collider B.
     *
     * @param Collider otherCollider - A reference to another Collider object.
     * @return boolean isColliding - True is the two Collider instances are
     *         overlapping, false otherwise.
     */
    public boolean isColliding(Collider colliderB) {
        if (colliderB instanceof FastCollider) {
            return colliderB.isColliding(this);
        }

        // Get positions of both instances
        Vector2D posA = this.parent.getPosition();
        Vector2D posB = colliderB.parent.getPosition();

        // Define this bounding-box
        double leftA = posA.x - this.width / 2;
        double rightA = posA.x + this.width / 2;
        double topA = posA.y - this.height / 2;
        double bottomA = posA.y + this.height / 2;

        // Define other bounding-box
        double leftB = posB.x - colliderB.getWidth() / 2;
        double rightB = posB.x + colliderB.getWidth() / 2;
        double topB = posB.y - colliderB.getHeight() / 2;
        double bottomB = posB.y + colliderB.getHeight() / 2;

        // Splitting this logic out for clarity.
        // Check if the bounding boxes are overlapping on the x-axis.
        boolean isXColliding = false;
        if (leftA > leftB && leftA < rightB) {
            // Case 1: Left edge of A is between left and right edge of B.
            isXColliding = true;
        } else if (rightA > leftB && rightA < rightB) {
            // Case 2: Right edge of A is between left and right edge of B.
            isXColliding = true;
            // Case 1 and 2 catch all cases where A is wholly inside B.
        } else if (leftA < leftB && rightA > rightB) {
            // Case 3: A is larger than B and wholly encases B.
            isXColliding = true;
        }

        // Do the same for the y-axis.
        boolean isYColliding = false;
        if (topA > topB && topA < bottomB) {
            // Case 1: Top edge of A is between top and bottom edge of B.
            isYColliding = true;
        } else if (bottomA > topB && bottomA < bottomB) {
            // Case 2: Bottom edge of A is between top and bottom edge of B.
            isYColliding = true;
            // Case 1 and 2 catch all cases where A is wholly inside B.
        } else if (topA < topB && bottomA > bottomB) {
            // Case 3: A is larger than B and wholly encases B.
            isYColliding = true;
        }

        // If both x and y are colliding, then the two bounding boxes are colliding.
        boolean isColliding = isXColliding && isYColliding;
        return isColliding;
    }

    /**
     * Execute the action of this Collider.
     * 
     * @param Collision c - The collision object that caused the action to be
     *                  executed.
     */
    public void collide(Collision c) {
        parent.onCollision(c);
    }

    /**
     * Safely destroy the Collider.
     */
    public void destroy() {
        parent.getEngine().removeCollider(this);
    }

    /* ---------------------- Getters and Setters ---------------------- */

    /**
     * Getter function for Collider parent
     *
     * @param none
     * @return GameObject parent
     */
    public GameObject getParent() {
        return this.parent;
    }

    /**
     * Getter function for Collider position.
     * 
     * @return Vector2D position - Collider position
     */
    public Vector2D getPosition() {
        return this.parent.getPosition();
    }

    /**
     * Getter function for Collider width
     *
     * @param none
     * @return double width - Collider width
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Getter function for Collider height
     *
     * @param none
     * @return double height - Collider height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Getter function for Collider isEnabled
     *
     * @param none
     * @return boolean isEnabled - Collider isEnabled
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Getter function for Collider isTrigger
     *
     * @param none
     * @return boolean isTrigger - Collider isTrigger
     */
    public boolean isTrigger() {
        return this.isTrigger;
    }

    /**
     * Getter function for Collider corners
     * 
     * @return Vector2D[] corners - Array of Vector2D objects representing the
     *         positions of the corners of the Collider. Arranged in such a way that
     *         the first corner is the top-left corner, and the rest are arranged in
     *         a clockwise manner.
     */
    public Vector2D[] getCorners() {
        Vector2D[] corners = new Vector2D[4];
        Vector2D pos = this.parent.getPosition();
        corners[0] = new Vector2D(pos.x - width / 2, pos.y - height / 2);
        corners[1] = new Vector2D(pos.x + width / 2, pos.y - height / 2);
        corners[2] = new Vector2D(pos.x + width / 2, pos.y + height / 2);
        corners[3] = new Vector2D(pos.x - width / 2, pos.y + height / 2);
        return corners;
    }

    /**
     * Setter function for Collider width
     *
     * @param double width - Collider width
     * @return none
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Setter function for Collider height
     *
     * @param double height - Collider height
     * @return none
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Setter function for Collider isEnabled
     *
     * @param boolean isEnabled - Collider isEnabled
     * @return none
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Setter function for Collider isTrigger
     *
     * @param boolean isTrigger - Collider isTrigger
     * @return none
     */
    public void setTrigger(boolean isTrigger) {
        this.isTrigger = isTrigger;
    }
}
