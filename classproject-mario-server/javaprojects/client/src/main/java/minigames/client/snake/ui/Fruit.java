package minigames.client.snake.ui;

/**
 * Abstract base class representing a fruit
 * It has common attributes and methods for all fruit types
 */
public abstract class Fruit {

    protected int x;
    protected int y;
    protected boolean isEaten;

    public Fruit(int x, int y) {
        this.x = x;
        this.y = y;
        this.isEaten = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public void setEaten(boolean isEaten) {
        this.isEaten = isEaten;
    }

    public abstract void applyEffect(SnakeGraphic snake, SnakeGame game);


    public abstract java.awt.Image getImage();
}
