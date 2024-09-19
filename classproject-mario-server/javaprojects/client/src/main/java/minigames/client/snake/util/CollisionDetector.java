package minigames.client.snake.util;

import java.util.List;

import minigames.client.snake.ui.Fruit;
import minigames.client.snake.ui.SnakeGraphic;

public class CollisionDetector {
    private final SnakeGraphic snake;
    private final int boardWidth;
    private final int gridHeight;
    private final int cellSize;

    public CollisionDetector(SnakeGraphic snake, int boardWidth, int gridHeight, int cellSize) {
        this.snake = snake;
        this.boardWidth = boardWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
    }

    /**
     * Checks whether the snake has collided with the wall.
     *
     * <p>This method determines if the snake's head has collided with any of the walls
     * surrounding the game board. It checks if the coordinates of the snake's head
     * are outside the valid bounds of the game board. If the head's coordinates
     * are less than 0 or exceed the board's width or height, a collision is detected.
     *
     * @return {@code true} if the snake's head has collided with the wall;
     *         {@code false} otherwise.
     */
    public boolean checkWallCollision() {

        int headX = snake.getX(0);
        int headY = snake.getY(0);

        return headX < 0 || headX >= boardWidth-cellSize || headY < 0 || headY >= cellSize * gridHeight;
    }

    /**
     * Checks whether the snake has collided with itself.
     *
     * This method determines if the snake's head has collided with any part of its body.
     * It compares the coordinates of the snake's head with each segment of the snake's body.
     * If a collision is detected, the method prints a message indicating the coordinates
     * where the collision occurred and returns true. If no collision is detected,
     * the method returns false.
     *
     * @return True if the snake's head has collided with any part of its body;
     *         False otherwise.
     */
    public boolean checkSelfCollision() {

        int headX = snake.getX(0);
        int headY = snake.getY(0);

        for (int i = 1; i < snake.getBodyParts(); i++) {
            if (headX == snake.getX(i) && headY == snake.getY(i)) {
                System.out.println("Self collision detected at: X=" + headX + " Y=" + headY); //TBD
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the snake's head has collided with the given fruit.
     *
     * This method compares the coordinates of the snake's head with the
     * coordinates of the specified fruit to determine if a collision has occurred.
     * If the fruit is null, the method returns false, indicating no collision.
     *
     * @param fruits the list of fruits object to check for collision with the snake's head.
     *              If null, the method will return false.
     * @return true if the snake's head coordinates match the fruit's coordinates,
     *         indicating a collision; false otherwise.
     */
    public Fruit checkFruitCollision(List<Fruit> fruits) {
        int headX = snake.getX(0);
        int headY = snake.getY(0);
    
        for (Fruit fruit : fruits) {
            if (fruit != null && headX == fruit.getX() && headY == fruit.getY()) {
                System.out.println("Snake has eaten the fruit " + fruit);
                return fruit; // Return the collided fruit
            }
        }
    
        return null; // No collision
    }
}