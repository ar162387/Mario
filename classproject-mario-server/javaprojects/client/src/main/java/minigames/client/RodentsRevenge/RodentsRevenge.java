package minigames.client.RodentsRevenge;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import minigames.client.Animator;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.client.Tickable;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;
import minigames.client.RodentsRevenge.TileMap;
import minigames.client.RodentsRevenge.RodentPlayer;

public class RodentsRevenge implements Tickable, GameClient {

    MinigameNetworkClient mnClient;

    /** We hold on to this because we'll need it when sending commands to the server */
    GameMetadata gm;
    /** Your name */
    public static String player;
    JPanel mainPanel;  // This will be the container panel with CardLayout
    CardLayout cardLayout;

    JTextField userCommand;
    JButton send;

    JPanel commandPanel;

    Animator animator;

    int SCREEN_WIDTH = 1600;
    int SCREEN_HEIGHT = 980;
    MouseSelectionPanel mouseSelectionPanel;
    public static String selectedMouseColor = "gray"; // Default color
    TimerScorePanel timerScorePanel;
    GamePanel gamePanel;
    TileMap tileMap;
    public static HashMap<String, RodentPlayer> rrPlayerMap= new HashMap<String, RodentPlayer>();
    public static int time;


    public RodentsRevenge() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mouseSelectionPanel = new MouseSelectionPanel(this);
        gamePanel = new GamePanel();
        commandPanel = new JPanel();

        mainPanel.add(mouseSelectionPanel, "MouseSelection");
        mainPanel.add(gamePanel, "Game");
    }


    /**
     * Sends a command to the game at the server.
     * We're sending these as
     * { "command": command }
     */

    public void sendCommand(String command) {
        JsonObject json = new JsonObject().put("command", command);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    @Override
    public void tick(Animator al, long now, long delta) {
        if (gamePanel.isVisible()) {
            gamePanel.repaint();
        }
        al.requestTick(this);
    }

    /**
     * What we do when our client is loaded into the main screen
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        this.animator = mnClient.getAnimator();

        mnClient.getMainWindow().addCenter(mainPanel);  // Add the main panel with CardLayout to the window
        mnClient.getMainWindow().pack();

        cardLayout.show(mainPanel, "MouseSelection");

        Sound.getInstance().playMusic(Sound.Type.MENU); // Play mouse selection panel music
    }

    public void startGame() {
        // Initialize the TileMap with the selected mouse color
        tileMap = new TileMap(gamePanel);
        gamePanel.tileMap = tileMap;  // Link the TileMap to the GamePanel
        animator.requestTick(this);

        gamePanel.add(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                tileMap.draw(g);
            }
        }, BorderLayout.CENTER);
        
        // Add key listener for movement
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case (KeyEvent.VK_LEFT) -> sendCommand("LEFT");
                    case (KeyEvent.VK_RIGHT) -> sendCommand("RIGHT");
                    case (KeyEvent.VK_UP) -> sendCommand("UP");
                    case (KeyEvent.VK_DOWN) -> sendCommand("DOWN");
                }
            }
        });

        cardLayout.show(mainPanel, "Game");  // Switch to the game panel after mouse selection
        mnClient.getMainWindow().pack();
        gamePanel.requestFocusInWindow();
        Sound.getInstance().playMusic(Sound.Type.GAME); // Play background music for the game
    }

    public void setMouseColor(String color) {
        selectedMouseColor = color; // Update the selected color
        sendCommand(color); // send the color information to the server
        startGame();  // Start the game after selecting the color
    }

    public static String getMouseColor() {
        return selectedMouseColor;
    }


    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;

        // We should only be receiving messages that our game understands
        // Note that this uses the -> version of case statements, not the : version
        // (which means we don't nead to say "break;" at the end of our cases)
        if (command.getValue("boardState")!=null) {
            // Translate JsonObject to java to get boardState as 2D array
            var arr = (JsonArray) command.getValue("boardState");

            int[][] resolved = new int[GamePanel.screenRow+1][GamePanel.screenCol+1];
            List<Integer> pieces = new ArrayList<>();

            int curr = 0;
            int ind = 0;

            arr.forEach((subObj)->
            {
                ((JsonArray)subObj).forEach((subSubObj)->
                {
                    pieces.add((int)subSubObj);
                });
            });

            for (int piece : pieces) {

                resolved[curr][ind++] = piece;

                if(ind%25==0){ ++curr; ind=0; }
            }

            gamePanel.setTailMap(resolved); // Set tileMap by the recieveing boardState from the server

        }
        else if(command.getValue("playerName")!=null){
            // Translate JsonObject to java to get RodentPlayer HashMap
            var arr = (JsonArray) command.getValue("playerName");
            var playerMap = new HashMap<String, RodentPlayer>();

            arr.forEach((subObj)->
            {
                var rrPlayer = ((JsonObject)subObj).mapTo(RodentPlayer.class);
                rrPlayerMap.put(rrPlayer.getX() + "|" + rrPlayer.getY(), rrPlayer); // Get RodentPlayer position from the server
                if (rrPlayer.getName().equals(player.trim())) {
                    int numOfDeath = rrPlayer.getNumOfDeath(); // Get RodentPlayer num of death from the server
                    int score = rrPlayer.getScore(); // Get RodentPlay score from the server

                    gamePanel.setNumOfDeath(numOfDeath); // Set the display of num of death
                    gamePanel.setScore(score); // Set the dislay of score
                }
            });
        }
        else if (command.getValue("timeRemain") != null) {
            // Trnaslate JsonObejct to an integer to get the remainning time
            int time = Integer.parseInt(command.getValue("timeRemain").toString());
            gamePanel.setTime(time); // Set display of the timer
        }
    }

    @Override
    public void closeGame() {
        // Nothing to do
    }

}