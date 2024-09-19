package minigames.client.snake.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class SnakeKeyAdapter implements KeyListener {
    private final SnakeGraphic snake; //

    public SnakeKeyAdapter(SnakeGraphic snake) {
        this.snake = snake;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                snake.setDirection('L');
                break;
            case KeyEvent.VK_RIGHT:
                snake.setDirection('R');
                break;
            case KeyEvent.VK_UP:
                snake.setDirection('U');
                break;
            case KeyEvent.VK_DOWN:
                snake.setDirection('D');
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Optionally handle key releases
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Optionally handle key typing
    }
}
