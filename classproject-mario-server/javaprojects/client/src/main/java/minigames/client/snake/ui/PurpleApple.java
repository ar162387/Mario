package minigames.client.snake.ui;


import java.awt.Image;

public class PurpleApple extends Fruit {

    public PurpleApple(int x, int y) {
        super(x, y);
    }

    @Override
    public void applyEffect(SnakeGraphic snake, SnakeGame game) {
        if (!isEaten()) {
            //TODO add some code here with what we want purple to do ie add 5 points to the score
        }
    }


    @Override
    public Image getImage() {
        return GameBoard.getFruitImage("purpleApple");
    }
}