package minigames.client.snake.ui;

import minigames.client.snake.util.ButtonCreator;

import javax.swing.*;
import java.awt.*;

/**
 * Game over menu in the Snake game.
 * This panel is displayed when the game ends and includes the final score and a button to return to the main menu.
 */
public class GameOverMenu extends JPanel {

    public final SnakeGame snakeGame;
    public Image backgroundImage;

    public GameOverMenu(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        this.backgroundImage = SnakeGame.snakeEndBGImg; // Use the loaded static image from SnakeGame
        initializeMenu();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image, scaled to fit the entire panel
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    /**
     * Initialises the menu by setting up its layout, score label, and button.
     * The buttons action listener closes the current window when clicked.
     */
    private void initializeMenu() {
        this.setLayout(null);

        JLabel scoreLabel = new JLabel("Score: " + snakeGame.applesEaten);
        Font ourFont = GameBoard.customFont;  // Get custom Font that was loaded in Gameboard
        scoreLabel.setFont(ourFont); // Set font using custom Font
        scoreLabel.setForeground(Color.RED);
        scoreLabel.setBounds(200, 200, 250, 50); // Centered the text
        this.add(scoreLabel);

        JButton backToMenuButton = ButtonCreator.createButton(
                "Back to Menu",      // Button text
                "#4682b4",               // Bluish Blackground colour
                "#6495ed",               // Lighter blue when hovered
                e -> {                   // Action listener implementation
                    // Get the top-level frame and close it
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GameOverMenu.this);
                    topFrame.dispose();

                    // Add any code to show the main menu or restart the game here

                }
        );
        backToMenuButton.setBounds(190, 270, 200, 50); // Centered the button
        this.add(backToMenuButton);
    }
}
