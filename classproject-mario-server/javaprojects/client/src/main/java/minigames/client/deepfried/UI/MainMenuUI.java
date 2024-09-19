package minigames.client.deepfried.UI;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Implements the main menu interface for the DeepFried minigame. 
 * Contains panels for the main screen, how to play screen, options screen, and buttons that link to them as well as
 * a button to start a new game.
 * 
 * Created by: Hotfix Heroes
 * 
 */

public class MainMenuUI extends JPanel{
    // Buttons
    private JButton startButton;
    private JButton tutorialButton;
    private JButton optionsButton;
    private JButton returnButton;
    private JButton newButton;

    // Panels
    private JPanel mainMenuPanel;
    private JPanel tutorialPanel;

    // CardLayout to switch between panels
    private CardLayout cardLayout;

    public MainMenuUI() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);  // Set this panel's layout to CardLayout

        // Initialize both panels
        tutorialPanel = initialiseTutorialPanel();
        mainMenuPanel = initialiseMainMenuPanel();
        

        // Add both panels to this panel managed by CardLayout
        add(tutorialPanel, "tutorial");
        add(mainMenuPanel, "mainMenu");
        

        // Show the main menu by default
        showMainMenu();
        //showTutorial();
        
    }

    private JPanel initialiseMainMenuPanel() {
                // Create buttons
                startButton = createJButton("Start New Game", "Helvetica", 20, "#f09c4a", "#FFFFFF");
                tutorialButton = createJButton("How to Play", "Helvetica", 20, "#f09c4a", "#FFFFFF");
                
                // Add the components to the overall panel using the grid bag layout manager and return it
                JPanel menuPanel = new JPanel(new GridBagLayout());
                menuPanel.setBounds(0, 0, 1400, 900);
                menuPanel.setBackground(Color.decode("#97FAFF"));
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.BOTH;
            
                // Get screen size to calculate padding
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int screenHeight = screenSize.height;
                int screenWidth = screenSize.width;
        
                // Calculate padding and apply it
                int paddingHeight = (int) (screenHeight * 0.02);
                int paddingWidth = (int) (screenWidth * 0.02);
                constraints.insets = new Insets(paddingHeight, paddingWidth, paddingHeight, paddingWidth);
        
                // Main menu image
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 2;
                constraints.gridheight = 1;
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.insets = new Insets(0, 10, 0, 10);
                JLabel mainMenuImage = loadChefImage("/deepFried/DeepFriedMenuBackground.png");
                menuPanel.add(mainMenuImage, constraints);
        
                // Start button
                constraints.ipadx = 250;
                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                constraints.anchor = GridBagConstraints.SOUTHWEST;
                constraints.insets = new Insets(10, 50, 10, 25);
                menuPanel.add(startButton, constraints);
        
                // Tutorial button
                constraints.ipadx = 1;
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                constraints.anchor = GridBagConstraints.SOUTH;
                constraints.insets = new Insets(10, 0, 10, 50);
                menuPanel.add(tutorialButton, constraints);

                return menuPanel;
    }

    /**
     * Method used to load the chef image used for the main menu.
     * @param path image path
     * @return a JLabel containing the image
     */
    private static JLabel loadChefImage(String path){
        JLabel mainMenuImage = null;
        try {
            BufferedImage chefImage = ImageIO.read(MainMenuUI.class.getResource(path));
            ImageIcon imageIcon = new ImageIcon(chefImage);

            mainMenuImage = new JLabel(imageIcon);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mainMenuImage;
    }

    /**
     * A method to create a new JButton
     * @param label a String containing the text to be displayed on the button
     * @param fontName the font used for the button text
     * @param fontSize the font size used for the button text
     * @param bgColour the background colour of the button (in RGB format)
     * @param textColour the colour of the button text (in RGB format)
     * @return a formatted JButton
     */
    public JButton createJButton(String label, String fontName, int fontSize, String bgColour, String textColour) {
        JButton newButton = new JButton(label);
        newButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
        newButton.setBackground(Color.decode(bgColour));
        newButton.setForeground(Color.decode(textColour));

        return newButton;
    }

    /**
     * A method to generate the tutorial screen. 
     * Contains info on how to play the game.
     * @return A JPanel containing tutorial info
     */
    private JPanel initialiseTutorialPanel() {
        // Create a panel to hold all of the components on the tutorial screen, and set up the layout manager
        JPanel tutorialPanel = new JPanel(new GridBagLayout());
        tutorialPanel.setBounds(0, 0, 1400, 950);
        tutorialPanel.setBackground(Color.decode("#97FAFF"));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        // Label for the page title
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        JLabel tutorialTitle = loadChefImage("/deepFried/TutorialTitle.png");
        tutorialTitle.setBackground(Color.decode("#97faff"));
        tutorialTitle.setMaximumSize(new Dimension(1400, 150));
        tutorialPanel.add(tutorialTitle, constraints);
         
        // Create panel to hold tutorial info
        JPanel tutorialInfoPanel = new JPanel(new GridBagLayout());
        tutorialInfoPanel.setBackground(Color.decode("#97faff"));
        tutorialInfoPanel.setMaximumSize(new Dimension(1300, 500));
        GridBagConstraints scrollConstraints = new GridBagConstraints();
        scrollConstraints.fill = GridBagConstraints.BOTH;

        // Controls section
        scrollConstraints.gridx = 0;
        scrollConstraints.gridy = 0;
        scrollConstraints.gridwidth = 1;
        scrollConstraints.gridheight = 1;
        scrollConstraints.anchor = GridBagConstraints.CENTER;
        scrollConstraints.insets = new Insets(10, 10, 10, 10);
        JLabel controls = loadChefImage("/deepFried/TutorialControls.png");
        controls.setBackground(Color.decode("#97faff"));
        tutorialInfoPanel.add(controls, scrollConstraints);

        // Tutorial steps image
        scrollConstraints.gridx = 0;
        scrollConstraints.gridy = 1;
        scrollConstraints.gridwidth = 1;
        scrollConstraints.gridheight = 1;
        scrollConstraints.anchor = GridBagConstraints.CENTER;
        scrollConstraints.insets = new Insets(10, 10, 10, 10);
        JLabel tutorialSteps = loadChefImage("/deepFried/TutorialSteps.png");
        tutorialSteps.setBackground(Color.decode("#97faff"));
        tutorialInfoPanel.add(tutorialSteps, scrollConstraints);

        // Text area for tutorial steps
        scrollConstraints.gridx = 0;
        scrollConstraints.gridy = 2;
        scrollConstraints.gridwidth = 1;
        scrollConstraints.gridheight = 1;
        scrollConstraints.anchor = GridBagConstraints.SOUTH;
        scrollConstraints.insets = new Insets(10, 0, 10, 0);
        JTextArea tutorialText = new JTextArea("1. Collect Ingredient \n2. Chop \n3. Fry \n4. Collect plate " + 
        "\n5. Plate cooked food \n6. Deliver to window to score!", 6, 10);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setPreferredSize(new Dimension(200, 150));
        tutorialText.setBackground(Color.decode("#bdfcff"));
        tutorialText.setFont(new Font("Segoe UI", Font.BOLD, 25));
        tutorialText.setEditable(false);
        tutorialInfoPanel.add(tutorialText, scrollConstraints);

        // Create scroll pane and add info section to it
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.insets = new Insets(10, 0, 10, 0);
        JScrollPane tutorialScrollPane = new JScrollPane();
        tutorialScrollPane.setPreferredSize(new Dimension(1300, 500));
        tutorialScrollPane.setViewportView(tutorialInfoPanel);
        tutorialScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tutorialScrollPane.setBackground(Color.decode("#97faff"));
        tutorialPanel.add(tutorialScrollPane, constraints);
        

        // Button to return to main menu
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.insets = new Insets(5, 125, 5, 125);
        returnButton = createJButton("Return to main menu", "Helvetica", 20, "#f09c4a", "#ffffff");
        tutorialPanel.add(returnButton, constraints);

        return tutorialPanel;

    }

    public void showMainMenu() {
        cardLayout.show(this, "mainMenu");
    }

    public void showTutorial() {
        cardLayout.show(this, "tutorial");
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getTutorialButton() {
        return tutorialButton;
    }

    public JButton getOptionsButton() {
        return optionsButton;
    }

    public JButton getReturnButton() {
        return returnButton;
    }


}
