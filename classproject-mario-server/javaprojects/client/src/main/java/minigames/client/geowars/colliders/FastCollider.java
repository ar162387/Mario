package minigames.client.geowars.colliders;

import minigames.client.geowars.gameobjects.*;
import minigames.client.geowars.util.Vector2D;

/**
 * A type of collider for GeoWars.
 * Used by GameObjects that move very quickly, and have the potential to pass
 * through other GameObjects in a single frame.
 * Works by casting a ray from the previous position to the current position of
 * the GameObject.
 * If the ray intersects with another Collider, a Collision is detected.
 * 
 * Right now, this only detects collisions with other Colliders, not with other
 * FastColliders.
 * At this time, there is no use-case for this, as only projectiles use
 * FastColliders and they do not interact when colliding with eachother anyway.
 */
public class FastCollider extends Collider {

  /**
   * Constructor for FastCollider.
   * 
   * @param parent    the GameObject that this Collider is attached to
   * @param isTrigger whether this Collider is a trigger or not
   */
  public FastCollider(GameObject parent, boolean isTrigger) {
    super(parent, isTrigger);
  }

  /**
   * Checks if this FastCollider is colliding with another Collider.
   * This executes an algorithm 4 times to see if the line segment from the past
   * position to the current position of the parent GameObject intersects with any
   * of the sides of the other Collider.
   * The algorithm was found at
   * https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
   * and is credited to 'Gareth Rees'.
   * It is based off the article 'Intersection of two lines in three-space' by
   * Ronald Goldman, and is the 2-dimensional specialisation of the 3-dimensional
   * algorithm.
   * 
   * @param other the Collider to check for collision with
   * @return boolean isColliding - True if the ray cast by this FastCollider
   *         intersects with the other Collider, false otherwise.
   */
  @Override
  public boolean isColliding(Collider other) {
    // If the other Collider is a FastCollider, then these two objects can't
    // collide.
    if (other instanceof FastCollider) {
      return false;
    }

    // Retrieve the Vectors representing the corners of the other collider.
    Vector2D[] otherCorners = other.getCorners();

    // If both the past and current position of the parent object are inside the
    // other collider, that is a collision.

    // We can find the bounds of the other collider from corners 0 and 2.
    Vector2D topLeft = otherCorners[0];
    Vector2D bottomRight = otherCorners[2];
    if (isPointInsideBounds(parent.getPastPosition(), topLeft, bottomRight)
        && isPointInsideBounds(parent.getPosition(), topLeft, bottomRight)) {
      return true;
    }

    // If both previous checks fail, then we proceed with the line-segment
    // intersection algorithm.

    boolean isColliding = false;

    // Get the previous and current positions of the parent GameObject.
    // These are used as 'p' and 'r' in the algorithm, and the ray can be
    // represented as 'p + r'.
    Vector2D p = parent.getPastPosition();
    // r is the vector from the past position to the current position, so we get
    // this by subtracting the past position from the current position.
    Vector2D r = new Vector2D();
    r.sub(parent.getPosition(), p);

    Vector2D q;
    Vector2D s;

    // Check if the ray intersects with any of the sides of the other collider.
    for (int i = 0; i < 4; i++) {
      // Choose the two corners that make up side i.
      // These are used as 'q' and 's' in the algorithm, and the line segment can be
      // represented as 'q + s'.
      q = otherCorners[i];
      // s is the vector from the first corner to the second corner, so we get this by
      // subtracting the first corner from the second corner.
      s = new Vector2D();
      s.sub(otherCorners[(i + 1) % 4], q);

      // Any point on the two lines can be found by 'p + tr' and 'q + us'.
      // If the two lines intersect, then we can find 't' and 'u' such that:
      // p + tr = q + us

      // If we define the 2-d cross product as 'v x w = vx * wy - vy * wx',
      // then we can find 't' and 'u' by the following:

      // Finding 't':
      // (p + tr) x s = (q + us) x s
      // since 's x s = 0':
      // t(r x s) = (q - p) x s
      // t = (q - p) x s / (r x s)
      Vector2D qMinusP = new Vector2D();
      qMinusP.sub(q, p);

      double qMinusPCrossS = Vector2D.cross(qMinusP, s);
      double rCrossS = Vector2D.cross(r, s);
      double rCrossSInverse = 1 / rCrossS;

      double t = qMinusPCrossS * rCrossSInverse;

      // Finding 'u':
      // (p + tr) x r = (q + us) x r
      // since 'r x r = 0':
      // u(s x r) = (q - p) x r
      // u = (p - q) x r / (s x r)
      // Remembering that 's x r = -r x s', we can rearrange this in terms we have
      // already computed:
      // u = (q - p) x r / (r x s)
      double qMinusPCrossR = Vector2D.cross(qMinusP, r);

      double u = qMinusPCrossR * rCrossSInverse;

      // There are then 4 cases to check:
      // 1. If 'r x s = 0' and '(q - p) x r = 0', then the two lines are collinear.
      // For simplicity, we will consider this not a collision, though we could check
      // if the two lines overlap or are disjoint.
      // 2. If 'r x s = 0' and '(q - p) x r != 0', then the two lines are parallel and
      // non-intersecting. This is also not a collision.
      // 3. If 'r x s != 0' and '0 <= t <= 1' and '0 <= u <= 1', then the two lines
      // intersect at 'p + tr' and 'q + us'. This is a collision. We don't really care
      // about the point of intersection, just that it exists.
      // 4. Otherwise, the two lines are not parallel, but they do not intersect
      // either. This is not a collision.
      // We can just check for case 3 and return true if so, continuing through the
      // loop otherwise.
      if (rCrossS != 0 && t >= 0 && t <= 1 && u >= 0 && u <= 1) {
        // Since we are accepting cases where t = 0 or t = 1, we are accepting cases
        // where the collision occurs right on the corner of the other collider. This is
        // preferable, as the bullets do have some visual size, even if their collider
        // is a point.
        isColliding = true;
        break;
      }

      // If we have reached the end of the loop, then there was no collision, and we
      // can return false.
    }

    return isColliding;
  }

  private static boolean isPointInsideBounds(Vector2D point, Vector2D topLeft, Vector2D bottomRight) {
    return point.x >= topLeft.x && point.x <= bottomRight.x && point.y >= topLeft.y && point.y <= bottomRight.y;
  }

}
