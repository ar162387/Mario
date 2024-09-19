package minigames.client.mario;

import minigames.client.MinigameNetworkClient;
import minigames.client.MinigameNetworkClientWindow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


/**
 * Class used to create a main menu screen for Mario game.
 * Uses panels and a cardLayout to switch between different menu information.
 *
 * @return a main menu JPanel holding different menu panels.
*/
public class MarioMainMenu {
	
    private static final Logger logger = LogManager.getLogger(Menu.class);
    
    private MinigameNetworkClient networkClient;	
    private CardLayout cardLayout; 
    private JPanel mainPanel; 
    private Image backgroundImage; 
    
    String player = "";
    
    /**
     * Constructs a MarioMainMenu instance.
     *
     * @param networkClient The network client instance.
     */
    public MarioMainMenu(MinigameNetworkClient networkClient) {
        this.networkClient = networkClient;
    }
    
    /**
     * Displays the main menu
     * Method called via MinigameNetworkClientWindow
     * When a new Mario game is selected
     */
    public void display() {
        logger.info("Displaying Mario main menu______");

        // Create main panel and add components
        JPanel mainPanel = createMainPanel();

        // Clear existing components then add the main panel
        logger.info("______main menu displayed");
        networkClient.getMainWindow().clearAll();
        networkClient.getMainWindow().addCenter(mainPanel);
        networkClient.getMainWindow().pack();
        
        // Method used to set up the frame properties
        setupFrame();
        
    }
    
    /**
     * Sets up the frame properties.
     * Method called when needed to display a new game.
     */
    private void setupFrame() {
    	
        JFrame mainFrame = networkClient.getMainWindow().getFrame();
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    /**
     * Creates the main panel for the main menu.
     *
     * @return The main panel.
     */
    private JPanel createMainPanel() {
    	
    	  // Initialise card layout and main panel.
    	  cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
    	
        try {
        	   // image taken from https://www.clipartmax.com/download/m2i8K9A0N4Z5i8m2_detail-clip-art-super-mario-and-luigi/#
        	   // used here for demo purposes. ideally create and display our own characters for a production purpose.
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/mario/mario-bros.jpg"));
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        // Create the different screen panels.
        JPanel menuPanel = createMenuPanel();
        JPanel howToPanel = createHowToPanel();
        JPanel achievementsPanel = createAchievementsPanel();

        // Add screen panels to main panel.
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(howToPanel, "How To Play");
        mainPanel.add(achievementsPanel, "Achievements");

        cardLayout.show(mainPanel, "Menu");
        return mainPanel;
    }
    
    /**
     * Creates a button panel which is used to navigate through the menu.
     *
     * Buttons will switch to corresponding view when clicked.
     *
     * @return A JPanel with navigation buttons.
     */
    private JPanel createButtonPanel() {
    	
    	  // Create panel and set layout.
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 25, 800, 60);

        // Create a menu button for title screen.
        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(evt -> cardLayout.show(mainPanel, "Menu"));
        menuButton.setFont(new Font("Serif",Font.BOLD,15));
        menuButton.setBackground(Color.WHITE);
        menuButton.setForeground(Color.RED);
        menuButton.setFocusPainted(false);
        menuButton.setBorderPainted(true);
        
        // Create a play game button to start the game on the server.
        JButton playButton = new JButton("PLAY GAME");
        playButton.setFont(new Font("Serif",Font.BOLD,25));
        playButton.setBackground(new Color(0,51,204));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(true);
        playButton.addActionListener(evt -> {
                      networkClient.newGame("Mario", player);
        });

        // Create a how to play button to switch to an information panel.
        JButton howToButton = new JButton("How to Play");
        howToButton.setFont(new Font("Serif",Font.BOLD,20));
        howToButton.setBackground(new Color(230,37,53));//import java.awt.Color;
        howToButton.setForeground(Color.WHITE);
        howToButton.setFocusPainted(false);
        howToButton.setBorderPainted(true);
        howToButton.addActionListener(evt -> cardLayout.show(mainPanel, "How To Play"));

        // Create an achievements button to switch to an achievements panel.        
        JButton achievementsButton = new JButton("Achievements");
        achievementsButton.setFont(new Font("Serif",Font.BOLD,20));
        achievementsButton.setBackground(new Color(14,188,49));//import java.awt.Color;
        achievementsButton.setForeground(Color.WHITE);
        achievementsButton.setFocusPainted(false);
        achievementsButton.setBorderPainted(true);
        achievementsButton.addActionListener(evt -> cardLayout.show(mainPanel, "Achievements"));
    
        // Add buttons to the panel
        buttonPanel.add(howToButton);
        buttonPanel.add(playButton);
        buttonPanel.add(achievementsButton);
        buttonPanel.add(menuButton);
    
        return buttonPanel;
    }
    
    /**
     * Creates the  main menu panel.
     *
     * @return the menu JPanel with a background and buttons
     */
    private JPanel createMenuPanel() {
    	
    	  // Add a Layered pane with background and navigation buttons.
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
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(
                        backgroundImage.getWidth(null),
                        backgroundImage.getHeight(null));
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel buttonPanel = createButtonPanel();
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the how to play panel.
     *
     * @return a JPanel with instructions and navigation buttons.
     */
    private JPanel createHowToPanel() {

        // Add a layered pane with text instructions and buttons.
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));
        
        JTextArea instructions = new JTextArea("\n\n\n                    --------------------  How to Play  --------------------\n\n\n" +
                                                        "                         to do\n\n" +
                                                        "          ........... finilise with final controls and game instructions\n\n" +
                                                        "              ............ make prettier");
        instructions.setEditable(false);
        instructions.setPreferredSize(new Dimension(800, 800));
        instructions.setForeground(Color.RED);
        instructions.setBackground(Color.BLACK);
        instructions.setFont(new Font("Verdana", Font.PLAIN, 26));
    
        JPanel buttonPanel = createButtonPanel();
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.SOUTH);  
        
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.add(instructions);
        
        backgroundPanel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);  
        
        return panel;
    }

    /**
     * Creates an Achievements panel.
     *
     * @return The Achievements JPanel with achievements and navigation buttons.
     */
    private JPanel createAchievementsPanel() {

        // Add a layered pane with text achievements and buttons.
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));        
        
        JTextArea achievements = new JTextArea("\n\n\n                    --------------------  Achievements  --------------------\n\n\n" +
                                                        "                         to do\n\n" +
                                                        "          ........... add achievements\n\n" +
                                                        "              ............ make prettier");
        achievements.setEditable(false);
        achievements.setPreferredSize(new Dimension(800, 600));
        achievements.setForeground(Color.GREEN);
        achievements.setBackground(Color.BLACK);
        achievements.setFont(new Font("Verdana", Font.PLAIN, 26));

        JPanel buttonPanel = createButtonPanel();
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(layeredPane, BorderLayout.SOUTH);  
        
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.add(achievements);
        
        backgroundPanel.setBounds(0, 0, 800, 800);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);         
        
        return panel;
    }

    /**
     * getter for main panel.
     *
     * @return The main panel.
     */
    public JPanel getMainPanel() {
        return this.mainPanel;
    }


}

        
        