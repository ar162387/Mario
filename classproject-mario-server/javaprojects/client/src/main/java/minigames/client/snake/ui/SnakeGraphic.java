package minigames.client.snake.ui;

import minigames.client.snake.util.Observer;

import java.util.ArrayList;
import java.util.List;

public class SnakeGraphic  {
    private final int[] x, y;
    private int bodyParts;
    private char direction;
    private final int cellSize;
    private boolean hasMoved;

//     A list of observer objects that observe the SnakeGraphic for changes
//     These objects implement the Observer interface and are notified of any changes to the Snake's state i.e. its
//     movement
     private final List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a new SnakeGraphic object with the specified board width/height, cell size and inital number of body parts
     * the snake is initialised to move to the right starting from the top left hand corner of screen
     *
     * @param boardWidth The width of the game board in pixels
     * @param boardHeight The height of the game board in pixels
     * @param cellSize The size of each cell in pixels
     * @param bodyParts The initial number of body parts the snake has
     */
    public SnakeGraphic(int boardWidth, int boardHeight ,int cellSize, int bodyParts) {
        // Initialise snake state
        this.bodyParts = bodyParts;
        this.direction = 'R'; // Default direction
        this.cellSize = cellSize;
        this.x = new int[(boardWidth * boardHeight) / cellSize];
        this.y = new int[(boardWidth * boardHeight) / cellSize];

        // Initialise the snake's body position to start moving right
        for (int i = 0; i < this.bodyParts; i++) {
            x[i] = this.bodyParts * cellSize -i * cellSize; // Spread horizontally starting from 0
            y[i] = 0; // All segments have the same y coordinate starting from 0
        }


    }

     // Method to add an observer to the list
     public void addObserver(Observer observer) {
         observers.add(observer);
     }

     // Method to remove an observer from the list
     public void removeObserver(Observer observer) {
         observers.remove(observer);
     }

     // Method to notify all observers that the snake's state has changed
     private void notifyObservers() {
         // Iterating over each observer and calling their update() method
         for (Observer observer : observers) {
             observer.update();
         }
     }

    /**
     * Method to move the snake in the current direction
     *
     * After moving this method notifies all the observers of the change
     */
    public void move() {
        for (int i = bodyParts - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (this.getDirection()) {
            case 'U':
                y[0] -= cellSize;
                break;
            case 'D':
                y[0] += cellSize;
                break;
            case 'L':
                x[0] -= cellSize;
                break;
            case 'R':
                x[0] += cellSize;
                break;
        }
        hasMoved = true;

         notifyObservers(); // After moving, notify all observers of the change.
    }

    /**
     * Increases the size of the sanek by adding one body part
     */
    public void grow() {
    bodyParts++;
    // Initialise the new segment at the same psition as the last segment
    // Copy the last segments position to the new segment
    x[bodyParts - 1] = x[bodyParts - 2];
    y[bodyParts - 1] = y[bodyParts - 2];
}

    /**
     * Sets the direction for the snake's movement.
     * Direction can only be changed if the snake has already moved.
     * The new direction must not be opposite to the current direction
     * @param newDirection The new direction for the snake(U for up, D for down, R for right, L for let.
     */
    public void setDirection(char newDirection) {
        if (hasMoved) {
            if (direction == 'U' && newDirection != 'D' ||
                    direction == 'D' && newDirection != 'U' ||
                    direction == 'L' && newDirection != 'R' ||
                    direction == 'R' && newDirection != 'L') {
                direction = newDirection;
            }
        }
        hasMoved = false;
    }

    /**
     * Returns the x-coordinate of the specified index in the array of x-coordinates.
     * @param index of the x-coordinate to retrieve
     * @return x-cordinate at specified index
     */
    public int getX(int index) { return x[index]; }

    /**
     * Returns the y-coordinate of the specified index in the array of y-coordinates.
     * @param index of the y-coordinate to retrieve
     * @return y-cordinate at specified index
     */
    public int getY(int index) { return y[index]; }

    /**
     * returns the number of body parts of the snak
     * @return the number of body parts as an int
     */
    public int getBodyParts() { return bodyParts; }

    /**
     * Returns the current direction of the snake.
     * @return the current direction as a char
     */
    public char getDirection() { return direction; }
}