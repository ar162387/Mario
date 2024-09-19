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

public class YouWon {

    private MinigameNetworkClient networkClient;
    private JPanel youWonPanel;
    private JLabel thankYouLabel;
    private Timer animationTimer;

    /**
     * Constructs a YouWon instance.
     *
     * @param networkClient The network client instance.
     */
    public YouWon(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
        this.youWonPanel = createYouWonPanel();
    }

    /**
     * Displays the You Won screen.
     */
    public void display() {
        networkClient.getMainWindow().clearAll();
        networkClient.getMainWindow().addCenter(youWonPanel);
        networkClient.getMainWindow().pack();
    }

    /**
     * Creates the "You Won" panel with a background image, labels, and buttons.
     *
     * @return The "You Won" JPanel.
     */
    private JPanel createYouWonPanel() {
        // Create a custom JPanel with overridden paintComponent to draw the background image
        JPanel panel = new JPanel() {
            private BufferedImage wonBackgroundImage; // Store the background image

            {
                // Load the background image
                try {
                    wonBackgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/mario/won.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image to fill the entire panel
                if (wonBackgroundImage != null) {
                    g.drawImage(wonBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1250, 725));

        // Load pixelated font
        Font pixelatedFont = loadPixelFont("/fonts/PressStart2P-Regular.ttf", 36f);
        Font creditsFont = loadPixelFont("/fonts/PressStart2P-Regular.ttf", 10f);

        // Create "Thank You" label
        JLabel thankYouLabel = new JLabel("Thank You For Playing", SwingConstants.CENTER);
        thankYouLabel.setFont(pixelatedFont != null ? pixelatedFont : new Font("Serif", Font.BOLD, 52));
        thankYouLabel.setForeground(Color.RED);
        thankYouLabel.setBounds(200, 100, 850, 100);
        panel.add(thankYouLabel);

        // Create credits label
        JLabel creditsLabel = new JLabel("Credits:\n Naba Zaheer Khan \n Chelsea Allen \n Max Thorton \n Chris Patt \n Genara", SwingConstants.CENTER);
        creditsLabel.setFont(creditsFont != null ? creditsFont : new Font("Serif", Font.PLAIN, 20));
        creditsLabel.setForeground(Color.BLACK);
        creditsLabel.setBounds(100, 250, 1000, 200);
        panel.add(creditsLabel);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(950, 500, 250, 150); // Position buttons at the bottom right

        // Restart Button
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Serif", Font.BOLD, 24));
        restartButton.setBackground(new Color(0, 153, 51));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(evt -> restartGame());
        buttonPanel.add(restartButton);

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Serif", Font.BOLD, 24));
        mainMenuButton.setBackground(new Color(255, 102, 0));
        mainMenuButton.setForeground(Color.WHITE);
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.addActionListener(evt -> returnToMainMenu());
        buttonPanel.add(mainMenuButton);

        panel.add(buttonPanel);

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
            if (fontStream == null) {
                System.err.println("Font not found at path: " + fontPath);
                return null;
            }
            return Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Restarts the game by reinitializing the game instance.
     */
    private void restartGame() {
        networkClient.newGame("Mario", "");
    }

    /**
     * Returns to the main menu screen.
     */
    private void returnToMainMenu() {
        MarioMainMenu mainMenu = new MarioMainMenu(networkClient);
        mainMenu.display();
    }
}
