package minigames.client.snake.ui;



import minigames.client.snake.AchievementHandler;
import minigames.client.snake.util.CollisionDetector;
import minigames.client.snake.util.ImageLoader;
import minigames.client.snake.Sound;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.IOException;
import minigames.client.MinigameNetworkClient;

/**
 * Class Name: SnakeGame
 * Created by: Algorithm Avengers
 * Last edited by: Leanne Masters
 * Date: August 19, 2024
 */

public class SnakeGame extends JPanel implements ActionListener {

    // Game constants
    static final int BOARD_WIDTH = 560;
    static final int BOARD_HEIGHT = 600;
    static final int GRID_WIDTH = 24;

    static final int GRID_HEIGHT = 14;
    static final int CELL_SIZE = 23;
    static final int PADDING = 325;
    static final int PANEL_WIDTH = BOARD_WIDTH + 2 * PADDING; // Width of the panel (board + padding)
    static final int PANEL_HEIGHT = BOARD_HEIGHT; // Height of the panel (same as board height)
    private static int DELAY = 200; // Delay for the game timer (controls speed)
    private int speedEffectMovesRemaining = 0; // Tracks the remaining moves with the speed effect

//    public static String speedEffect = "normal";

    public void setSpeedEffect(String speedEffect) {
        switch(speedEffect) {
            case "fast":
                // speed up the snake
                timer.setDelay(100);
                break;
            case "slow":
                // slow down the snake
                timer.setDelay(400);
                break;
            default:
                // normal speed
                timer.setDelay(200);
                break;
        }
    }


    final int[] X = new int[(BOARD_WIDTH * BOARD_HEIGHT) / CELL_SIZE]; // Array to store x-coordinates of the snake's
                                                                       // body parts
    final int[] Y = new int[(BOARD_WIDTH * BOARD_HEIGHT) / CELL_SIZE]; // Array to store y-coordinates of the snake's
                                                                       // body parts
    final int BODY_PARTS = 6; // Initial size of the snake
    int applesEaten; // Counter for apples eaten
    int appleX; // x-coordinate of the apple
    int appleY; // y-coordinate of the apple
    int poisonAppleX; // x-coordinate of the poisoned apple
    int poisonAppleY; // y-coordinate of the poisoned apple
    boolean running = false;
    public Timer timer;
    Random random;

    static final Image snakeEndBGImg = ImageLoader.loadImage("snake/snakeEndBG.png");
    static final Image achivementScreenImg = ImageLoader.loadImage("snake/achiev.png");

    public SnakeGraphic snake;
    public CollisionDetector collisionDetector;
    public GameBoard gameBoard;

    // References to the network client and player
    private final MinigameNetworkClient mnClient;
    private final String player;
    private JPanel achievementsPanel;

    

    /**
     * Constructor for the SnakeGame class
     * Sets up the game panel and starts the game
     *
     * @throws IOException If loading resources fails.
     */
    public SnakeGame(MinigameNetworkClient mnClient, String player) throws IOException {

        this.mnClient = mnClient; // Assign the mnClient parameter to the class field
        this.player = player; // Assign the player parameter to the class field

        snake = new SnakeGraphic(BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE, BODY_PARTS);
        collisionDetector = new CollisionDetector(snake, BOARD_WIDTH, GRID_HEIGHT, CELL_SIZE);
        random = new Random();
        gameBoard = new GameBoard( this, snake);
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        // Setting a layout manager to ensure proper sizing and positioning
        this.setLayout(new BorderLayout());
        this.add(gameBoard, BorderLayout.CENTER); // Add GameBoard to fill the center of SnakeGame panel

        this.setFocusable(true);
        this.requestFocusInWindow(); // Ensure the panel has focus
        this.addKeyListener(new SnakeKeyAdapter(snake));

        // play the Menu music
        Sound.getInstance().loop(Sound.Type.MENU);

        // Register the GameBoard as an observer of the SnakeGraphic
        // This allows the GameBoard to automatically update (repaint) when the snake
        // moves
        // snake.addObserver(observer);

        startGame(); // Start the game
    }

    /**
     * Method to start the game be spawning a new apple and starting the game timer
     */
    public void startGame() {
        // stop the Menu music
        Sound.getInstance().stopLoop(Sound.Type.MENU);

        // Pull the game background music from the Sound class
        Sound.getInstance().loop(Sound.Type.BACKGROUND_MUSIC);

        // newApple(); // Generate the first apple
        gameBoard.spawnApple();
        running = true; // Set game status to running
        timer = new Timer(DELAY, this); // Create a timer to call actionPerformed method periodically
        timer.start(); // Start the timer
    }

