package minigames.client.bomberman;

import javafx.scene.Node;
import javafx.util.Pair;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implement strategy design pattern for interface, defines a strategy for moving an enemy on a board.
 *  Contributors: Lixang Li lli32@myune.edu.au
 * Implementations of this interface can provide various movement algorithms, such as:
 *  - Random strategy
 *  - TurnRight on every intersection else go straight else go left if dead end go back
 *  - Direct Pursuit
 */
public interface MovementStrategy {

    CollisionManager collisionManager = CollisionManager.getInstance();
    /**
     * Move enemy based on algorithm
     * @param enemy
     * @param board
     * @return Pair of doubles for where the enemy has chosen to move
     */

    Pair<Integer,Integer> move(Enemy enemy, Board board);

    // DEFAULT HELPER METHODS
    /**
     * Example Usage
     * Boolean checkCollision(Direction.RIGHT, board) t or f
     * Boolean checkCollision(relative(Direction.RIGHT,enemy),board)
     * @param direction
     * @param board
     * @return
     */
    default boolean checkCollision(Direction direction, Board board, Enemy enemy) {
        try {
            final int speed = (int) ((GameConstants.ENEMY_SIZE_REDUCTION-1) * enemy.getSpeed());// Move whole tile and check if okay


            return collisionManager.checkTileCollision(moveBounds(enemy,direction,speed), board.getLevel().getTileMap());
        }
        catch (ClassCastException e) {
            System.err.println(e.getMessage());
            return true;
        }
    }

    /**
     * Returns a new bounds of the enemy's hitbox moved in a cardinal direction by the specified speed.
     *
     * @param enemy The enemy whose hitbox is to be moved.
     * @param direction The direction in which to move the hitbox.
     * @param speed The speed by which to move the hitbox.
     * @return A new Rectangle representing the moved bounds of the enemy's hitbox.
     */
    default Node moveBounds(Enemy enemy, Direction direction, Integer speed) {
        Rectangle box = (Rectangle) enemy.getBounds(); // Assuming getBounds() returns a Rectangle
        double x = box.getX();
        double y = box.getY();
        double width = box.getWidth();
        double height = box.getHeight();

        switch (direction) {
            case UP -> y -= speed;
            case DOWN -> y += speed;
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
            default -> throw new IllegalArgumentException("Invalid direction");
        }

        return new Rectangle(x, y, width, height);
    }
    /**
     * Converts a Direction to a Pair representing movement in (dx, dy).
     *
     * @param direction The direction to convert.
     * @return A Pair<Integer, Integer> representing the movement vector.
     */
    default Pair<Integer, Integer> directionToPair(Direction direction) {
        return switch (direction) {
            case UP -> new Pair<>(0, -1);
            case DOWN -> new Pair<>(0, 1);
            case LEFT -> new Pair<>(-1, 0);
            case RIGHT -> new Pair<>(1, 0);
        };
    }

    /**
     * Returns a random Direction
     *
     * @return A randomly selected Direction.
     */
    default Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        int randomIndex = new Random().nextInt(directions.length);
        return directions[randomIndex];
    }

    /**
     * Move in a random open direction to get unstuck
     * @param board
     * @param enemy
     * @return
     */
    default Pair<Integer,Integer> getUnstuck(Board board, Enemy enemy) {
        List<Direction> open = Arrays.stream(Direction.values()).filter(direction -> !checkCollision(direction,board,enemy)).toList();
        if (open.size() == 0 ) return new Pair<>(0,0); // Early return to prevent null
        Random random = new Random();
        int randomInt = random.nextInt(open.size());
        return directionToPair(open.get(randomInt));
    }
    /**
     * Enum representing relative directions from the current heading of an enemy.
     * This enum provides a method to convert a relative direction into an absolute direction
     * based on the current direction of an enemy.
     */
    enum RelativeDirection {
        STRAIGHT, RIGHT, LEFT, BACK;

        /**
         * Determines the true direction based on the enemy's current direction and the relative direction.
         */
        public Direction getTrueDirection(Enemy enemy) {
            return switch (this) {
                case STRAIGHT -> enemy.getDirection();
                case RIGHT -> switch (enemy.getDirection()) {
                    case UP -> Direction.RIGHT;
                    case DOWN -> Direction.LEFT;
                    case LEFT -> Direction.UP;
                    case RIGHT -> Direction.DOWN;
                };
                case LEFT -> switch (enemy.getDirection()) {
                    case UP -> Direction.LEFT;
                    case DOWN -> Direction.RIGHT;
                    case LEFT -> Direction.DOWN;
                    case RIGHT -> Direction.UP;
                };
                case BACK -> switch (enemy.getDirection()) {
                    case UP -> Direction.DOWN;
                    case DOWN -> Direction.UP;
                    case LEFT -> Direction.RIGHT;
                    case RIGHT -> Direction.LEFT;
                };
            };
        }
    }
}


