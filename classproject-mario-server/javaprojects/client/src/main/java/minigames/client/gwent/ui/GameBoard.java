package minigames.client.gwent.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import minigames.client.gwent.GameController;
import minigames.gwent.GameState;

/**
 * GameBoard represents the playing board for Gwent.
 */
public class GameBoard {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);
    CenterPanel centerPanel;
    LeftSidePanel leftSidePanel;
    RightSidePanel rightSidePanel;
    PassTurnPanel passTurnPanel;
    private JPanel mainPanel;
    private JFrame frame;

    /**
     * Constructor for GameBoard.
     *
     * @param gameController The controller that manages game logic.
     */
    public GameBoard(GameController gameController, GameState gameState) {
        this.centerPanel = new CenterPanel(gameController, gameState);
        this.leftSidePanel = new LeftSidePanel(gameState, gameController);
        this.rightSidePanel = new RightSidePanel(gameState.getPlayerOne(), gameState.getPlayerTwo(), gameController);

        this.passTurnPanel = new PassTurnPanel(gameController);

        initialiseBoard();
    }

    /**
     * Initialises the game board components and layout.
     */
    private void initialiseBoard() {
        logger.info("Initialising game board");

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(centerPanel.getPanel(), BorderLayout.CENTER);
        mainPanel.add(leftSidePanel.getPanel(), BorderLayout.WEST);
        mainPanel.add(rightSidePanel.getPanel(), BorderLayout.EAST);

        logger.info("Game board initialised");
    }

    /**
     * Public method for updating the board components.
     */
    public void updateBoard() {
        refreshBoard();
    }

    /**
     * Repaints the board.
     */
    private void refreshBoard() {
        logger.info("Refreshing game board");

        centerPanel.update();
        leftSidePanel.update();
        rightSidePanel.update();

        mainPanel.revalidate();
        mainPanel.repaint();

        logger.info("Game board refreshed");
    }

    /**
     * Shows the game board.
     */
    public void show() {
        logger.info("Displaying game board");

        frame = new JFrame("Gwent Game Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the game window size
        frame.setPreferredSize(new Dimension(1280, 800));

        frame.setContentPane(mainPanel);
        frame.pack();

        // Center the window on the screen
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    /**
     * Hides the game board.
     */
    public void hide() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
