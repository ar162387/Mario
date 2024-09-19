package minigames.client.snake.ui;

import java.awt.*;

public class SlowApple extends Fruit {

    private SnakeGame snakeGame;

    public SlowApple(int x, int y, SnakeGame snakeGame) {
        super(x, y);
        this.snakeGame = snakeGame; // Assign the passed SnakeGame instance to the field
    }


    @Override
    public void applyEffect(SnakeGraphic snake, SnakeGame game) {
        if (!isEaten()) {
            snakeGame.setSpeedEffect("slow");
            snakeGame.setSpeedEffectMovesRemaining(15); // Set 15 moves for the speed effect
            setEaten(true);
        }
    }

    @Override
    public Image getImage() {
        return GameBoard.getFruitImage("slowApple");
    }
}
