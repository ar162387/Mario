package minigames.client.deepfried.UI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import minigames.client.deepfried.entities.Entity;
import minigames.client.deepfried.entities.Player;


public class GamePlayUI extends JPanel{
    
    public static JLabel playerImage;
    public static JLabel fryerImage1;
    public static JLabel fryerImage2;
    public static JLabel fryerImage3;
    public static JLabel fryerImage4;    
    Image kitchenImage;
    JPanel mainPanel;
    //private PauseMenuUI pauseMenuUI;
    private JButton resumeButton;
    private JButton restartButton;
    private JButton exitToMenuButton;

    public Player player;
    public ArrayList<Entity> entities = new ArrayList<>(); // for storing all entities in the game
    private JPanel gamePlayPanel;
    private JPanel pausePanel;
    private CardLayout cardLayout;

    public GamePlayUI(ArrayList<Entity> entities) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);  // Set this panel's layout to CardLayout

        gamePlayPanel = initialiseGamePlayUI(entities);
        pausePanel = initialisePauseMenuPanel();

        add(gamePlayPanel, "gamePanel");
        add(pausePanel, "pausePanel");

        // default show gameplay window
        showGamePanel();
    }

    /**
     * UI for the gameplay screen
     * including kitchen and player
     */
    public JPanel initialiseGamePlayUI(ArrayList<Entity> entities) {
        final int SCREEN_WIDTH = 1140;
        final int SCREEN_HEIGHT = 680;

        //null layout used to allow the chef to go on top of the kitchen
        gamePlayPanel = new JPanel(null);
        gamePlayPanel.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        gamePlayPanel.setBackground(Color.CYAN);

        JLabel kitchen = loadKitchenImage("/deepFried/kitchen.png");
        kitchen.setBounds(0,0,1140,680);
        gamePlayPanel.add(kitchen);

        // loop through entities listed in the initialiseEntity() method
        for (int i=0; i<entities.size(); i++) {
            Entity entity = entities.get(i);
            addEntityToPanel(gamePlayPanel, entity);
        }

        gamePlayPanel.setFocusable(true);
        gamePlayPanel.requestFocusInWindow();

        return gamePlayPanel;
    }

    public JPanel initialisePauseMenuPanel() {
        // Pause menu panel setup
        JPanel pauseMenu = new JPanel(new GridBagLayout());
        pauseMenu.setMinimumSize(new Dimension(1400, 900));
        pauseMenu.setBackground(Color.decode("#97faff"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        // Menu title
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        JLabel menuTitle = new JLabel("Game Paused");
        menuTitle.setFont(new Font("Helvetica", Font.BOLD, 40));
        menuTitle.setBackground(Color.decode("#97ffaf"));
        menuTitle.setForeground(Color.decode("#f09c4a"));
        pauseMenu.add(menuTitle, constraints);

        // Resume button
        constraints.ipadx = 50;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 10, 10, 10);
        resumeButton = createJButton("Resume Game", "Helvetica", 25, "#808080", "#ffffff");
        pauseMenu.add(resumeButton, constraints);

        // Restart button
        constraints.ipadx = 50;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 10, 10, 10);
        restartButton = createJButton("Restart Game", "Helvetica", 25, "#808080", "#ffffff");
        pauseMenu.add(restartButton, constraints);

        // Exit to main menu button
        constraints.ipadx = 50;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 10, 10, 10);
        exitToMenuButton = createJButton("Exit to Main Menu", "Helvetica", 25, "#808080", "#ffffff");
        pauseMenu.add(exitToMenuButton, constraints);
        
        return pauseMenu;
    }

    private void addEntityToPanel(JPanel gamePlayPanel, Entity entity) {
        try {
            JLabel entityImage = entity.getEntityImage(); // Get the image from the entity
            // Check if the entity image is null
            if (entityImage != null) {
                entityImage.setBounds(entity.getX(), entity.getY(), entityImage.getPreferredSize().width, entityImage.getPreferredSize().height);

                // Add the image to the game panel
                gamePlayPanel.add(entityImage);
                gamePlayPanel.setComponentZOrder(entityImage, 0); // Ensurethe entitiy is the highest object on the Panel
            } else {
                System.err.println("Warning: Entity " + entity.toString() + " has a null image.");
            }
        } catch (Exception e) {
            // Log any exceptions that occur
            System.err.println("Error while adding entity to panel: " + entity.toString());
            e.getMessage(); // Print stack trace for debugging
        }
        
    }


    private JButton createJButton(String label, String fontName, int fontSize, String bgColour, String textColour) {
        JButton newButton = new JButton(label);
        newButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
        newButton.setBackground(Color.decode(bgColour));
        newButton.setForeground(Color.decode(textColour));

        return newButton;
    }
    //Load image of kitchen for game play UI and place in JLAbel
    // takes in the path of the image and returns the image
    private JLabel loadKitchenImage(String path){
        JLabel gamePlayImage = null;
        try {
            BufferedImage kitchenImage = ImageIO.read(GamePlayUI.class.getResource(path));
            ImageIcon imageIcon = new ImageIcon(kitchenImage);

            gamePlayImage = new JLabel(imageIcon);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gamePlayImage;
    }

    public JButton getResumeButton() {
        return resumeButton;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public JButton getExitButton() {
        return exitToMenuButton;
    }

    public void showGamePanel() {
        cardLayout.show(this, "gamePanel");
    }
    public void showPausePanel() {
        cardLayout.show(this, "pausePanel");
    }

}

