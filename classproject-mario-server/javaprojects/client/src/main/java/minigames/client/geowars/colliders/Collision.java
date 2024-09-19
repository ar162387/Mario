package minigames.client.geowars.colliders;

/**
 * Collision class for GeoWars.
 */
public class Collision {

    // Collision Attributes
    private Collider otherCollider;
    private Collider thisCollider;

    // Constructor
    public Collision(Collider otherCollider, Collider thisCollider) {
        this.otherCollider = otherCollider;
        this.thisCollider = thisCollider;

        if (thisCollider.getParent() == otherCollider.getParent()) {
            System.err.println("Collision between two colliders of the same object.");
        }
    }

    /* ------------------------ Getters ------------------------ */

    public Collider getOtherCollider() {
        return otherCollider;
    }

    public Collider getThisCollider() {
        return thisCollider;
    }
}
