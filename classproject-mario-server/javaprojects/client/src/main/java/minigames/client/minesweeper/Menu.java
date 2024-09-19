package minigames.client.minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.*;

import minigames.client.MinigameNetworkClient;
import minigames.client.snake.AchievementHandler;

/**
 * A menu that will initialize the Minesweeper game. The user can click start or exit,
 * or customize the settings to their preferences. This menu features buttons for
 * 'How To Play', 'Leaderboard', 'Restart', 'Exit', and 'Start', all centered and
 * of uniform width within their respective tabs.
 *
 * Author: Oren Marsh & Jake Mayled
 */
public class Menu extends JTabbedPane {
    private Minesweeper minesweeper;
    private userInterface userInterface;
    private GameController gameController;
    private MinigameNetworkClient networkClient;

    //background image variables
    private BufferedImage background;
    private static String dir = "/images/minesweeper/";
    private static String background_img = dir + "mainmenu-background.png";

    private Timer timer;
    AtomicInteger countdown = new AtomicInteger(4);
    String difficulty;


    public Menu(userInterface userInterface, Minesweeper minesweeper, GameController gameController) {
        this.userInterface = userInterface;
        this.minesweeper = minesweeper;
        this.gameController = gameController;

        //Loads in the background image
        try {
            background = ImageIO.read(getClass().getResource(background_img));
        } catch (IOException e) {
            System.out.println("Error loading background image. \nError message: " + e.getMessage());
        }


    }

    public void setNetworkClient(MinigameNetworkClient networkClient){
        this.networkClient = networkClient;
    }

    //-----------------Main menu code-----------------//
    /**
     * This creates the main panel with a card layout
     * @param gameController
     * @return
     */
    public JPanel createMainMenuPanel(GameController gameController) {
        //Creates the main panel
        JPanel mainPanel = new JPanel(new CardLayout());

        //Gets other panels
        JPanel mainMenuPanel = mainMenuPanel(mainPanel);
        JPanel playPanel = playPanel(mainPanel);
        JPanel newOrLoadGamePanel = newOrLoadGamePanel(mainPanel);
        JPanel leaderboardPanel = leaderboardPanel(mainPanel);
        JPanel gameLoadingPanel = gameLoadingPanel(mainPanel);

        //Adds panels to the mainPanel
        mainPanel.add(mainMenuPanel, "main menu");
        mainPanel.add(playPanel, "Play");
        mainPanel.add(newOrLoadGamePanel, "New/Load Game");
        mainPanel.add(gameLoadingPanel, "game loading");
        mainPanel.add(leaderboardPanel, "leaderboard");

        CardLayout card = (CardLayout) (mainPanel.getLayout());
        card.show(mainPanel, "main menu");

        //returns panel
        return mainPanel;
    }