    public void gameOver() {
        // Pull the game over sound from the Sound class
        Sound.getInstance().play(Sound.Type.GAME_OVER);

        // stop the background music
        Sound.getInstance().stopLoop(Sound.Type.BACKGROUND_MUSIC);

        // Trigger the game over logic to get achievements from Snake.java
        if (snake != null) {
            AchievementHandler achievementHandler = new AchievementHandler(mnClient, player, this);
            achievementHandler.handleGameOver(); // Handle game over and award achievements
        }

        // Remove the game board or hide it
        if (gameBoard != null) {
            this.remove(gameBoard);
        }

        // Try to get the top-level JFrame
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (topFrame != null) {
            // Resize the window to be narrower (e.g., 600px wide)
            topFrame.setSize(600, topFrame.getHeight()); // Set the width to 600px, height remains the same
            topFrame.setLocationRelativeTo(null); // Re-center the frame on the screen
        } else {
            System.out.println("Warning: topFrame is null. Cannot resize or re-center the window.");
        }

        // Create and display the GameOverMenu
        GameOverMenu gameOverMenu = new GameOverMenu(this);
        this.add(gameOverMenu);
        // play the Menu music
        Sound.getInstance().play(Sound.Type.MENU);
        this.revalidate();
        this.repaint();

        System.out.println("Game over screen with 'Back to Menu' button should be displayed.");
    }

    public void actionPerformed(ActionEvent e) {
        if (running) {
            
            snake.move(); // Move the snake in the current direction
            gameBoard.updateScoreLabel(applesEaten);
            try {
                gameBoard.snakeMoved();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            // Check collision with normal apples
            Fruit collidedApple = collisionDetector.checkFruitCollision(gameBoard.getApples());
            if (collidedApple != null) {
                // Pull the snake eats sound from the Sound class
                Sound.getInstance().play(Sound.Type.EAT_APPLE);
                collidedApple.applyEffect(snake, this);
                gameBoard.appleEaten(collidedApple);
            }

            // Check collision with powerup fruits
            Fruit collidedPowerupFruit = collisionDetector.checkFruitCollision(gameBoard.getPowerupFruits());
            if (collidedPowerupFruit != null) {
                // Pull the power up sound from the Sound class
                Sound.getInstance().play(Sound.Type.POWER_UP);
                collidedPowerupFruit.applyEffect(snake, this);
                gameBoard.powerupFruitEaten(collidedPowerupFruit);

            }

            // Decrement the speedyMovesRemaining counter
            if (speedEffectMovesRemaining > 0) {
                speedEffectMovesRemaining--;
                if (speedEffectMovesRemaining == 0) {
                    setSpeedEffect("normal"); // Reset speed after the effect wears off
                }
            }


            // NOTE: we could probably break down the collision detection into its own
            // method - passing in the type of collision check and the object(s)

            if (collisionDetector.checkSelfCollision() || collisionDetector.checkWallCollision()) {
                // Pull the snake hit sound from the Sound class
                Sound.getInstance().play(Sound.Type.HIT);
                running = false; // Stop the game if there's a collision
                timer.stop(); // Stop the timer
                gameOver(); // Display the game over screen
            }
        }
        gameBoard.repaint(); // Repaint the panel to update the visual display of the game
    }

    public void updateAchievementsPanel(JPanel newAchievementsPanel) {
        // Remove the old achievements panel if it exists
        if (achievementsPanel != null) {
            this.remove(achievementsPanel);
        }

        // Add the new achievements panel
        achievementsPanel = newAchievementsPanel;
        this.add(achievementsPanel, BorderLayout.EAST);

        // Refresh the layout
        this.revalidate();
        this.repaint();
    }

    public boolean isRunning() {
        return running;
    }

    public int getAppleX() {
        return appleX;
    }

    public int getAppleY() {
        return appleY;
    }

    public int getPoisonAppleX() {
        return poisonAppleX;
    }

    public int getPoisonAppleY() {
        return poisonAppleY;
    }

    public int getX(int index) {
        return X[index];
    }

    public int getY(int index) {
        return Y[index];
    }

    public int getApplesEaten() {
        return this.applesEaten;
    }

    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public void setSpeedEffectMovesRemaining(int moves) {
        this.speedEffectMovesRemaining = moves;
    }

    public JPanel getAchievementsPanel() {
        return achievementsPanel;
    }
}
