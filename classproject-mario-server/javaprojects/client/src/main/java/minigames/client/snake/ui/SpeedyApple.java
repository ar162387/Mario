package minigames.client.snake.ui;

import java.awt.Image;

public class SpeedyApple extends Fruit {

    private SnakeGame snakeGame;

    public SpeedyApple(int x, int y, SnakeGame snakeGame) {
        super(x, y);
        this.snakeGame = snakeGame; // Assign the passed SnakeGame instance to the field
    }

    @Override
    public void applyEffect(SnakeGraphic snake, SnakeGame game) {
        if (!isEaten()) {
            snakeGame.setSpeedEffect("fast");
            snakeGame.setSpeedEffectMovesRemaining(15); // Set 15 moves for the speed effect
            setEaten(true);
        }
    }

    @Override
    public Image getImage() {
        return GameBoard.getFruitImage("speedyApple");
    }
}