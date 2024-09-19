package minigames.client.snake.ui;


import javax.swing.*;

import minigames.client.snake.util.*;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class GameBoard extends JPanel implements Observer {

    public final SnakeGame snakeGame;
    public Image backgroundImage;
    static public Font customFont;

    SnakeGraphic snake;
    public Fruit currentApple;
    public Fruit currentPoisonedApple;
    public Fruit currentSpeedyApple;
    public Fruit currentSlowApple;
    public int applesEaten;
    private Random random;
    private JLabel scoreLabel;

    private static Map<String, Image> fruitImages;
    private Map<String, Image> snakeImages;



    public int snakeMoves; // Tracks how many moves the snake has made
    private List<Fruit> apples;           // List to store multiple apples
    private List<Fruit> powerupFruits;   // List to store multiple powerup fruits
    private Map<Fruit, Integer> powerupFruitsTimers; // Map to track when each powerup fruit was spawned


    /**
     * Update method from the Observer interface
     * This method is called when the subject (SnakeGraphic) notifies its observers of a change
     */
    @Override
    public void update() {
        repaint();  // Redraw the game board when notified of a change
    }


    /**
     * Constructor for the GameBoard class
     */
    public GameBoard(SnakeGame snakeGame, SnakeGraphic snake) {

        this.snakeGame = snakeGame;
        this.snake = snake;
        this.random = new Random();
        this.snakeMoves = 0;
        this.apples = new ArrayList<>();
        this.powerupFruits = new ArrayList<>();
        this.powerupFruitsTimers = new HashMap<>();

        loadResources();
        initialiseUI();
        setPreferredSize(new Dimension(SnakeGame.BOARD_WIDTH + 2 * SnakeGame.PADDING, SnakeGame.BOARD_HEIGHT));
    }

    private void loadResources() {
        fruitImages = new HashMap<>();
        snakeImages = new HashMap<>();
        loadFruitImages();
        loadSnakeImages();
        loadCustomFont();
        loadBackgroundImage();
    }

    /**
     * Loads the custom font for the game.
     * If the font fails to load Arial is used
     */
    private void loadCustomFont() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/Chailce.ttf")) {
            customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(36f); // Using a custom font
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 24); // Fallback font
        }
    }

    /**
     * Initialises the user interface components for the game board.
     * This method sets up the score label, including its font, color, and position on the panel.
     * The score label is created with a cutsom font and a specified colour, and it it positioned
     * to ensure proper alignment on the game board.
     */
    private void initialiseUI() {
        scoreLabel = new JLabel("Score: " + snakeGame.applesEaten);
        setLayout(null); // This allows absolute positioning needed for setBounds to work
        scoreLabel.setFont(customFont);
        Color customColor = new Color(176, 0, 20); // Custom RGB color
        scoreLabel.setForeground(customColor);
        int scoreLabelWidth = 250;
        int scoreLabelHeight = 170;
        int scoreLabelX = ((SnakeGame.BOARD_WIDTH + 2 * SnakeGame.PADDING - scoreLabelWidth) / 2) + 52; // Center horizontally + 40px added due to text length
        int scoreLabelY = 15; // Distance from the top of the panel
        scoreLabel.setBounds(scoreLabelX, scoreLabelY, scoreLabelWidth, scoreLabelHeight);
        add(scoreLabel);
    }


    /**
     * loads the images for the snake segments (head, body, tail).
     */
    private void loadSnakeImages(){
        snakeImages.put("headRight",ImageLoader.loadImage("snake/snakeHeadRight.png"));
        snakeImages.put("headLeft",ImageLoader.loadImage("snake/snakeHeadLeft.png"));
        snakeImages.put("headUp",ImageLoader.loadImage("snake/snakeHeadUp.png"));
        snakeImages.put("headDown", ImageLoader.loadImage("snake/snakeHeadDown.png"));
        snakeImages.put("body",ImageLoader.loadImage("snake/snakeBody.png"));
        snakeImages.put("tail",ImageLoader.loadImage("snake/snakeTail.png"));
    }

    /**
     * Loads the background image for the game board.
     */
    public void loadBackgroundImage(){
        backgroundImage = ImageLoader.loadImage("snake/snakeBackground.png");
    }

    /**
     * Loads the images for different types of fruit powerups
     */
    private void loadFruitImages() {
        fruitImages.put("apple", ImageLoader.loadAndResizeImage("/snake/appleImage.png", SnakeGame.CELL_SIZE));
        fruitImages.put("poisonApple",ImageLoader.loadAndResizeImage("/snake/poisonApple.png", SnakeGame.CELL_SIZE));
        fruitImages.put ("purpleApple",ImageLoader.loadAndResizeImage("/snake/purpleApple.png", SnakeGame.CELL_SIZE));
        fruitImages.put ("speedyApple",ImageLoader.loadAndResizeImage("/snake/speedyApple.png", SnakeGame.CELL_SIZE));
        fruitImages.put ("slowApple",ImageLoader.loadAndResizeImage("/snake/slowApple.png", SnakeGame.CELL_SIZE));
    }

    /**
     * Retrieves the image associated with a given fruit type.
     *
     * @param fruitType the type of fruit (eg "apple", "poisonApple").
     * @return the Image for the specified fruit type.
     */
    public static Image getFruitImage(String fruitType) {
        return fruitImages.get(fruitType);
    }


    /**
     * Called to repaint the game board, including the background, grid,
     * snake, and fruit.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // Draw the background image if it's loaded
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw the grid on top of the background
        drawGrid(g);

        // Draw the snake and apple on top of the grid
        drawSnake(g);

        // Draw apple
        //drawFruit(g, getCurrentApple());
        for (Fruit apple : apples) {
            drawFruit(g, apple);
        }

        for (Fruit powerupFruit : powerupFruits) {
            drawFruit(g, powerupFruit);
        }

        // Draw poisoned apple if criteria (applesEaten % 5 == 0) is met
        // if (getCurrentPoisonedApple() != null) {
        //     drawFruit(g, getCurrentPoisonedApple());
        // }
    }

    public void drawGrid(Graphics g) {
        int gridXOffset = SnakeGame.PADDING;
        int gridYOffset = 165; // Hardcoded value from original SnakeGame
    
        // Define the two colors for the checkboard pattern
        Color lightGreen = new Color(186, 218, 179); // Light green color
        Color darkGreen = new Color(152, 190, 140);    // Slightly darker green color
    
        // Loop through the grid and fill each cell with alternating colors
        for (int row = 0; row < SnakeGame.GRID_HEIGHT; row++) {
            for (int col = 0; col < SnakeGame.GRID_WIDTH; col++) {
                // Alternate between lightGreen and darkGreen based on the row and column
                if ((row + col) % 2 == 0) {
                    g.setColor(lightGreen);
                } else {
                    g.setColor(darkGreen);
                }
    
                // Fill the cell with the selected color
                g.fillRect(gridXOffset + col * SnakeGame.CELL_SIZE, gridYOffset + row * SnakeGame.CELL_SIZE,
                        SnakeGame.CELL_SIZE,SnakeGame.CELL_SIZE);
            }
        }
    
        // Now, draw the grid lines on top of the checkered background with reduced transparency (someone might tweak this to make it nicer)
        g.setColor(new Color(128, 128, 128, 10));
    
        // Draw vertical lines
        for (int i = 0; i <= SnakeGame.GRID_WIDTH; i++) {
            g.drawLine(gridXOffset + i * SnakeGame.CELL_SIZE, gridYOffset, gridXOffset + i * SnakeGame.CELL_SIZE,
                    gridYOffset + (SnakeGame.GRID_HEIGHT * SnakeGame.CELL_SIZE));
        }
    
        // Draw horizontal lines
        for (int i = 0; i <= SnakeGame.GRID_HEIGHT; i++) {
            g.drawLine(gridXOffset, gridYOffset + i * SnakeGame.CELL_SIZE, gridXOffset + SnakeGame.BOARD_WIDTH - 8,
                    gridYOffset + i * SnakeGame.CELL_SIZE);
        }
    }
    

    /**
     * Updates the score label with the new score.
     *
     * @param score The new score to display.
     */
    public void updateScoreLabel(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Draws a fruit on the game board.
     *
     * @param g The Graphics object used for painting.
     * @param fruit The Fruit to be drawn.
     */
    public void drawFruit (Graphics g, Fruit fruit) {

        int gridXOffset = SnakeGame.PADDING;
        int gridYOffset = 165;

        Image fruitImage = fruit.getImage();
        g.drawImage(fruitImage, fruit.getX()+gridXOffset, fruit.getY()+gridYOffset, this);

    }

    /**
     * Draws the snake on the game board.
     *
     * @param g The Graphics object used for painting.
     */
    public void drawSnake(Graphics g) {
        if (snakeGame.isRunning()) {
            int gridXOffset = SnakeGame.PADDING;
            int gridYOffset = 165;
            int offset = 5; // This centres the tail. Hardcoded values???? Tail graphic needs to be larger          

            // Draw the snake
            for (int i = 0; i < snake.getBodyParts(); i++) {
                int x = gridXOffset + snake.getX(i);
                int y = gridYOffset + snake.getY(i);

                // Apply offset only for the tail segment
                if (i == snake.getBodyParts() - 1) {
                    x += offset;
                    y += offset;
                }

                Image segmentImage = null;

                if (i == 0) { // Head
                    // Apply direction-based offset to the head
                    switch (snake.getDirection()) {
                        case 'U':
                            x -= 1; // Offset y-coordinate for up
                            segmentImage = snakeImages.get("headUp");
                            break;
                        case 'D':
                            x -= 1; // Offset y-coordinate for down
                            segmentImage = snakeImages.get("headDown");
                            break;
                        case 'L':
                            y -= 1; // Offset x-coordinate for left
                            segmentImage = snakeImages.get("headLeft");
                            break;
                        case 'R':
                            y -= 1; // Offset x-coordinate for right
                            segmentImage = snakeImages.get("headRight");
                            break;
                    }
                } else if (i == snake.getBodyParts() - 1) { // Tail
                    segmentImage = snakeImages.get("tail");
                } else { // Body
                    segmentImage = snakeImages.get("body");
                }

                g.drawImage(segmentImage, x, y, this);
            }
        } else {
            // If not running, delegate SnakeGame to handle the game over screen
            System.out.println("END GAME CALLED IN drawSnakeAndApple"); // Does this line of code ever get executed?
            snakeGame.gameOver();
        }

    }


    public void spawnFruit() {

        // Spawn normal apple
        int appleX, appleY;
        do {
            appleX = random.nextInt(SnakeGame.GRID_WIDTH) * SnakeGame.CELL_SIZE;
            appleY = random.nextInt(SnakeGame.GRID_HEIGHT) * SnakeGame.CELL_SIZE;
        } while (isOnSnake(appleX, appleY));
        currentApple = new Apple(appleX, appleY);
    
        repaint();
    }

    /**
     * Checks if the x or y overlap with the snake's body.
     * This method iterates through all body parts of the snake and compares their coordinates with the provided coordinates.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates overlap with any part of the snake's body else return false
     */
    private boolean isOnSnake(int x, int y) {
        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (snake.getX(i) == x && snake.getY(i) == y) {
                return true;
            }
        }
        return false;
    }

