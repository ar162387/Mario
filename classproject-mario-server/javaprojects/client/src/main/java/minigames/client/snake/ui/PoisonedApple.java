package minigames.client.snake.ui;

import java.awt.Image;

public class PoisonedApple extends Fruit {


    public PoisonedApple(int x, int y) {
        super(x, y);
    }

    @Override
    public void applyEffect(SnakeGraphic snake, SnakeGame game) {
        if (!isEaten()) {
            game.gameOver();
            setEaten(true);
        }
    }

    @Override
    public Image getImage() {
        return GameBoard.getFruitImage("poisonApple");
    }
}
