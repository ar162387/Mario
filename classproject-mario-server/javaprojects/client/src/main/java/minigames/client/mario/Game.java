package minigames.client.mario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import minigames.client.*;
import org.apache.logging.log4j.*;

/**
 * The Game class handles the rendering of the game, key input events,
 * and updates to the game state based on information received from the server.
 */
public class Game extends JPanel implements KeyListener, ActionListener {

    private MarioClient marioClient;
    private Player player;
    private boolean onGround = true;
    private int score = 0;
    private int playerHealth = 0;
    private BufferedImage heartImage;

    private List<Enemy> enemies = new ArrayList<>();

    private List<Herb> herbs = new ArrayList<>();
    private BufferedImage backgroundImage; // Field to store the background image

    /**
     * Constructs the Game panel, initializes the KeyListener, and sets up rendering properties.
     *
     * @param marioClient The MarioClient instance managing server communication.
     */
    public Game(MarioClient marioClient, Player player) {
        this.marioClient = marioClient;
        this.player = player;
        setFocusable(true);
        requestFocusInWindow();
        setDoubleBuffered(true);
        addKeyListener(this);
        loadHeartImage();
        loadBackgroundImage(); // Load the background image
    }

    /**
     * Paints the game's components on the screen, including the background, player, and score.
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawPlayer(g);
        drawScore(g);
        drawHealth(g);
        drawEnemies(g);
        drawHerbs(g);
    }
    // Method to load the background image
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/Images/mario/bg.png")); // Adjust the path to your resource folder
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    public void updateEnemy(int index, int x, int y, boolean active) {
        if (index >= 0 && index < enemies.size()) {
            Enemy enemy = enemies.get(index);
            enemy.updatePosition(x, y);
            enemy.setActive(active);
        } else {
            Enemy newEnemy = new Enemy(x, y, 50, 50, active);
            enemies.add(newEnemy);
        }
    }

    public void updateHerb(int index, int x, int y, boolean active) {
        // Ensure the herb list is properly initialized and contains the herb to update
        if (index >= 0 && index < herbs.size()) {
            Herb herb = herbs.get(index);
            herb.updatePosition(x, y); // Method to update position
            herb.setActive(active); // Method to update active state
        }
        else  {
            // If the index is greater than the current size, add a new instance of Herb
            Herb newHerb = new Herb(x, y, 30, 30, active); // Example dimensions: 30x30
            herbs.add(newHerb);
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                player.setDirection("left");
                marioClient.sendCommand("move", "left", "left");
                break;

            case KeyEvent.VK_RIGHT:
                player.setDirection("right");
                marioClient.sendCommand("move", "right", "right");
                break;

            case KeyEvent.VK_UP:
                player.setDirection("up");
                marioClient.sendCommand("move", "up", "up");
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_LEFT:
                player.setDirection("idleL");
                marioClient.sendCommand("move", "idleL", "idleL");
            case KeyEvent.VK_RIGHT:
                player.setDirection("idleR");
                marioClient.sendCommand("move", "idleR", "idleR");
                break;

            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void updateScore(int newScore) {
        this.score = newScore;
        repaint();
    }

    public void updatePlayerPosition(int x, int y, boolean onGround, String direction) {
        player.updatePosition(x, y, onGround, direction);
        repaint();
    }

    private void drawBackground(Graphics g) {
        // Clear the existing background with black color
        g.setColor(Color.BLACK);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.fillRect(0, 0, getWidth(), getHeight());
        }

            g.setColor(new Color(170, 69, 19));
            g.fillRect(0, 520, getWidth(), getHeight() - 520);

    }


    private void drawPlayer(Graphics g) {
        player.draw(g);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, getWidth() - 200, 50);
    }

    private void loadHeartImage() {
        try {
            heartImage = ImageIO.read(getClass().getResourceAsStream("/images/mario/heart.png"));
        } catch (IOException e) {
            System.err.println("Error loading heart image: " + e.getMessage());
        }
    }

    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
    }
    private void drawHerbs(Graphics g)
    {
        for(Herb herb : herbs)
        {
            herb.draw(g);
        }
    }

    public void updatePlayerHealth(int health) {
        playerHealth = health;
        repaint();
    }

    private void drawHealth(Graphics g) {
        int heartWidth = heartImage.getWidth();
        int heartHeight = heartImage.getHeight();
        int startX = 10;
        int startY = 10;
        int spacing = 10;

        for (int i = 0; i < playerHealth; i++) {
            g.drawImage(heartImage, startX + (i * (heartWidth + spacing)), startY, this);
        }
    }

    public boolean isOnGround() {
        return onGround;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setScore(int score){this.score = score;}
    public int getScore(){return score;}
}
