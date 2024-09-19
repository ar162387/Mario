/**
 * SnakeMainMenu.java
 *
 * This class implements the starting main menu interface for the Snake game.
 *
 */

package minigames.client.snake.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.io.IOException;

import minigames.client.snake.Sound;
import minigames.client.snake.util.ButtonCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.MinigameNetworkClient;
import minigames.client.snake.AchievementHandler;
import minigames.client.snake.ui.AboutMenu;

public class SnakeMainMenu {
    private CardLayout cardLayout; // Layout manager for switching between different menu panels
    private JPanel mainPanel; // Main panel that holds all the menu panels
    private Image backgroundImage; // Image used as the background for the main menu
    private static final Logger logger = LogManager.getLogger(SnakeMainMenu.class);
    private MinigameNetworkClient networkClient;

    // Placeholder for the player's name
    String player = "PlayerName";


    /**
     * Constructs a SnakeMainMenu instance.
     *
     * @param networkClient The network client instance.
     */
    public SnakeMainMenu(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
    }


    /**
     * Displays the main menu.
     */
    public void show() {
        logger.info("Displaying Snake main menu");

        // Create the main panel and add components
        JPanel mainPanel = createMainPanel();

        // Clear existing components and add the main panel to the center
        networkClient.getMainWindow().clearAll();
        networkClient.getMainWindow().addCenter(mainPanel);
        networkClient.getMainWindow().pack();
        logger.info("Main menu displayed");

        // play the Menu music
        Sound.getInstance().loop(Sound.Type.MENU);

        // Set up the game frame properties
        setupFrame();
    }

    /**
     * Sets up the game frame properties.
     */
    private void setupFrame() {
        JFrame mainFrame = networkClient.getMainWindow().getFrame();
        mainFrame.setTitle("Snake Game");
        mainFrame.setSize(800, 800);
        mainFrame.setResizable(false);

        // Get the current location of the frame
        int x = mainFrame.getLocation().x;
        int y = mainFrame.getLocation().y;

        // Set the new location, 50px higher to avoid south edge running off bottom of screen
        mainFrame.setLocation(x, y - 50);
    }

    /**
     * Creates the main panel for the main menu.
     *
     * @return The main panel.
     */
    private JPanel createMainPanel() {
        //Is this the best way to get the image?
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/snake/snakeMain.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null; // Handle case where image isn't found or doesn't load
        }

        cardLayout = new CardLayout(); // Initialize the CardLayout
        mainPanel = new JPanel(cardLayout); // Initialize the main panel with the CardLayout

        // Create panels for different screens
        JPanel menuPanel = createMenuPanel();
        JPanel helpPanel = createAboutPanel();
        JPanel highScoresPanel = createHighScoresPanel();
        JPanel achievementsPanel = createAchievementsPanel();

        // Add panels to main panel
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(helpPanel, "About");
        mainPanel.add(highScoresPanel, "High Scores");
        mainPanel.add(achievementsPanel, "Achievements");

