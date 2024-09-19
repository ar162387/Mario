package minigames.client.mario;


import io.vertx.core.json.JsonObject;
import minigames.client.*;
import minigames.commands.CommandPackage;
import minigames.rendering.GameMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.Dimension;
import java.util.Collections;

/**
 * MarioClient represents the client-side logic for the Mario game.
 * It handles communication with the server, sending commands, and rendering the game on the client.
 */
public class MarioClient implements GameClient, Tickable {

    private MinigameNetworkClient mnClient;  // Network communication client
    private GameMetadata gm;  // Game metadata from the server
    private String player;  // Player name
    private Game gameInstance;
    private boolean ticking;
    private long last;
    Animator animator;
    private boolean end = false;

    /**
     * Constructs a new MarioClient instance and initializes the Game (UI) component.
     */
    public MarioClient() {
        // FIXME: sync the player instance what what is loaded from the server
        //Player player = new Player(50, 508, 64, 96); // Create a new player instance
        //gameInstance = new Game(this);
        animator = new Animator();
        //Dimension gameDimension = new Dimension(1250, 725);
        //gameInstance.setPreferredSize(gameDimension);;
        //System.out.println("Initialised");
    }

    @Override
    public void tick(Animator al, long now, long delta) {
        this.sendCommand("UPDATE", null, null); // send update request every tick - is this too process-intensive?
        gameInstance.repaint();
        if (this.ticking) al.requestTick(this);
    }

    /**
     * Loads the game into the client by connecting to the server and displaying the game UI.
     *
     * @param mnClient The network client used for communication with the server.
     * @param game     The metadata for the game received from the server.
     * @param player   The name of the player.
     */
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String playerName) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = playerName;
        this.ticking = true;
        this.last = System.nanoTime();

        System.out.println("Loaded game client for player: " + playerName);

        // Example initial player state (you might want to get this from the server)
   //     Player player = new Player(50, 400, 64, 96);  // Example position and dimensions
        Player player = new Player(50, 400, 64, 96);  // Example position and dimensions        
        
        // Instantiate the Game instance, passing in the player
        gameInstance = new Game(this, player);

        // Set the preferred size and add the Game UI component to the main window
        gameInstance.setPreferredSize(new Dimension(1250, 725));
        mnClient.getMainWindow().addCenter(gameInstance);
        mnClient.getMainWindow().pack(); // Adjust window size
        mnClient.getMainWindow().getFrame().setSize(1250, 725);
        mnClient.getMainWindow().centreWindow();
        gameInstance.requestFocusInWindow();  // Request focus for the game

        // Request ticks to start updating the game
        this.mnClient.getAnimator().requestTick(this);
    }


    /**
     * Sends a command to the server, such as movement or jump actions.
     *
     * @param commandType The type of command (e.g., "move", "jump").
     * @param direction   The direction for movement ("left" or "right").
     * @param strength    The strength of the action (e.g., jump strength).
     */
    public void sendCommand(String commandType, String direction, String strength) {
        // Construct a JSON object representing the command
        JsonObject json = new JsonObject();
        json.put("type", commandType);

        if (direction != null) {
            json.put("direction", direction);
        }

        if (strength != null) {
            json.put("strength", strength);
        }

        // Send the command to the server
        //System.out.println("Sending update command to server: " + json.encodePrettily());
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    //  Add a new sendPositionUpdate method for updating the player's x and y coordinates:
    public void sendPositionUpdate(int x, int y, boolean onGround) {
        // Construct a JSON object for updating position
        JsonObject json = new JsonObject();
        json.put("type", "updatePosition");
        json.put("x", x);
        json.put("y", y);
        json.put("onGround", onGround);

        // Send the position update to the server
        //System.out.println("Sending position update to server: " + json.encodePrettily());
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    /**
     * Executes the commands received from the server and updates the game state accordingly.
     *
     * @param game    The metadata for the game received from the server.
     * @param command The JSON object representing the command to be executed.
     */
    @Override
    public void execute(GameMetadata game, JsonObject command) {
        String cmd = command.getString("command");

        if ("updatePosition".equals(cmd)) {
            int x = command.getInteger("x");
            int y = command.getInteger("y");
            boolean onGround = command.getBoolean("onGround", false);
            int health = command.getInteger("health" );
            String direction = command.getString("direction");
            int score = command.getInteger("score");
            gameInstance.setScore(score);
            // Check if the Player object exists in Game, otherwise instantiate it
            if (gameInstance.getPlayer() == null) {
                // Instantiate the player based on the initial position received
      //          gameInstance.setPlayer(new Player(x, y, 32, 64));  // Example player dimensions, adjust as needed
                gameInstance.setPlayer(new Player(x, y, 32, 64));  // Example player dimensions, adjust as needed
            }

            // Update the player's position in Game
            gameInstance.updatePlayerPosition(x, y, onGround , direction);
            if(health <=3) {
                gameInstance.updatePlayerHealth(health);
            }
            // Check if the player's health is 0
            if (health == 0 && !end) {
                // Load the GameOver screen
                closeGame();
                GameOver gameOverScreen = new GameOver(mnClient);
                gameOverScreen.display();
                return; // Exit the method to avoid further processing
            }


        }
        else if ("updateEnemy".equals(cmd)) {
            int index = command.getInteger("index");
            int x = command.getInteger("x");
            int y = command.getInteger("y");
            boolean active = command.getBoolean("active");

            // Update the enemy's position and state
            gameInstance.updateEnemy(index, x, y, active);
        }
        else if ("updateHerb".equals(cmd)) {
            int index = command.getInteger("index");
            int x = command.getInteger("x");
            int y = command.getInteger("y");
            boolean active = command.getBoolean("active");

            // Update the herb's position and state
            gameInstance.updateHerb(index, x, y, active);
        }
        else if ("waveUpdate".equals(cmd))
        {
            int wave = command.getInteger("wave");
            if(wave == 1)
            {
                end = true;
                closeGame();
                YouWon WonScreen =  new YouWon(mnClient);
                WonScreen.display();
                return;
            }
        }

        // Trigger a repaint to reflect any changes to the game state
        gameInstance.repaint();
    }


    // FIXME:
    /**
     * Closes the game for the current player.
     * This method is called when the game session ends, and it handles cleanup tasks such as
     * removing the game UI from the main window and releasing any resources used during gameplay.
     */
    @Override

    public void closeGame() {
        System.out.println("Closing game for player: " + player);





        // Set the game instance and player to null to release references
        gameInstance = null;
        player = null;
        ticking = false; // Stop ticking to halt game updates

        // Additional cleanup logic can be added here (e.g., freeing resources, closing connections)
        System.out.println("Game closed successfully.");
    }

}