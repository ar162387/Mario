package minigames.client.gwent.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import minigames.client.gwent.GameController;

/**
 * MainMenu is the starting menu UI for Gwent.
 * The main menu for Gwent where a user can start a new game or exit the game.
 */
public class MainMenu {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);
    private final GameController gameController;
    private JFrame frame;
    private JPanel mainPanel;

    /**
     * Constructor for MainMenu.
     *
     * @param gameController The controller that manages game logic.
     */
    public MainMenu(GameController gameController) {
        this.gameController = gameController;
        initialiseMenu();
    }

    /**
     * Initialises the main menu components and layout.
     */
    private void initialiseMenu() {
        logger.info("Initialising main menu");

        frame = new JFrame("Gwent Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        addTitleLabel(mainPanel);
        addStartButton(mainPanel);
        addExitButton(mainPanel);

        frame.add(mainPanel);
    }

    /**
     * Adds the title label to the main menu.
     *
     * @param panel The panel to which the label is added.
     */
    private void addTitleLabel(JPanel panel) {
        JLabel titleLabel = new JLabel("Welcome to Gwent!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
    }

    /**
     * Adds the start button to the main menu.
     *
     * @param panel The panel to which the button is added.
     */
    private void addStartButton(JPanel panel) {
        JButton startButton = new JButton("Start New Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            logger.info("Starting a new game");
            hide();
            gameController.startNewGame();
        });
        panel.add(startButton);
    }

    /**
     * Adds the exit button to the main menu.
     *
     * @param panel The panel to which the button is added.
     */
    private void addExitButton(JPanel panel) {
        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);
    }

    /**
     * Displays the main menu.
     */
    public void show() {
        logger.info("Displaying Gwent main menu");
        frame.setVisible(true);
    }

    /**
     * Hides the main menu.
     */
    public void hide() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}