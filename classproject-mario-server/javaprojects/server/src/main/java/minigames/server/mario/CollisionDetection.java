package minigames.server.mario;

import java.util.List;
import java.util.Map;
import java.awt.Rectangle;

/**
 * CollisionDetection class handles collision detection between players and enemies.
 */
public class CollisionDetection {

    /**
     * Checks for collisions between players and enemies and updates player health if a collision is detected.
     *
     * @param players The map of players in the game.
     * @param enemies The list of enemies in the game.
     */
    public boolean checkCollisions(Map<String, Player> players, List<Enemy> enemies , List<Herb> herbs) {
        boolean collisionDetected = false;
        for (Player player : players.values()) {
            for (Enemy enemy : enemies) {
                if (isTopCollision(player, enemy)) {
                    enemy.deactivate(); // Assuming deactivate method handles enemy defeat
                    player.applyBounce();
                    player.increasescore();
                    System.out.println("Output: Enemy defeated by player jumping on top!");
                } else if ( isCollision(player.getBounds(), enemy.getBounds())) {
                    enemy.setCollisionDetected(true);
                    player.decreaseHealth();
                    System.out.println("Output: Collision detected between player and enemy!");
                    collisionDetected = true;
                }
            }

            // Add collision logic for herbs
            for (Herb herb : herbs) {
                if (herb.isActive() && isCollision(player.getBounds(), herb.getBounds())) {
                    herb.collect();  // Mark the herb as collected
                    player.increaseHealth();  // Increase player's health by 1
                    System.out.println("Output: Herb collected by player!");
                }
            }
        }
        return collisionDetected;
    }

    /**
     * Determines if two rectangles (bounding boxes) intersect.
     *
     * @param rect1 The bounding rectangle of the first object.
     * @param rect2 The bounding rectangle of the second object.
     * @return true if the rectangles intersect, false otherwise.
     */
    private boolean isCollision(Rectangle playerRect, Rectangle enemyRect) {
        if (!playerRect.intersects(enemyRect)) {
            return false;  // No collision if there is no intersection
        }

        // Calculate intersection details
        int widthIntersection = Math.min(playerRect.x + playerRect.width, enemyRect.x + enemyRect.width) -
                Math.max(playerRect.x, enemyRect.x);
        int heightIntersection = Math.min(playerRect.y + playerRect.height, enemyRect.y + enemyRect.height) -
                Math.max(playerRect.y, enemyRect.y);

        // Check the nature of the intersection
        if (widthIntersection > heightIntersection) {
            // This is a top or bottom collision, which we do not want to count as harmful side collision
            return false;
        }

        // At this point, it's either a side collision or a corner collision that involves the sides.
        // You can further refine this by checking the center points to infer the likely direction of impact
        // if necessary (e.g., distinguishing between side and corner impacts).

        return true;  // Confirm side collision as it passed the above filters
    }

    private boolean isTopCollision(Player player, Enemy enemy) {
        Rectangle playerRect = player.getBounds();
        Rectangle enemyRect = enemy.getBounds();

        // Check if there is any intersection
        if (!playerRect.intersects(enemyRect)) {
            System.out.println("No intersection detected.");
            return false; // No collision if there is no intersection
        }

        // Check if the player is moving downward
        boolean isMovingDownward = player.getDy() > 0; // Assuming positive dy indicates downward movement
        System.out.println("Player Y: " + playerRect.y + ", Enemy Y: " + enemyRect.y);
        System.out.println("Player is moving downward: " + isMovingDownward);

        // Determine if the collision is at the top of the enemy
        if (isMovingDownward) {
            int playerBottom = playerRect.y + playerRect.height;
            int enemyTop = enemyRect.y;

            // Debugging output
            System.out.println("Player Bottom: " + playerBottom + ", Enemy Top: " + enemyTop);

            // Check if the player's bottom edge is within a small threshold of the enemy's top edge
            if (playerBottom >= enemyTop && playerBottom <= enemyTop + 5) {
                System.out.println("Top collision detected!");
                return true; // Collision detected at the top of the enemy
            }
        }

        System.out.println("No top collision detected.");
        return false; // No top collision detected
    }




}