    /**
     * Creates a menu panel that contains the title and the buttonPanel
     * @return
     */
    private JPanel mainMenuPanel(JPanel cardPanel){
        //Creates a panel that has a background
        JPanel mainMenuPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imageWidth = background.getWidth();
                    int imageHeight = background.getHeight();

                    // Calculate the scaling factor to maintain the aspect ratio
                    double aspectRatio = (double) imageWidth / imageHeight;
                    int newWidth = panelWidth;
                    int newHeight = (int) (panelWidth / aspectRatio);

                    if (newHeight > panelHeight) {
                        newHeight = panelHeight;
                        newWidth = (int) (panelHeight * aspectRatio);
                    }

                    // Calculate the top-left corner for centering the image
                    int x = (panelWidth - newWidth) / 2;
                    int y = (panelHeight - newHeight) / 2;

                    // Draw the image with the calculated dimensions
                    g.drawImage(background, x, y, newWidth, newHeight, this);
                }
            }
        };
        mainMenuPanel.setPreferredSize(new Dimension(800, 600));

        // Get the button panel
        JPanel buttonPanel = buttonPanel(cardPanel);
        // Add the button panel to the left side of the main menu panel
        mainMenuPanel.add(buttonPanel, BorderLayout.WEST);

        //Creates a label to display the title
        JLabel label = new JLabel("Minesweeper", SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // Set font color to white
        // Get the current font of the label
        Font currentFont = label.getFont();
        // Create a new font with the same font family and style, but with a different size
        Font newFont = currentFont.deriveFont(45f); // Set the font size to 45
        // Apply the new font to the label
        label.setFont(newFont);
        // Sets preferred size of label
        label.setPreferredSize(new Dimension(200, 150));
        //Adds label to the panel
        mainMenuPanel.add(label, BorderLayout.NORTH);

        return mainMenuPanel;
    }
    /**
     * Creates a panel that has a few buttons in it, 'Play', 'How to Play', 'Leaderboard', 'Exit'
     * @return
     */
    JPanel buttonPanel(JPanel cardPanel){
        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Create the buttons
        JButton playButton = new JButton("Play");
        JButton howToPlayButton = new JButton("How to Play");
        JButton leaderboardButton = new JButton("Leaderboard");
        JButton exitButton = new JButton("Exit");

        // Create Dev Mode button
        JButton devButton = new JButton("Dev Mode");

        // Create Achievements button
        JButton achieveButton = new JButton("Achievements");
        
        // Set preferred size for buttons to make them the same width
        Dimension buttonSize = new Dimension(150, 40);
        playButton.setMaximumSize(buttonSize);
        howToPlayButton.setMaximumSize(buttonSize);
        leaderboardButton.setMaximumSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);
        devButton.setMaximumSize(buttonSize);
        achieveButton.setMaximumSize(buttonSize);

        // Set a larger font size for the button text
        Font buttonFont = new Font(playButton.getFont().getName(), Font.PLAIN, 20);
        playButton.setFont(buttonFont);
        howToPlayButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        devButton.setFont(buttonFont);
        achieveButton.setFont(buttonFont);

        // Add action listeners to the buttons
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout card = (CardLayout) (cardPanel.getLayout());
                card.show(cardPanel, "New/Load Game");
            }
        });

        howToPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHowToPlay();
                //CardLayout card = (CardLayout) (cardPanel.getLayout());
                //card.show(cardPanel, "How to play");
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameController.showLeaderboard();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });

        achieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new JFrame to display the achievements panel
                JFrame achievementFrame = new JFrame("Achievements");
                achievementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                achievementFrame.setSize(400, 600);
        
                // Create an empty JPanel for achievements and wrap it in a JScrollPane
                JPanel achievementPanel = new JPanel();
                achievementPanel.setLayout(new BoxLayout(achievementPanel, BoxLayout.Y_AXIS));
                JScrollPane scrollPane = new JScrollPane(achievementPanel);
                achievementFrame.add(scrollPane);
        
                // Display the achievements window (without achievements yet)
                achievementFrame.setLocationRelativeTo(null); // Center the window on the screen
                achievementFrame.setVisible(true);
                
                
                // Fetch achievements and update the panel
                AchievementHandler achievementHandler = new AchievementHandler(networkClient, "jane_smith", null);
                
                // Call the awardAchievement method
                //TODO: I just awarded these two achievements here for demo purposes but this needs moving to elsewhere in game logic
                achievementHandler.awardAchievement("DefeatedEasyMode");
                //achievementHandler.awardAchievement("DefeatedMediumMode");
                JPanel achievementsPanel = achievementHandler.getPlayerAchievements("jane_smith", "MineSweeper");
        
                // Update the frame with the fetched achievements panel
                achievementFrame.getContentPane().remove(scrollPane);  // Remove the old empty panel
                JScrollPane newScrollPane = new JScrollPane(achievementsPanel);  // Wrap the new achievements panel in a scroll pane
                achievementFrame.getContentPane().add(newScrollPane);  // Add the updated achievements panel
                achievementFrame.revalidate();  // Revalidate the frame to apply changes
                achievementFrame.repaint();  // Repaint the frame to update the UI
            }
        });
        

        // Action listener for Dev Mode button
        devButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minesweeper.setDifficulty("medium"); // Set a default or custom difficulty
                minesweeper.changeGameState(GameState.PLAYING); // Transition to playing state
            }
        });

        // Add buttons to the button panel and adds spacing between the buttons
        buttonPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(howToPlayButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(devButton); // Add the Dev Mode button
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(achieveButton); // Add the Dev Mode button

        // Align buttons to the left within the button panel
        playButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        howToPlayButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        leaderboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        devButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        achieveButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Return button panel
        return buttonPanel;
    }

    /**
     * Creates a panel that contains a back button and a title
     * @param cardPanel
     * @param title
     * @return
     */
    private JPanel titleAndBackButtonPanel(JPanel cardPanel, String title){
        //Create a panel to contain the title and a back button
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        //Creates a back button and when clicked shows the main menu card panel
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            CardLayout card = (CardLayout) (cardPanel.getLayout());
            card.show(cardPanel, "main menu");
        });
        //Adds the button to the panel
        panel.add(backButton, BorderLayout.EAST);

        //Creates a label to display the title
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        // Get the current font of the label
        Font currentFont = label.getFont();
        // Create a new font with the same font family and style, but with a different size
        Font newFont = currentFont.deriveFont(45f); // Set the font size to 45
        // Apply the new font to the label
        label.setFont(newFont);
        // Sets preferred size of label
        label.setPreferredSize(new Dimension(600, 150));
        //Adds label to the panel
        panel.add(label, BorderLayout.CENTER);
        //Return the panel
        return panel;
    }

    /**
     * Creates the play panel, which displays some buttons, 'Easy', 'Medium', 'Hard', 'Custom'
     * @return
     */
    JPanel playPanel(JPanel cardPanel){
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        //Gets the title and back button panel
        JPanel titleAndBackButtonPanel = titleAndBackButtonPanel(cardPanel, "Play");
        //Adds panel to the main panel
        panel.add(titleAndBackButtonPanel, BorderLayout.NORTH);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Enter Your name: ");
        nameLabel.setFont(new Font(null, 0, 20));
        //nameLabel.setForeground(Color.BLACK);
        JTextField nameField = new JTextField("User");
        nameField.setFont(new Font(null, 0, 20));
        nameField.setPreferredSize(new Dimension(150, 40));
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        panel.add(namePanel, BorderLayout.SOUTH);

        //Create a button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        // Create buttons
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");
        JButton customButton = new JButton("Custom");

        // Set preferred size for buttons to make them the same width
        Dimension buttonSize = new Dimension(150, 40);
        easyButton.setMaximumSize(buttonSize);
        mediumButton.setMaximumSize(buttonSize);
        hardButton.setMaximumSize(buttonSize);
        customButton.setMaximumSize(buttonSize);

        //Updates fonts for the buttons
        Font buttonFont = new Font(easyButton.getFont().getName(), Font.PLAIN, 20);
        easyButton.setFont(buttonFont);
        mediumButton.setFont(buttonFont);
        hardButton.setFont(buttonFont);
        customButton.setFont(buttonFont);

        // Add action listeners to the buttons (optional, add your own logic here)
        easyButton.addActionListener(e -> {
            userInterface.getUser().setUserName(nameField.getText());
            difficulty = "easy";
            CardLayout card = (CardLayout) (cardPanel.getLayout());
            card.show(cardPanel, "game loading");
            countdown.decrementAndGet();
        });

        mediumButton.addActionListener(e -> {
            userInterface.getUser().setUserName(nameField.getText());
            difficulty = "medium";
            CardLayout card = (CardLayout) (cardPanel.getLayout());
            card.show(cardPanel, "game loading");
            countdown.decrementAndGet();
        });

        hardButton.addActionListener(e -> {
            userInterface.getUser().setUserName(nameField.getText());
            difficulty = "hard";
            CardLayout card = (CardLayout) (cardPanel.getLayout());
            card.show(cardPanel, "game loading");
            countdown.decrementAndGet();
        });

        customButton.addActionListener(e -> {
            userInterface.getUser().setUserName(nameField.getText());
            difficulty = minesweeper.customDifficulty();
            if(!difficulty.equals("")){
                CardLayout card = (CardLayout) (cardPanel.getLayout());
                card.show(cardPanel, "game loading");
                countdown.decrementAndGet();
            }
        });

        //Create labels to go under each button
        JLabel easyLabel = new JLabel("5 X 5 grid with 5 mines");
        JLabel mediumLabel = new JLabel("8 X 8 grid with 15 mines");
        JLabel hardLabel = new JLabel("18 X 18 grid with 40 mines");
        JLabel customLabel = new JLabel("Create a custom sized grid, with as many mines as you like");

        easyLabel.setForeground(Color.BLACK);
        mediumLabel.setForeground(Color.BLACK);
        hardLabel.setForeground(Color.BLACK);
        customLabel.setForeground(Color.BLACK);

        // Updates font for the labels
        Font labelFont = new Font(easyLabel.getFont().getName(), Font.BOLD, 16);
        easyLabel.setFont(labelFont);
        mediumLabel.setFont(labelFont);
        hardLabel.setFont(labelFont);
        customLabel.setFont(labelFont);

        easyLabel.setBackground(Color.WHITE);
        mediumLabel.setBackground(Color.WHITE);
        hardLabel.setBackground(Color.WHITE);
        customLabel.setBackground(Color.WHITE);

        easyLabel.setOpaque(true);
        mediumLabel.setOpaque(true);
        hardLabel.setOpaque(true);
        customLabel.setOpaque(true);

        // Add buttons to the panel
        buttonPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        buttonPanel.add(easyButton);
        buttonPanel.add(easyLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(mediumButton);
        buttonPanel.add(mediumLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(hardButton);
        buttonPanel.add(hardLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(customButton);
        buttonPanel.add(customLabel);

        panel.add(buttonPanel);
        return panel;
    }

    /**
     * Creates a panel to display 'New Game' or 'Load Game' buttons.
     * @return
     */
    JPanel newOrLoadGamePanel(JPanel cardPanel){
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        //Gets the title and back button panel
        JPanel titleAndBackButtonPanel = titleAndBackButtonPanel(cardPanel, "Play");
        //Adds panel to the main panel
        panel.add(titleAndBackButtonPanel, BorderLayout.NORTH);

         //Create a button panel
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
         buttonPanel.setOpaque(false);
         // Create buttons
         JButton newGameButton = new JButton("New Game");
         JButton loadGameButton = new JButton("Load Game");

         // Set preferred size for buttons to make them the same width
         Dimension buttonSize = new Dimension(150, 40);
         newGameButton.setMaximumSize(buttonSize);
         loadGameButton.setMaximumSize(buttonSize);

         //Updates fonts for the buttons
         Font buttonFont = new Font(newGameButton.getFont().getName(), Font.PLAIN, 20);
         newGameButton.setFont(buttonFont);
         loadGameButton.setFont(buttonFont);

         // Add action listeners to the buttons
         newGameButton.addActionListener(e -> {
            CardLayout card = (CardLayout) (cardPanel.getLayout());
            card.show(cardPanel, "Play");
         });

         loadGameButton.addActionListener(e -> {
            networkClient.getGameMetadata("Minesweeper").onSuccess(list -> networkClient.getMainWindow().showGames("Minesweeper", list));
         });

         //Create labels to go under each button
         JLabel newGameLabel = new JLabel("Create a new game");
         JLabel loadGameLabel = new JLabel("Load a saved game");

         newGameButton.setForeground(Color.BLACK);
         loadGameButton.setForeground(Color.BLACK);

         // Updates font for the labels
         Font labelFont = new Font(newGameLabel.getFont().getName(), Font.BOLD, 16);
         newGameLabel.setFont(labelFont);
         loadGameLabel.setFont(labelFont);

         newGameLabel.setBackground(Color.WHITE);
         loadGameLabel.setBackground(Color.WHITE);

         newGameLabel.setOpaque(true);
         loadGameLabel.setOpaque(true);

         // Add buttons to the panel
         buttonPanel.add(Box.createRigidArea(new Dimension(7, 0)));
         buttonPanel.add(newGameButton);
         buttonPanel.add(newGameLabel);
         buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
         buttonPanel.add(loadGameButton);
         buttonPanel.add(loadGameLabel);

         panel.add(buttonPanel);


        return panel;
    }

    /**
     * Creates a loading screen
     * @return
     */
    JPanel gameLoadingPanel(JPanel cardPanel){
        //Creates a new panel
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JLabel countdownLabel = new JLabel("Game Starting in:\n" + countdown, SwingConstants.CENTER);
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 40));
        // Initialize the Timer
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int currentCountdown = countdown.get();
                if (currentCountdown == 4) {
                    //Do nothing if the current count is 4
                }else{
                    if (currentCountdown > 0) {
                        countdownLabel.setText("Game Starting in: " + currentCountdown);
                        countdown.decrementAndGet();
                    } else {
                        if (timer != null) {
                            timer.cancel();
                            timer = null; // Nullify the timer after stopping
                        }
                        countdown.set(4);
                        minesweeper.setDifficulty(difficulty);  // Set the difficulty
                        minesweeper.changeGameState(GameState.PLAYING);  // Transition to playing state
                    }
                }
            }
        };
        // Timer runs every second
        timer.scheduleAtFixedRate(task, 0, 1000);

        panel.add(countdownLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a leaderboard panel
     * @return
     */
    JPanel leaderboardPanel(JPanel cardPanel){
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        //Gets the title and back button panel
        JPanel titleAndBackButtonPanel = titleAndBackButtonPanel(cardPanel, "Leaderboard");
        //Adds panel to the main panel
        panel.add(titleAndBackButtonPanel, BorderLayout.NORTH);

        //Returns the panel
        return panel;
    }

    /**
     * Displays a pop-up window which displays how to play minesweeper
     */
    private void showHowToPlay(){
        //Creates a new panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(450, 600));

        //Creates the how to play text
        String howToPlayText = "How To Play Minesweeper\n\n" + "Minesweeper is a logic-based game where the objective is to clear a grid of hidden tiles without triggering any mines.\n"
        + "The game starts with a grid of covered tiles, some of which hide mines. To play, you click on tiles using the left mouse button to reveal what's underneath. If a tile contains a number, that number"
        + " indicates how many mines are adjacent to it. Using this information, you deduce which nearby tiles are safe to click and which may contain mines."
        + "\nIf you suspect a tile hides a mine, you can flag it using the right mouse button, or if you are unsure if a tile contains a mine, press the right mouse button again which will put a '?' on the tile, "
        + "and you can come back to the tile later. \nThe game is won by either revealing all non-mine tiles or correctly flagging all the mines. "
        + "However, clicking on a mine results in a loss. Careful planning and logical thinking are key to successfully navigating the grid and avoiding the mines.";

        //Creates the JTextArea which holds the text
        JTextArea text = new JTextArea(howToPlayText);
        text.setFont(new Font("Arial", Font.BOLD, 18));
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);

        //Adds title and text to the panel
        panel.add(new JScrollPane(text), BorderLayout.CENTER);

        JOptionPane.showConfirmDialog(null, panel, "How To Play", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    //-----------------Options tab menu code-----------------//
    /**
     * This creates a popup dialog utilizing the JDialog package from Java Swing. The dialog
     * contains a tabbed pane with a main menu and a new game tab, each featuring centered
     * buttons of uniform width.
     *
     * @return The main menu dialog.
     */
    public JDialog showOptions(Runnable onCloseCallback) {

        JDialog optionsMenuDialog = new JDialog();
        //optionsMenuDialog.setTitle("");
        optionsMenuDialog.setSize(220, 350);
        optionsMenuDialog.setLocationRelativeTo(null); // Center the dialog
        optionsMenuDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Ensure it can be closed

        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.addTab("Options Menu", createMenuTabPanel(optionsMenuDialog));
        tabPanel.addTab("New Game", createNewGameTabPanel(optionsMenuDialog));

        optionsMenuDialog.add(tabPanel);
        optionsMenuDialog.setModal(true);

        // Add a WindowListener to resume the timer when the dialog is closed
        optionsMenuDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCloseCallback.run(); // Resume the timer
            }

            @Override
            public void windowClosed(WindowEvent e) {
                onCloseCallback.run(); // Resume the timer
            }
        });


        optionsMenuDialog.setVisible(true); // Make the dialog visible after adding components
        return optionsMenuDialog;
    }


    public JDialog createOptionsDialog(Runnable onCloseCallback) {
        JDialog optionsMenuDialog = new JDialog();
        optionsMenuDialog.setSize(220, 350);
        optionsMenuDialog.setLocationRelativeTo(null); // Center the dialog
        optionsMenuDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Ensure it can be closed
        return optionsMenuDialog;
    }
    
    /**
     * Creates the menu tab panel with centered buttons.
     *
     * @return The menu tab panel.
     */
    private JPanel createMenuTabPanel(JDialog optionsMenuDialog) {
        JPanel optionsMenuTabPanel = new JPanel();
        optionsMenuTabPanel.setLayout(new BoxLayout(optionsMenuTabPanel, BoxLayout.Y_AXIS));
        optionsMenuTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around edges

        JButton howToPlayButton = new JButton("How To Play");
        JButton leaderboardButton = new JButton("Leaderboard");
        JButton saveButton = new JButton("Save");
        JButton restartButton = new JButton("Restart");
        JButton mainMenuButton = new JButton("Main Menu");
        JButton exitButton = new JButton("Exit");

        // Set preferred size for buttons to make them the same width
        Dimension buttonSize = new Dimension(150, 30);
        howToPlayButton.setMaximumSize(buttonSize);
        leaderboardButton.setMaximumSize(buttonSize);
        saveButton.setMaximumSize(buttonSize);
        restartButton.setMaximumSize(buttonSize);
        mainMenuButton.setMaximumSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);

        addCenteredButton(optionsMenuTabPanel, howToPlayButton);
        addCenteredButton(optionsMenuTabPanel, leaderboardButton);
        addCenteredButton(optionsMenuTabPanel, saveButton);
        addCenteredButton(optionsMenuTabPanel, restartButton);
        addCenteredButton(optionsMenuTabPanel, mainMenuButton);
        addCenteredButton(optionsMenuTabPanel, exitButton);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the JDialog
                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(restartButton);
                if (dialog != null) {
                    dialog.dispose();  // Close the dialog
                }

                // Reset the game and restart the timer
                userInterface.resetGame();  // Ensure this method resets the game state

            }
        });

        howToPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHowToPlay();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
           public void actionPerformed(ActionEvent e) {
                String dialogMsg;
                if (minesweeper.getSavesLeft() > 0) {
                    minesweeper.saveGame();
                    optionsMenuDialog.dispose(); // Close the options menu dialog
                    dialogMsg = "The game progress is saved. You have " +
                        Integer.toString(minesweeper.getSavesLeft()) + " saves left";
                } else {
                    dialogMsg = "The game progress could not be saved. You don't have any saves left";
                }
                JOptionPane.showMessageDialog(null, dialogMsg,
                    "OK", JOptionPane.DEFAULT_OPTION);
            }
        });

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Leaderboard Under Development.. Come back later..",
                "Leaderboard", JOptionPane.DEFAULT_OPTION);;
            }
        });

        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = 0;
                if (minesweeper.getSavesLeft() > 0) {
                    // Ask to save game only if some saves are left
                    response = JOptionPane.showConfirmDialog(null,
                        "Would you like your progress saved?", "Confirm",
                        JOptionPane.YES_NO_OPTION);
                }

                if (response == JOptionPane.YES_OPTION) {
                    minesweeper.closeGame();
                    minesweeper.transitionTo(GameState.MENU); // Transition to the main menu state
                    optionsMenuDialog.dispose(); // Close the options menu dialog
                }else{
                    minesweeper.transitionTo(GameState.MENU); // Transition to the main menu state
                    optionsMenuDialog.dispose(); // Close the options menu dialog
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = 0;
                if (minesweeper.getSavesLeft() > 0) {
                    // Ask to save game only if some saves are left
                    response = JOptionPane.showConfirmDialog(null,
                        "Would you like your progress saved?", "Confirm",
                        JOptionPane.YES_NO_OPTION);
                }

                if (response == JOptionPane.YES_OPTION) {
                    // Now it only saves the game to the server.
                    minesweeper.closeGame();
                    System.exit(0);
                }else{
                    System.exit(0);
                }
            }
        });
        return optionsMenuTabPanel;
    }

    /**
     * Creates the new game tab panel with centered components.
     *
     * @return The new game tab panel.
     */
    private JPanel createNewGameTabPanel(JDialog optionsMenuDialog) {
        JPanel newGameTabPanel = new JPanel();
        newGameTabPanel.setLayout(new BoxLayout(newGameTabPanel, BoxLayout.Y_AXIS));
        newGameTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around edges
        addCenteredComponent(newGameTabPanel, new JLabel("Start a New Game"));

        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");
        JButton customButton = new JButton("Custom");

        Dimension buttonSize = new Dimension(150, 30);
        easyButton.setMaximumSize(buttonSize);
        mediumButton.setMaximumSize(buttonSize);
        hardButton.setMaximumSize(buttonSize);
        customButton.setMaximumSize(buttonSize);

        addCenteredButton(newGameTabPanel,easyButton);
        addCenteredButton(newGameTabPanel,mediumButton);
        addCenteredButton(newGameTabPanel,hardButton);
        addCenteredButton(newGameTabPanel,customButton);

        easyButton.addActionListener(e -> {
            minesweeper.setDifficulty("easy"); // Set difficulty to easy
            optionsMenuDialog.dispose(); // Close the options menu dialog
            minesweeper.startNewGame(); // Start a new game with the selected difficulty
        });

        mediumButton.addActionListener(e -> {
            minesweeper.setDifficulty("medium"); // Set difficulty to medium
            optionsMenuDialog.dispose(); // Close the options menu dialog
            minesweeper.startNewGame(); // Start a new game with the selected difficulty
        });

        hardButton.addActionListener(e -> {
            minesweeper.setDifficulty("hard"); // Set difficulty to hard
            optionsMenuDialog.dispose(); // Close the options menu dialog
            minesweeper.startNewGame(); // Start a new game with the selected difficulty
        });

        customButton.addActionListener(e -> {
            String customDifficulty = minesweeper.customDifficulty(); // Get custom difficulty settings
            minesweeper.setDifficulty(customDifficulty); // Set custom difficulty
            optionsMenuDialog.dispose(); // Close the options menu dialog
            minesweeper.startNewGame(); // Start a new game with the custom difficulty
        });

        return newGameTabPanel;
    }

    /**
     * Adds a button to a panel and centers it horizontally.
     *
     * @param panel The panel to which the button will be added.
     * @param button The button to be added.
     */
    private void addCenteredButton(JPanel panel, JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10)); // Add space between buttons
        panel.add(button);
    }

    /**
     * Adds a component to a panel and centers it horizontally.
     *
     * @param panel The panel to which the component will be added.
     * @param component The component to be added.
     */
    private void addCenteredComponent(JPanel panel, JComponent component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(component);
    }
}