        cardLayout.show(mainPanel, "Menu");
        return mainPanel;
    }


    /**
     * Creates a panel containing buttons for navigating the main menu.
     *
     * This panel includes buttons for "Play", "About", "High Scores", and "Achievements".
     * Each button is configured to switch to the corresponding view in the card layout
     * when clicked.
     *
     * @return The panel with navigation buttons.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();


        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 25, 800, 60);

        String buttonColour = "#9932CC";
        String hoverColour ="#c061ed";

        JButton playButton = ButtonCreator.createButton("Play",buttonColour,hoverColour,e -> startGame());
        JButton helpButton = ButtonCreator.createButton("About", buttonColour,hoverColour,  e -> cardLayout.show(mainPanel, "About"));
        JButton highScoresButton = ButtonCreator.createButton("High Scores", buttonColour, hoverColour, e -> cardLayout.show(mainPanel, "High Scores"));
    
        JButton achievementsButton = ButtonCreator.createButton("Achievements", buttonColour, hoverColour ,e -> {
            // Remove the old achievements panel from the CardLayout
            mainPanel.remove(mainPanel.getComponent(mainPanel.getComponentCount() - 1));
    
            // Dynamically recreate the Achievements panel each time the button is pressed
            JPanel achievementsPanel = createAchievementsPanel();
    
            // Add the newly created achievements panel to the CardLayout
            mainPanel.add(achievementsPanel, "Achievements");
    
            // Switch to the new Achievements panel
            cardLayout.show(mainPanel, "Achievements");
            
            // Revalidate and repaint the mainPanel to ensure proper UI updates
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        // Add buttons to the panel
        buttonPanel.add(helpButton);
        buttonPanel.add(playButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(achievementsButton);
    
        return buttonPanel;
    }
    
    /**
     * Creates the menu panel for the game
     * @return the menu panel with background and buttons
     */
    private JPanel createMenuPanel() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel buttonPanel = createButtonPanel();
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);

        return panel;
    }



    /**
     * Creates the about panel for the game screen
     * @return a JPanel representing the About panel
     */
    private JPanel createAboutPanel() {

        AboutMenu aboutMenu = new AboutMenu(e -> cardLayout.show(mainPanel, "Menu"));
        return aboutMenu.getAboutPanel();
    }

    /**
     * Creates the High Scores panel for the game screen
     * @return The High Scores panel with a placeholder label and back button
     */
    private JPanel createHighScoresPanel() {
        JPanel panel = new JPanel();
        // panel.setBackground(new Color(204, 153, 255)); // Light purple
        JLabel label = new JLabel("High Scores (To be implemented)");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        JButton backButton = ButtonCreator.createButton("Back to Menu ","#4287f5","#4287f5", e -> cardLayout.show(mainPanel, "Menu") );
        panel.add(backButton);

        return panel;
    }

    /**
     * Creates the Achievements panel for the game screen
     * @return The Achievements panel with a placeholder label and back button
     */
    private JPanel createAchievementsPanel() {
        // Load background image
        Image backgroundImage = SnakeGame.achivementScreenImg;

        // Create a custom JPanel with the background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image, scaled to fit the entire panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight() - 150, this);
            }
        };

        panel.setLayout(null); // Absolute positioning

        // Add back to menu button
        JButton backButton = ButtonCreator.createButton("Back to Menu","#4287f5" , "#6495ed", e -> cardLayout.show(mainPanel, "Menu"));
        backButton.setBounds(300, 600, 200, 50); // Position the button
        panel.add(backButton);

        // Create an instance of AchievementHandler
        AchievementHandler achievementHandler = new AchievementHandler(networkClient, player, null);

        // Call awardAchievement3 to get the achievements panel
        // TODO: replace hardcoded jane_smith with current logged in player
        JPanel achievementsPanel = achievementHandler.getPlayerAchievements("jane_smith", "Snake");

        // Wrap the achievements panel in a JScrollPane to make it scrollable
        JScrollPane scrollPane = new JScrollPane(achievementsPanel);
        scrollPane.setBounds(194, 145, 415, 329); // Set appropriate bounds for the scroll pane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Adjust as needed

        // Add the scroll pane to the main panel
        panel.add(scrollPane);

        return panel;
    }

    /**
     * Starts a new game. This method is called when the start button is clicked.
     */
    public void startGame() {
        logger.info("Starting a new game");



        int boardWidth = 600;
        int boardHeight = 600;

        JFrame frame = new JFrame("Snake Game");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set location of the frame on the screen
        frame.setLocation(100, 100); 
        try {
            SnakeGame snakeGame = new SnakeGame(networkClient, player);  // Handle IOException here
            frame.add(snakeGame);
            frame.pack();
            snakeGame.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();  // Log or handle the exception here
            JOptionPane.showMessageDialog(frame, "Failed to start the game due to an error loading resources.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }


}

