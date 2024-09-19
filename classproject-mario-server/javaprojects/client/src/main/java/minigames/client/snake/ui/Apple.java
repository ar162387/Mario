package minigames.client.snake.ui;

import java.awt.Image;

public class Apple extends Fruit {

    public Apple(int x, int y) {
        super(x, y);
    }

    @Override
    public void applyEffect(SnakeGraphic snake, SnakeGame game) {
        if (!isEaten()) {
            snake.grow();
            game.applesEaten++;
            setEaten(true);
        }
    }

    @Override
    public Image getImage() {
        return GameBoard.getFruitImage("apple");
    }
}
