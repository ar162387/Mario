package minigames.client.mario;

import minigames.client.MinigameNetworkClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class GameOver {

    private MinigameNetworkClient networkClient;
    private JPanel gameOverPanel;
    private JLabel gameOverLabel;
    private int oscillationDirection = 1; // 1 for right, -1 for left
    private Timer animationTimer;

    /**
     * Constructs a GameOver instance.
     *
     * @param networkClient The network client instance.
     */
    public GameOver(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
        this.gameOverPanel = createGameOverPanel();
    }

    /**
     * Displays the Game Over screen.
     */
    public void display() {
        networkClient.getMainWindow().clearAll();
        networkClient.getMainWindow().addCenter(gameOverPanel);
        networkClient.getMainWindow().pack();

        // Start oscillation animation
        startOscillation();
    }

    /**
     * Creates the game over panel with a red background and buttons.
     *
     * @return The game over JPanel.
     */


    private JPanel createGameOverPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.RED);
        panel.setPreferredSize(new Dimension(1250, 725));

        // Load pixelated font
        Font pixelatedFont = loadPixelFont("/fonts/PressStart2P-Regular.ttf", 52f);

        // Create Game Over label with the pixelated font
        gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(pixelatedFont != null ? pixelatedFont : new Font("Serif", Font.BOLD, 72));
        gameOverLabel.setForeground(Color.BLACK);
        gameOverLabel.setBounds(400, 100, 600, 100);
        panel.add(gameOverLabel);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(500, 300, 250, 200); // Position buttons below the Game Over label

        // Restart Button
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Serif", Font.BOLD, 24));
        restartButton.setBackground(new Color(0, 51, 204));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(evt -> restartGame());
        buttonPanel.add(restartButton);

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Serif", Font.BOLD, 24));
        mainMenuButton.setBackground(new Color(230, 37, 53));
        mainMenuButton.setForeground(Color.WHITE);
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.addActionListener(evt -> returnToMainMenu());
        buttonPanel.add(mainMenuButton);

        panel.add(buttonPanel);

        // Load and display the mario-over.png image
        try {
            BufferedImage marioOverImage = ImageIO.read(getClass().getResourceAsStream("/images/mario/mario-over.png"));
            JLabel imageLabel = new JLabel(new ImageIcon(marioOverImage));
            imageLabel.setBounds(475, 450, marioOverImage.getWidth(), marioOverImage.getHeight());
            panel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return panel;
    }


    /**
     * Loads a custom pixelated font from the resources folder.
     *
     * @param fontPath The path to the font file.
     * @param fontSize The size of the font.
     * @return The custom font or null if loading fails.
     */
    private Font loadPixelFont(String fontPath, float fontSize) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(fontPath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(fontSize);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Starts the oscillation animation for the "Game Over" label.
     */
    private void startOscillation() {
        animationTimer = new Timer(1, new ActionListener() { // Faster oscillation: reduced delay from 20 to 10 ms
            int xPos = 350; // Initial X position


            @Override
            public void actionPerformed(ActionEvent e) {
                xPos += oscillationDirection * 3; // Increase the speed by changing the increment value

                if (xPos >= 650 || xPos <= 200) {
                    oscillationDirection = -oscillationDirection; // Reverse direction
                }

                gameOverLabel.setLocation(xPos, gameOverLabel.getY());
            }
        });
        animationTimer.start();
    }

    /**
     * Restarts the game by reinitializing the game instance.
     */
    private void restartGame() {
        animationTimer.stop();
        networkClient.newGame("Mario", "");
    }

    /**
     * Returns to the main menu screen.
     */
    private void returnToMainMenu() {
        animationTimer.stop();
        MarioMainMenu mainMenu = new MarioMainMenu(networkClient);
        mainMenu.display();
    }
}