// Should this be handled here? Would it be better to be in SnakeGame where state is handled rather than in this class where rendering is handled?
    public void appleEaten(Fruit apple) {
        applesEaten++;
        apples.remove(apple); // remove apple from the list of apple objects
        spawnApple();
    }

    public void powerupFruitEaten(Fruit powerupFruit) {
        powerupFruits.remove(powerupFruit); // remove powerup fruit from the list of powerup fruits objects
    }

    public void snakeMoved() throws IOException {
        snakeMoves++;               // increment move counter when snake moves
        checkFruitSpawning();       // Check if new fruits need to be spwaned
        removeOldFruits();      // Remove old poisoned apples
    }

    public void checkFruitSpawning() throws IOException {

        // spawn apple every 20 moves
        if (this.getSnakeMoves() % 40 == 0) {
            spawnApple();
        }

        // spawn powerup fruits

        int cycleMove = this.getSnakeMoves() % 60; // cycle through every 60 moves

        if (cycleMove == 30) {
            spawnPoisonedApple();
        } else if (cycleMove == 40) {
            spawnSpeedyApple();
        } else if (cycleMove == 50) {
            spawnSlowApple();
        }
    }

    public void spawnApple() {
        int appleX, appleY;
        do {
            appleX = random.nextInt(SnakeGame.GRID_WIDTH) * SnakeGame.CELL_SIZE;
            appleY = random.nextInt(SnakeGame.GRID_HEIGHT) * SnakeGame.CELL_SIZE;
        } while (isOnSnake(appleX, appleY));

        Fruit apple = new Apple(appleX, appleY);
        apples.add(apple);
    }

    public void spawnPoisonedApple() {
        int poisonAppleX, poisonAppleY;
        do {
            poisonAppleX = random.nextInt(SnakeGame.GRID_WIDTH) * SnakeGame.CELL_SIZE;
            poisonAppleY = random.nextInt(SnakeGame.GRID_HEIGHT) * SnakeGame.CELL_SIZE;
        } while (isOnSnake(poisonAppleX, poisonAppleY));

        Fruit poisonedApple = new PoisonedApple(poisonAppleX, poisonAppleY);
        powerupFruits.add(poisonedApple);
        powerupFruitsTimers.put(poisonedApple, snakeMoves);
    }

    public void spawnSpeedyApple() throws IOException {
        int speedyAppleX, speedyAppleY;
        do {
            speedyAppleX = random.nextInt(SnakeGame.GRID_WIDTH) * SnakeGame.CELL_SIZE;
            speedyAppleY = random.nextInt(SnakeGame.GRID_HEIGHT) * SnakeGame.CELL_SIZE;
        } while (isOnSnake(speedyAppleX, speedyAppleY));
    
        // Pass 'snakeGame' to SpeedyApple so it can change the snake's speed
        Fruit speedyApple = new SpeedyApple(speedyAppleX, speedyAppleY, snakeGame);
        powerupFruits.add(speedyApple);
        powerupFruitsTimers.put(speedyApple, snakeMoves);
    }

    public void spawnSlowApple() throws IOException {
        int slowAppleX, slowAppleY;
        do {
            slowAppleX = random.nextInt(SnakeGame.GRID_WIDTH) * SnakeGame.CELL_SIZE;
            slowAppleY = random.nextInt(SnakeGame.GRID_HEIGHT) * SnakeGame.CELL_SIZE;
        } while (isOnSnake(slowAppleX, slowAppleY));

        // Pass 'snakeGame' to SlowApple so it can change the snake's speed
        Fruit slowApple = new SlowApple(slowAppleX, slowAppleY, snakeGame);
        powerupFruits.add(slowApple);
        powerupFruitsTimers.put(slowApple, snakeMoves);
    }
    

    public void removeOldFruits() {
        // Remove powerup fruits whose time has expired
            powerupFruits.removeIf(powerupFruit -> {
                int spawnTime = powerupFruitsTimers.get(powerupFruit);
                // If 30 moves have passed since the fruit was spawned, remove it
                boolean shouldRemove = (snakeMoves - spawnTime >= 30);
                if (shouldRemove) {
                    powerupFruitsTimers.remove(powerupFruit); // Also remove from the timers map
                }
                return shouldRemove;
            });
    }

    public Fruit getCurrentApple() {
        return currentApple;
    }

    public Fruit getCurrentPoisonedApple() {
        return currentPoisonedApple;
    }

    // adding getter for snake object for testing purposes
    public SnakeGraphic getSnake() {
        return this.snake;
    }

    public List<Fruit> getApples() {
        return this.apples;
    }

    public List<Fruit> getPowerupFruits() {
        return this.powerupFruits;
    }

    public int getSnakeMoves() {
        return snakeMoves;
    }

    public Map<Fruit, Integer> getPowerupFruitsTimers() {
        return powerupFruitsTimers;
    }
}
    